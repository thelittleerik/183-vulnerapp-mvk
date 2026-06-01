package ch.bbw.m183.vulnerapp.datamodel;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "blogs")
public class BlogEntity {

	@Id
	UUID id;

	@Column
	@CreationTimestamp
	LocalDateTime createdAt;

	@Column(columnDefinition = "text")
	@NotBlank
	@Size(min = 12, max = 10000)
	String title;

	@Column(columnDefinition = "text")
	@NotBlank
	@Size(min = 12, max = 10000)
	String body;
}
