package birintsev.artplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This DTO represents a {@link birintsev.artplace.model.db.User user} request
 * to post some materials
 * in an owned {@link birintsev.artplace.model.db.Public public}.
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishRequestImpl implements PublishRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PublishRequestImpl.class
    );

    private String title;

    private String publicationText;

    private UUID parentPublicId;

    private Set<MultipartFile> attachedFiles;

    private UUID tariffId;

    @Override
    public Map<String, InputStream> getAttachments() {
        return attachedFiles.stream().collect(Collectors.toMap(
            MultipartFile::getOriginalFilename,
            multipartFile -> {
                try {
                    return multipartFile.getInputStream();
                } catch (IOException e) {
                    final String msg = String.format(
                        "Error during obtaining MultipartFile "
                            + "InputStream. The MultipartFile itself: %s",
                        multipartFile
                    );
                    LOGGER.error(
                        msg,
                        e
                    );
                    throw new UncheckedIOException(msg, e);
                }
            }
        ));
    }
}
