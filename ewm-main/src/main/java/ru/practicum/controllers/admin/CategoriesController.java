package ru.practicum.controllers.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.services.interfaces.CategoryService;

@Slf4j
@RequiredArgsConstructor
@RestController("adminCategoriesController")
@RequestMapping("/admin/categories")
public class CategoriesController {
	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryDto> createCategory(
			@RequestBody @Valid NewCategoryDto newCategoryDto
	) {
		log.info("Admin/CategoriesController.createCategory: newCategoryDto: {}", newCategoryDto);
		CategoryDto createdCategory = categoryService.createCategory(newCategoryDto);
		return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(
			@PathVariable @Positive Long id
	) {
		log.info("Admin/CategoriesController.deleteCategory: id: {}", id);
		categoryService.deleteCategory(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<CategoryDto> updateCategory(
			@RequestBody @Valid NewCategoryDto newCategoryDTO,
			@PathVariable @Positive Long id
	) {
		log.info("Admin/CategoriesController.updateCategory: id: {}, newCategoryDTO: {}", id, newCategoryDTO);
		CategoryDto updatedCategory = categoryService.updateCategory(id, newCategoryDTO);
		return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
	}
}