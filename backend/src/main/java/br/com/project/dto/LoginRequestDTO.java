package br.com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequestDTO(@Schema(example = "admin") String username, @Schema(example = "123") String password) {
}
