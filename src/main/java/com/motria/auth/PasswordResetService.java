package com.motria.auth;

import com.motria.config.AppProperties;
import com.motria.notification.EmailService;
import com.motria.shared.exception.BusinessException;
import com.motria.user.User;
import com.motria.user.UserRepository;
import com.motria.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(30);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final AppProperties properties;
    private final Clock clock;

    /**
     * Envía el enlace de recuperación. Si el email no existe no se informa al cliente,
     * para no revelar qué cuentas están registradas.
     */
    @Transactional
    public void requestReset(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).filter(User::isActive).orElse(null);
        if (user == null) {
            log.info("Solicitud de recuperación para un email inexistente o inactivo");
            return;
        }

        tokenRepository.invalidateAllForUser(user.getId());

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(generateSecureToken());
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now(clock).plus(TOKEN_VALIDITY));
        tokenRepository.save(resetToken);

        emailService.sendPasswordReset(
                user.getEmail(),
                user.getWorkshop().getOwnerName(),
                buildResetUrl(resetToken.getToken()),
                TOKEN_VALIDITY.toMinutes());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("El enlace no es válido"));
        if (resetToken.isUsed()) {
            throw new BusinessException("Este enlace ya fue utilizado");
        }
        if (resetToken.isExpired(LocalDateTime.now(clock))) {
            throw new BusinessException("El enlace ha expirado, solicita uno nuevo");
        }

        userService.changePassword(resetToken.getUser(), newPassword);
        resetToken.setUsed(true);
        log.info("Contraseña restablecida para el usuario {}", resetToken.getUser().getId());
    }

    private String buildResetUrl(String token) {
        return UriComponentsBuilder.fromUriString(properties.frontendUrl())
                .path("/reset-password")
                .queryParam("token", token)
                .toUriString();
    }

    private static String generateSecureToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
