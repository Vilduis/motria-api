package com.motria.auth;

import com.motria.security.CurrentUser;
import com.motria.security.TokenService;
import com.motria.shared.exception.BusinessException;
import com.motria.shared.exception.ResourceNotFoundException;
import com.motria.technical.Technical;
import com.motria.technical.TechnicalRepository;
import com.motria.user.Roles;
import com.motria.user.User;
import com.motria.user.UserRepository;
import com.motria.user.UserService;
import com.motria.workshop.RegisterWorkshopRequest;
import com.motria.workshop.Workshop;
import com.motria.workshop.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final TechnicalRepository technicalRepository;
    private final UserService userService;
    private final WorkshopService workshopService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    @Transactional
    public TokenResponse registerWorkshop(RegisterWorkshopRequest request) {
        Workshop workshop = workshopService.create(request);
        User admin = userService.createAccount(request.email(), request.password(), workshop, Roles.ADMIN, false);
        return buildTokenResponse(admin);
    }

    public TokenResponse login(AuthRequests.Login request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .filter(found -> passwordEncoder.matches(request.password(), found.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!user.isActive()) {
            throw new DisabledException("La cuenta está deshabilitada");
        }
        return buildTokenResponse(user);
    }

    public MeResponse me() {
        User user = getAuthenticatedUser();
        return new MeResponse(
                user.getId(),
                user.getEmail(),
                displayNameOf(user),
                joinedAuthorities(user),
                user.isActive(),
                user.isMustChangePassword(),
                user.getWorkshop().getId(),
                user.getWorkshop().getWorkshopName());
    }

    @Transactional
    public void changePassword(AuthRequests.ChangePassword request) {
        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }
        userService.changePassword(user, request.newPassword());
    }

    private TokenResponse buildTokenResponse(User user) {
        return new TokenResponse(
                tokenService.generateToken(user),
                user.getId(),
                joinedAuthorities(user),
                user.getWorkshop().getId(),
                user.getWorkshop().getWorkshopName(),
                user.isMustChangePassword());
    }

    private User getAuthenticatedUser() {
        Long userId = currentUser.userId();
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("el usuario", userId));
    }

    /** Nombre a mostrar: el dueño para el administrador, el nombre completo para el técnico o, si no, el email. */
    private String displayNameOf(User user) {
        if (user.hasRole(Roles.ADMIN)) {
            return user.getWorkshop().getOwnerName();
        }
        return technicalRepository.findByUserId(user.getId())
                .map(Technical::fullName)
                .orElseGet(() -> user.getEmail().substring(0, user.getEmail().indexOf('@')));
    }

    private static String joinedAuthorities(User user) {
        return String.join(";", user.authorityNames());
    }
}
