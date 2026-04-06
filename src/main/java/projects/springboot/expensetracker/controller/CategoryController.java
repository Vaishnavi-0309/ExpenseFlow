package projects.springboot.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projects.springboot.expensetracker.dto.CategoryDTO;
import projects.springboot.expensetracker.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory= categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping("/all/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories(){
        List<CategoryDTO> categories=categoryService.getCategoriesForUser();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable String type){
        List<CategoryDTO> categories=categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){
        try{
            CategoryDTO updatedCategory=categoryService.updateCategory(categoryId,categoryDTO);
            return ResponseEntity.ok(updatedCategory);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
}
