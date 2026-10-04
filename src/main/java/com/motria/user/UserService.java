package com.motria.user;

import com.motria.security.CurrentUser;
import com.motria.shared.exception.BusinessException;
import com.motria.shared.exception.ResourceNotFoundException;
import com.motria.workshop.Workshop;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    @Transactional
    public User createAccount(String email, String rawPassword, Workshop workshop, String role, boolean mustChangePassword) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Ya existe un usuario con ese email");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setMustChangePassword(mustChangePassword);
        user.setWorkshop(workshop);
        user.getAuthorities().add(findAuthority(role));
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, String newRawPassword) {
        user.setPassword(passwordEncoder.encode(newRawPassword));
        user.setMustChangePassword(false);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAllByWorkshopIdOrderByEmail(currentUser.workshopId()).stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(getInCurrentWorkshop(id));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = getInCurrentWorkshop(id);

        boolean emailChanged = !user.getEmail().equalsIgnoreCase(request.email());
        if (emailChanged && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Ya existe un usuario con ese email");
        }
        user.setEmail(request.email());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }
        return UserResponse.from(user);
    }

    @Transactional
    public void delete(Long id) {
        if (id.equals(currentUser.userId())) {
            throw new BusinessException("No puedes eliminar tu propia cuenta");
        }
        userRepository.delete(getInCurrentWorkshop(id));
    }

    private User getInCurrentWorkshop(Long id) {
        return userRepository.findByIdAndWorkshopId(id, currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("el usuario", id));
    }

    private Authority findAuthority(String role) {
        return authorityRepository.findByName(role)
                .orElseThrow(() -> new IllegalStateException("El rol " + role + " no existe. ¿Se ejecutaron las migraciones?"));
    }
}
