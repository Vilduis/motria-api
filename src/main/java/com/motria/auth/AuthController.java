package com.motria.auth;

import com.motria.workshop.RegisterWorkshopRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register-workshop")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registerWorkshop(@Valid @RequestBody RegisterWorkshopRequest request) {
        return authService.registerWorkshop(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody AuthRequests.Login request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me() {
        return authService.me();
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody AuthRequests.ChangePassword request) {
        authService.changePassword(request);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@Valid @RequestBody AuthRequests.ForgotPassword request) {
        passwordResetService.requestReset(request.email());
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody AuthRequests.ResetPassword request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
    }
}
