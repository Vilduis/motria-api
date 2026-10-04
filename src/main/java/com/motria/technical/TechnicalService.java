package com.motria.technical;

import com.motria.notification.EmailService;
import com.motria.security.CurrentUser;
import com.motria.serviceorder.ServiceOrderRepository;
import com.motria.shared.exception.BusinessException;
import com.motria.shared.exception.ResourceNotFoundException;
import com.motria.user.Roles;
import com.motria.user.TemporaryPasswordGenerator;
import com.motria.user.User;
import com.motria.user.UserRepository;
import com.motria.user.UserService;
import com.motria.workshop.Workshop;
import com.motria.workshop.WorkshopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechnicalService {

    private final TechnicalRepository technicalRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final WorkshopRepository workshopRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TemporaryPasswordGenerator passwordGenerator;
    private final EmailService emailService;
    private final CurrentUser currentUser;

    public List<TechnicalResponse> findAll() {
        return technicalRepository.findAllByWorkshopIdOrderByName(currentUser.workshopId()).stream()
                .map(TechnicalResponse::from)
                .toList();
    }

    public TechnicalResponse findById(Long id) {
        return TechnicalResponse.from(getInCurrentWorkshop(id));
    }

    /**
     * Crea el técnico y su cuenta, y le envía la contraseña temporal por correo.
     * Si el correo falla, la transacción se revierte: no quedan técnicos sin acceso.
     */
    @Transactional
    public TechnicalResponse create(CreateTechnicalRequest request) {
        Workshop workshop = workshopRepository.findById(currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("el taller", currentUser.workshopId()));

        String temporaryPassword = passwordGenerator.generate();
        User account = userService.createAccount(request.email(), temporaryPassword, workshop, Roles.TECHNICAL, true);

        Technical technical = new Technical();
        technical.setName(request.name());
        technical.setLastName(request.lastName());
        technical.setSpecialty(request.specialty());
        technical.setUser(account);
        technical.setWorkshop(workshop);
        technicalRepository.save(technical);

        emailService.sendTemporaryPassword(request.email(), technical.fullName(), temporaryPassword, workshop.getWorkshopName());
        return TechnicalResponse.from(technical);
    }

    @Transactional
    public TechnicalResponse update(Long id, UpdateTechnicalRequest request) {
        Technical technical = getInCurrentWorkshop(id);
        technical.setName(request.name());
        technical.setLastName(request.lastName());
        technical.setSpecialty(request.specialty());
        return TechnicalResponse.from(technical);
    }

    /** Elimina el técnico y también su cuenta, para que no pueda seguir iniciando sesión. */
    @Transactional
    public void delete(Long id) {
        Technical technical = getInCurrentWorkshop(id);
        if (serviceOrderRepository.existsByTechnicalId(id)) {
            throw new BusinessException(
                    "No se puede eliminar el técnico porque tiene órdenes asignadas. Puedes desactivar su cuenta en su lugar");
        }
        User account = technical.getUser();
        technicalRepository.delete(technical);
        userRepository.delete(account);
    }

    /** Busca un técnico del taller actual. Lo usan las órdenes de servicio y el dashboard. */
    public Technical getInCurrentWorkshop(Long id) {
        return technicalRepository.findByIdAndWorkshopId(id, currentUser.workshopId())
                .orElseThrow(() -> ResourceNotFoundException.of("el técnico", id));
    }
}
