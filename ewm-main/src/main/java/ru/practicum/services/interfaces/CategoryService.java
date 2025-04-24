package ru.practicum.services.interfaces;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
	CategoryDto createCategory(NewCategoryDto newCategoryDto);

	List<CategoryDto> getCategories(Pageable pageable);

	CategoryDto getCategory(Long id);

	void deleteCategory(Long id);

	CategoryDto updateCategory(Long id, NewCategoryDto newCategoryDto);
}