package projects.springboot.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projects.springboot.expensetracker.dto.ExpenseDTO;
import projects.springboot.expensetracker.dto.IncomeDTO;
import projects.springboot.expensetracker.service.IncomeService;

import java.util.List;

@RestController
@RequestMapping
public class IncomeController {

    private IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping("/incomes")
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto){
        IncomeDTO saved = incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @GetMapping("/incomes/currentMonth")
    public ResponseEntity<List<IncomeDTO>> getIncomes(){
        List<IncomeDTO> incomes=incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/income/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}
