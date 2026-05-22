package br.com.ultimoandar.contracts.security;

import br.com.ultimoandar.contracts.config.SecurityProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final SecurityProperties properties;
    private final ObjectMapper objectMapper;
    private final String jwtSecret;

    public JwtService(SecurityProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.jwtSecret = SecretResolver.resolve("APP_SECURITY_JWT_SECRET", properties.jwtSecret());
    }

    public TokenIssue issue(String username) {
        long expiresAt = Instant.now().plusSeconds(properties.tokenMinutes() * 60).getEpochSecond();
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", username);
        payload.put("iat", Instant.now().getEpochSecond());
        payload.put("exp", expiresAt);

        String headerPart = encodeJson(header);
        String payloadPart = encodeJson(payload);
        String signaturePart = sign(headerPart + "." + payloadPart);
        return new TokenIssue(headerPart + "." + payloadPart + "." + signaturePart, expiresAt);
    }

    public Optional<String> username(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }
            String signed = parts[0] + "." + parts[1];
            if (!MessageDigest.isEqual(sign(signed).getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }
            Map<String, Object> payload = objectMapper.readValue(DECODER.decode(parts[1]), MAP_TYPE);
            Number exp = (Number) payload.get("exp");
            if (exp == null || exp.longValue() <= Instant.now().getEpochSecond()) {
                return Optional.empty();
            }
            Object sub = payload.get("sub");
            return sub == null ? Optional.empty() : Optional.of(sub.toString());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private String encodeJson(Map<String, Object> data) {
        try {
            return ENCODER.encodeToString(objectMapper.writeValueAsBytes(data));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível criar o token de acesso.", exception);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível assinar o token de acesso.", exception);
        }
    }

    public record TokenIssue(String token, long expiresAt) {
    }
}
