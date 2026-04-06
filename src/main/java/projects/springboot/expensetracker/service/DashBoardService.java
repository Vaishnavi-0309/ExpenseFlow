package projects.springboot.expensetracker.service;

import org.springframework.stereotype.Service;
import projects.springboot.expensetracker.dto.ExpenseDTO;
import projects.springboot.expensetracker.dto.IncomeDTO;
import projects.springboot.expensetracker.dto.RecentTransactionDTO;
import projects.springboot.expensetracker.entity.Profile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Stream.concat;


@Service
public class DashBoardService {

    private IncomeService incomeService;
    private ExpenseService expenseService;
    private ProfileService profileService;

    public DashBoardService(IncomeService incomeService, ExpenseService expenseService, ProfileService profileService) {
        this.incomeService = incomeService;
        this.expenseService = expenseService;
        this.profileService = profileService;
    }

    public Map<String,Object> getDashBoardData(){
        Profile profile=profileService.getCurrentProfile();
        Map<String,Object> dashboardDtls=new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses=expenseService.getLatest5ExpensesForCurrentUser();
        List<RecentTransactionDTO> recentTransactions =concat(latestIncomes.stream().map(
                income -> RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .type("income")
                        .build()),
                latestExpenses.stream().map(
                        expense -> RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("expense")
                                .build()
                )).sorted((a,b) ->{
                    int cmp=b.getDate().compareTo(a.getDate());
                    if(cmp==0 && a.getCreatedAt()!=null && b.getCreatedAt()!=null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
        }).toList();

        dashboardDtls.put("totalBalance",
                incomeService.getTotalIncomeForCurrentUser().
                        subtract(expenseService.getTotalExpenseForCurrentUser()));
        dashboardDtls.put("totalIncome",incomeService.getTotalIncomeForCurrentUser());
        dashboardDtls.put("totalExpense",expenseService.getTotalExpenseForCurrentUser());
        dashboardDtls.put("recent5Expense",latestExpenses);
        dashboardDtls.put("recent5Incomes",latestIncomes);
        dashboardDtls.put("recentTransactions",recentTransactions);
        return dashboardDtls;
    }


}
