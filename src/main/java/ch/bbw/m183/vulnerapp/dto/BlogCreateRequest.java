package ch.bbw.m183.vulnerapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlogCreateRequest(
        @NotBlank
        @Size(min = 1, max = 10000)
        String title,

        @NotBlank
        @Size(min = 1, max = 10000)
        String body
) {}
