package br.com.ultimoandar.contracts.storage;

import br.com.ultimoandar.contracts.config.StorageProperties;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.util.TextNormalizer;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private final Path root;

    public StorageService(StorageProperties properties) {
        this.root = Path.of(properties.root()).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(root);
    }

    public StoredFile storeMultipart(String folder, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("O arquivo enviado está vazio.");
        }
        String original = file.getOriginalFilename() == null ? "arquivo" : file.getOriginalFilename();
        String extension = extension(original);
        String fileName = UUID.randomUUID() + "-" + TextNormalizer.safeFileName(original);
        if (!fileName.endsWith(extension)) {
            fileName = fileName + extension;
        }
        Path directory = directory(folder);
        Path destination = directory.resolve(fileName).normalize();
        assertInsideRoot(destination);
        try {
            byte[] bytes = file.getBytes();
            Files.write(destination, FileCrypto.encrypt(bytes));
        } catch (IOException exception) {
            throw new BusinessException("Não foi possível armazenar o arquivo enviado.");
        }
        return new StoredFile(fileName, root.relativize(destination).toString(), file.getContentType(), file.getSize());
    }

    public StoredFile storePdf(String prefix, String baseFileName, byte[] bytes) {
        String folder = "documents/" + LocalDate.now().getYear() + "/" + prefix.toLowerCase();
        String fileName = TextNormalizer.safeFileName(baseFileName) + ".pdf";
        Path directory = directory(folder);
        Path destination = directory.resolve(fileName).normalize();
        assertInsideRoot(destination);
        try {
            Files.write(destination, FileCrypto.encrypt(bytes));
        } catch (IOException exception) {
            throw new BusinessException("Não foi possível salvar o PDF gerado.");
        }
        return new StoredFile(fileName, root.relativize(destination).toString(), "application/pdf", bytes.length);
    }

    public Resource load(String storagePath) {
        Path path = root.resolve(storagePath).normalize();
        assertInsideRoot(path);
        try {
            if (!Files.exists(path) || !Files.isReadable(path)) {
                throw new BusinessException("Arquivo não encontrado no storage local.");
            }
            byte[] bytes = FileCrypto.decryptIfNeeded(Files.readAllBytes(path));
            return new ByteArrayResource(bytes);
        } catch (IOException exception) {
            throw new BusinessException("Não foi possível abrir o arquivo solicitado.");
        }
    }

    private Path directory(String folder) {
        Path directory = root.resolve(folder).normalize();
        assertInsideRoot(directory);
        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new BusinessException("Não foi possível preparar o diretório de arquivos.");
        }
        return directory;
    }

    private void assertInsideRoot(Path path) {
        if (!path.toAbsolutePath().normalize().startsWith(root)) {
            throw new BusinessException("Caminho de arquivo inválido.");
        }
    }

    private String extension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index).toLowerCase() : "";
    }
}
