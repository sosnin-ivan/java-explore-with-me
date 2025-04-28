package ru.practicum.controllers.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.services.interfaces.CategoryService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("publicCategoriesController")
@RequestMapping("/categories")
public class CategoriesController {
	private final CategoryService categoryService;

	@GetMapping
	public ResponseEntity<List<CategoryDto>> getCategories(
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Pub/CategoriesController.getCategories: from: {}, size: {}", from, size);
		List<CategoryDto> categories = categoryService.getCategories(PageRequest.of(from / size, size));
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> getCategory(
			@PathVariable @Positive Long categoryId
	) {
		log.info("Pub/CategoriesController.getCategory: categoryId: {}", categoryId);
		CategoryDto categoryDto = categoryService.getCategory(categoryId);
		return new ResponseEntity<>(categoryDto, HttpStatus.OK);
	}
}