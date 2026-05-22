package br.com.ultimoandar.contracts.document;

import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.repository.DocumentTemplateRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

@Service
public class TemplateRenderService {

    private final DocumentTemplateRepository templateRepository;

    public TemplateRenderService(DocumentTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public String render(DocumentType type, Map<String, String> placeholders) {
        TemplateContent template = load(type);
        String rendered = template.html();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String replacement = toHtml(entry.getValue());
            rendered = rendered.replace("{{" + entry.getKey() + "}}", replacement);
        }
        return rendered.replaceAll("\\{\\{[^}]+\\}\\}", "________________");
    }

    public TemplateContent load(DocumentType type) {
        return templateRepository.findFirstByDocumentTypeAndActiveTrueOrderByUpdatedAtDesc(type)
                .map(template -> new TemplateContent(template.getName(), template.getContent()))
                .orElseGet(() -> loadFromClasspath(type));
    }

    private TemplateContent loadFromClasspath(DocumentType type) {
        String path = switch (type) {
            case LEASE_CONTRACT -> "templates/documents/lease-contract.html";
            case ADDENDUM -> "templates/documents/addendum.html";
            case TERMINATION -> "templates/documents/termination.html";
        };
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String html = StreamUtils.copyToString(resource.getInputStream(), Objects.requireNonNull(StandardCharsets.UTF_8));
            return new TemplateContent(path, html);
        } catch (IOException exception) {
            throw new BusinessException("Modelo de documento não encontrado: " + type);
        }
    }

    private String toHtml(String value) {
        String text = value == null || value.isBlank() ? "________________" : value;
        return HtmlUtils.htmlEscape(text).replace("\n", "<br/>");
    }
}
