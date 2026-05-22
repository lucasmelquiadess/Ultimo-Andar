package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.dto.AuditEventDto;
import br.com.ultimoandar.contracts.entity.AuditEvent;
import br.com.ultimoandar.contracts.repository.AuditEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditService {

    private final AuditEventRepository repository;

    public AuditService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AuditEventDto> latest(int limit) {
        return latest(limit, null, null, null);
    }

    @Transactional(readOnly = true)
    public List<AuditEventDto> latest(int limit, String actor, String action, String resourceType) {
        int size = Math.max(1, Math.min(limit, 100));
        return repository.search(search(actor), search(action), search(resourceType), PageRequest.of(0, size)).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, String resourceType, Object resourceId, String details) {
        recordAs(currentActor(), action, resourceType, resourceId, details);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAs(String actor, String action, String resourceType, Object resourceId, String details) {
        AuditEvent event = new AuditEvent();
        event.setActor(actor == null || actor.isBlank() ? "sistema" : actor);
        event.setAction(action);
        event.setResourceType(resourceType);
        event.setResourceId(resourceId == null ? null : resourceId.toString());
        event.setDetails(details);
        event.setIpAddress(currentIp());
        repository.save(event);
    }

    public String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "sistema";
        }
        return authentication.getName();
    }

    private AuditEventDto toDto(AuditEvent event) {
        return new AuditEventDto(
                event.getId(),
                event.getCreatedAt(),
                event.getActor(),
                event.getAction(),
                event.getResourceType(),
                event.getResourceId(),
                event.getDetails(),
                event.getIpAddress()
        );
    }

    private String currentIp() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return null;
    }

    private String search(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
