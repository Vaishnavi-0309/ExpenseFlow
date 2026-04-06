package projects.springboot.expensetracker.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import projects.springboot.expensetracker.dto.CategoryDTO;
import projects.springboot.expensetracker.entity.Category;
import projects.springboot.expensetracker.entity.Profile;
import projects.springboot.expensetracker.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    private ProfileService profileService;
    private CategoryRepository categoryRepository;

    public CategoryService(ProfileService profileService,CategoryRepository categoryRepository) {
        this.profileService = profileService;
        this.categoryRepository=categoryRepository;
    }


    public CategoryDTO toDTO(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .profileId(category.getProfile()!=null ? category.getProfile().getId() : null)
                .icon(category.getIcon())
                .createdAt(category.getCreatedAtTime())
                .updatedAt(category.getUpdatedAtTime())
                .type(category.getType())
                .build();
    }

    public Category toEntity(CategoryDTO categoryDTO, Profile profile){
        return Category.builder()
                .name(categoryDTO.getName().toLowerCase())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType().toLowerCase())
                .build();
    }

    /* Save category */
    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        Profile profile=profileService.getCurrentProfile();
        boolean isPresent=categoryRepository.existsByNameAndProfileId(categoryDTO.getName().toLowerCase(), profile.getId());
        if(isPresent){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Category with this name already exists");
        }
        Category newCategory=toEntity(categoryDTO,profile);
        newCategory=categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    /* get categories for current user */

    public List<CategoryDTO> getCategoriesForUser(){
        Profile profile=profileService.getCurrentProfile();
        List<Category> categories=categoryRepository.findByProfileId(profile.getId());
        return categories.stream()
                .map(this::toDTO).toList();
    }

    /* Get categories for current user by type */
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        Profile profile=profileService.getCurrentProfile();
        List<Category> categories=categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    /* update categories */
    public CategoryDTO updateCategory(Long categoryId,CategoryDTO categoryDTO){
        Profile profile=profileService.getCurrentProfile();
        Category updateCategory=categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category with the given category Id doesnt exists for the current user"));
        updateCategory.setId(categoryId);
        updateCategory.setName(categoryDTO.getName());
        updateCategory.setType(categoryDTO.getType());
        updateCategory.setIcon(categoryDTO.getIcon());
        categoryRepository.save(updateCategory);
        return toDTO(updateCategory);
    }
}
