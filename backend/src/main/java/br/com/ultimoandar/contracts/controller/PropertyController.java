package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.PropertyDto;
import br.com.ultimoandar.contracts.dto.PropertyPhotoDto;
import br.com.ultimoandar.contracts.entity.enums.PropertyStatus;
import br.com.ultimoandar.contracts.service.PropertyService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @GetMapping
    public List<PropertyDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) UUID ownerId
    ) {
        return service.list(search, status, city, ownerId);
    }

    @GetMapping("/{id}")
    public PropertyDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyDto create(@Valid @RequestBody PropertyDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public PropertyDto update(@PathVariable UUID id, @Valid @RequestBody PropertyDto dto) {
        return service.update(id, dto);
    }

    @PostMapping("/{id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyPhotoDto uploadPhoto(@PathVariable UUID id, @RequestParam MultipartFile file) {
        return service.uploadPhoto(id, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        service.deactivate(id);
    }
}
