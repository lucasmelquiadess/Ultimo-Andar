package br.com.ultimoandar.contracts.document;

import br.com.ultimoandar.contracts.exception.BusinessException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

@Service
public class PdfGeneratorService {

    public byte[] generate(String html) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();
            return output.toByteArray();
        } catch (Exception exception) {
            throw new BusinessException("Não foi possível gerar o PDF.");
        }
    }
}
