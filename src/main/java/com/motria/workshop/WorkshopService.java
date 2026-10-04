package com.motria.workshop;

import com.motria.security.CurrentUser;
import com.motria.shared.exception.BusinessException;
import com.motria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkshopService {

    private final WorkshopRepository workshopRepository;
    private final CurrentUser currentUser;

    /** Crea el taller. La cuenta del administrador la crea {@code AuthService}. */
    @Transactional
    public Workshop create(RegisterWorkshopRequest request) {
        if (workshopRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Ya existe un taller registrado con ese email");
        }
        Workshop workshop = new Workshop();
        workshop.setWorkshopName(request.workshopName());
        workshop.setOwnerName(request.ownerName());
        workshop.setEmail(request.email());
        workshop.setPhone(request.phone());
        workshop.setAddress(request.address());
        return workshopRepository.save(workshop);
    }

    public WorkshopResponse findById(Long id) {
        return WorkshopResponse.from(getOwnWorkshop(id));
    }

    @Transactional
    public WorkshopResponse update(Long id, UpdateWorkshopRequest request) {
        Workshop workshop = getOwnWorkshop(id);
        workshop.setWorkshopName(request.workshopName());
        workshop.setOwnerName(request.ownerName());
        workshop.setPhone(request.phone());
        workshop.setAddress(request.address());
        return WorkshopResponse.from(workshop);
    }

    /** Referencia al taller del usuario actual, sin consultar la base de datos (para asignar relaciones). */
    public Workshop currentWorkshopReference() {
        return workshopRepository.getReferenceById(currentUser.workshopId());
    }

    private Workshop getOwnWorkshop(Long id) {
        if (!id.equals(currentUser.workshopId())) {
            throw new AccessDeniedException("No tienes acceso a este taller");
        }
        return workshopRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("el taller", id));
    }
}
