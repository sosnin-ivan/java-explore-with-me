package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.models.Category;

@Component
public class CategoryMapper {
	public static Category toCategory(NewCategoryDto newCategoryDto) {
		if (newCategoryDto == null) {
			return null;
		}
		return Category.builder()
				.name(newCategoryDto.getName())
				.build();
	}

	public static CategoryDto toCategoryDto(Category category) {
		if (category == null) {
			return null;
		}
		return CategoryDto.builder()
				.id(category.getId())
				.name(category.getName())
				.build();
	}
}