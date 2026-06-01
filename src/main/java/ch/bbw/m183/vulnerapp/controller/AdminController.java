package ch.bbw.m183.vulnerapp.controller;

import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.dto.CreateUserRequest;
import ch.bbw.m183.vulnerapp.dto.UserView;
import ch.bbw.m183.vulnerapp.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@PostMapping("/create")
	public UserView createUser(@Valid @RequestBody CreateUserRequest req) {
	 	var entity = new UserEntity()
				.setUsername(req.username())
				.setFullname(req.fullname())
				.setPassword(req.password())
				.setRole(req.role());
		 return UserView.from(adminService.createUser(entity));
	}

	@GetMapping("/users")
	public Page<UserView> getUsers(Pageable pageable) {
		return adminService.getUsers(pageable).map(UserView::from);
	}

	@DeleteMapping("/delete/{username}")
	public void deleteUser(@PathVariable String username) {
		adminService.deleteUser(username);
	}
}
