package ru.practicum.controllers.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.services.interfaces.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("adminUsersController")
@RequestMapping("/admin/users")
public class UsersController {
	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserDto> createUser(
			@RequestBody @Valid NewUserRequest newUserRequest
	) {
		log.info("Admin/UserController.createUser: newUserRequest: {}", newUserRequest);
		UserDto createdUser = userService.createUser(newUserRequest);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(
			@PathVariable Long id
	) {
		log.info("Admin/UserController.deleteUser: id: {}", id);
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping
	public ResponseEntity<List<UserDto>> getUsers(
			@RequestParam List<Long> ids,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Admin/UserController.getUsers: ids: {}, from: {}, size: {}", ids, from, size);
		List<UserDto> users = userService.getUsers(ids, PageRequest.of(from / size, size));
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
}