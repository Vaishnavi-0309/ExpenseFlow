package projects.springboot.expensetracker.controller;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projects.springboot.expensetracker.dto.ExpenseDTO;
import projects.springboot.expensetracker.dto.FilterDTO;
import projects.springboot.expensetracker.dto.IncomeDTO;
import projects.springboot.expensetracker.service.ExpenseService;
import projects.springboot.expensetracker.service.IncomeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
public class FilterController {

    private ExpenseService expenseService;
    private IncomeService incomeService;

    public FilterController(ExpenseService expenseService, IncomeService incomeService) {
        this.expenseService = expenseService;
        this.incomeService = incomeService;
    }

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO){
        // preparing the data or validating
        LocalDate startDate=filterDTO.getStartDate() ;
        LocalDate endDate=filterDTO.getEndDate()!=null ? filterDTO.getEndDate(): LocalDate.now();
        String keyword=filterDTO.getKeyword()!=null ? filterDTO.getKeyword() : "";
        String sortField=filterDTO.getSortField()!=null ? filterDTO.getSortField():"date";
        Sort.Direction direction="desc".equalsIgnoreCase(filterDTO.getSortOrder()) ? Sort.Direction.DESC:Sort.Direction.ASC;
        Sort sort=Sort.by(direction,sortField);
        if("income".equals(filterDTO.getType())){
            List<IncomeDTO> income=incomeService.filterIncome(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(income);
        }else if("expense".equalsIgnoreCase(filterDTO.getType())){
            List<ExpenseDTO> expense=expenseService.filterExpenses(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(expense);
        }
        return ResponseEntity.badRequest().body("Invalid type.Must be 'income or 'expense'");

    }
}
