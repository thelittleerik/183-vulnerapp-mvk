package ch.bbw.m183.vulnerapp.datamodel;

import ch.bbw.m183.vulnerapp.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@NotBlank
	@Size(min = 3, max = 50)
	String username;

	@Column
	@NotBlank
	@Size(min = 3, max = 100)
	String fullname;

	@Column
	@NotBlank
	@Size(min = 12, max = 100)
	String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	Role role;
}
