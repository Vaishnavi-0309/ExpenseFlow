package projects.springboot.expensetracker.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import projects.springboot.expensetracker.dto.ExpenseDTO;
import projects.springboot.expensetracker.entity.Category;
import projects.springboot.expensetracker.entity.Expense;
import projects.springboot.expensetracker.entity.Profile;
import projects.springboot.expensetracker.repository.CategoryRepository;
import projects.springboot.expensetracker.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private CategoryRepository categoryRepository;
    private ExpenseRepository expenseRepository;
    private ProfileService profileService;

    public ExpenseService(CategoryRepository categoryRepository, ExpenseRepository expenseRepository,ProfileService profileService) {
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.profileService=profileService;
    }

    /* adds new expense to db */
    public ExpenseDTO addExpense(ExpenseDTO dto){
        Profile profile=profileService.getCurrentProfile();
        Category category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        Expense newExpense=toEntity(dto,profile,category);
        newExpense=expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    /* Retrive all expenses for current month based on the start date and end date */

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> list= expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(
                this::toDTO
        ).toList();

    }

    /*Delete expense by id for current user */
    public void deleteExpense(Long expenseId){
        Profile profile=profileService.getCurrentProfile();
        Expense expense=expenseRepository.findById(expenseId).orElseThrow(
                ()-> new RuntimeException("Expense not found")
        );
        /* Check if the expense belongs to the logged-in user */
        if(!expense.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(expense);
    }

    /* get latest 5 expenses for current user */
public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
    Profile profile=profileService.getCurrentProfile();
    List<Expense> list =expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
    return list.stream().map(this::toDTO).toList();
}

/*Get total expenses for current user*/
    public BigDecimal getTotalExpenseForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        BigDecimal total=expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total !=null ? total:BigDecimal.ZERO;
    }

    /* Filter Expenses */
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        Profile profile=profileService.getCurrentProfile();
        List<Expense> list=expenseRepository.filterExpense(
                profile.getId(),startDate,endDate,keyword,sort
        );

        return list.stream().map(this::toDTO).toList();

    }

    /*Notifications */
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
        List<Expense> list=expenseRepository.findByProfileIdAndDate(profileId,date);
        return list.stream().map(this::toDTO).toList();
    }
    public Expense toEntity(ExpenseDTO expenseDTO, Profile profile, Category category){
        return Expense.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    public ExpenseDTO toDTO(Expense expense){
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory()!=null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory()!=null ? expense.getCategory().getName():"N/A")
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
