package com.motria.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

/**
 * Propiedades propias de la aplicación (prefijo {@code app} en application.yaml).
 * Si falta un valor obligatorio, la aplicación no arranca y lo indica en el log.
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @Valid @NotNull Jwt jwt,
        @Valid @NotNull Mail mail,
        @NotBlank String frontendUrl,
        @Valid @NotNull Cors cors
) {

    /** @param secret clave HMAC en Base64 (mínimo 32 bytes decodificados). */
    public record Jwt(@NotBlank String secret, @NotNull Duration expiration) {
    }

    public record Mail(@NotBlank String from, @NotBlank String fromName) {
    }

    public record Cors(@NotEmpty List<String> allowedOrigins) {
    }
}
