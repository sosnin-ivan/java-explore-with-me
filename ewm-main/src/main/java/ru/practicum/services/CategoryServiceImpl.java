package ru.practicum.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.models.Category;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.services.interfaces.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
	CategoryRepository categoryRepository;

	@Override
	@Transactional
	public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
		Category createdCategory = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
		return CategoryMapper.toCategoryDto(createdCategory);
	}

	@Override
	public List<CategoryDto> getCategories(Pageable pageable) {
		return categoryRepository.findAll(pageable).stream()
				.map(CategoryMapper::toCategoryDto)
				.collect(Collectors.toList());
	}

	@Override
	public CategoryDto getCategory(Long id) {
		Category category = findCategory(id);
		return CategoryMapper.toCategoryDto(category);
	}

	@Override
	@Transactional
	public CategoryDto updateCategory(Long id, NewCategoryDto newCategoryDto) {
		Category category = findCategory(id);
		category.setName(newCategoryDto.getName());
		Category updatedCategory = categoryRepository.save(category);
		return CategoryMapper.toCategoryDto(updatedCategory);
	}

	@Override
	@Transactional
	public void deleteCategory(Long id) {
		findCategory(id);
		categoryRepository.deleteById(id);
	}

	private Category findCategory(Long id) {
		return categoryRepository.findById(id).orElseThrow(() ->
				new NotFoundException(String.format("Категория c id %d не найдена", id)));
	}
}