package projects.springboot.expensetracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import projects.springboot.expensetracker.dto.ExpenseDTO;
import projects.springboot.expensetracker.dto.IncomeDTO;
import projects.springboot.expensetracker.entity.Category;
import projects.springboot.expensetracker.entity.Expense;
import projects.springboot.expensetracker.entity.Income;
import projects.springboot.expensetracker.entity.Profile;
import projects.springboot.expensetracker.repository.CategoryRepository;
import projects.springboot.expensetracker.repository.IncomeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeService {

    private CategoryRepository categoryRepository;
    private IncomeRepository incomeRepository;
    private ProfileService profileService;

    public IncomeService(CategoryRepository categoryRepository, IncomeRepository incomeRepository, ProfileService profileService) {
        this.categoryRepository = categoryRepository;
        this.incomeRepository = incomeRepository;
        this.profileService = profileService;
    }
    /* adds new income to db */
    public IncomeDTO addIncome(IncomeDTO dto){
        Profile profile=profileService.getCurrentProfile();
        Category category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        Income newExpense=toEntity(dto,profile,category);
        newExpense=incomeRepository.save(newExpense);
        return toDTO(newExpense);
    }

    /* Retrive all incomes for current month based on the start date and end date */

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<Income> list= incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(
                this::toDTO
        ).toList();

    }

    /*Delete Income by id for current user */
    public void deleteIncome(Long incomeId){
        Profile profile=profileService.getCurrentProfile();
        Income income=incomeRepository.findById(incomeId).orElseThrow(
                ()-> new RuntimeException("Income not found")
        );


        /* Check if the expense belongs to the logged-in user */
        if(!income.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(income);
    }

    /* get latest 5 incomes for current user */
    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        List<Income> list =incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    /*Get total incomes for current user*/
    public BigDecimal getTotalIncomeForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        BigDecimal total=incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total !=null ? total:BigDecimal.ZERO;
    }

    /* Filter Income */
    public List<IncomeDTO> filterIncome(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){

        Profile profile=profileService.getCurrentProfile();
//        keyword=keyword.toLowerCase();
        List<Income> list=incomeRepository.filterIncome(
                profile.getId(),startDate,endDate,keyword,sort
        );

        return list.stream().map(this::toDTO).toList();

    }

    public Income toEntity(IncomeDTO incomeDTO, Profile profile, Category category){
        return Income.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    public IncomeDTO toDTO(Income income){
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory()!=null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory()!=null ? income.getCategory().getName():"N/A")
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }




}
