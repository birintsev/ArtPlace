package birintsev.artplace.converters;

import birintsev.artplace.dto.PublishRequest;
import birintsev.artplace.model.db.File;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.repo.PublicRepo;
import birintsev.artplace.model.db.repo.TariffRepo;
import birintsev.artplace.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class converts, <strong>but not persists</strong>
 * a {@link PublishRequest request}
 * to a {@link Publication}.
 *
 * The class was created to unload
 * {@link birintsev.artplace.services.DefaultPublicationService}
 * from the conversion logic.
 * In fact, it just prepares a {@link Publication} to be stored to the database.
 *
 * However, it might be re-used somewhere else.
 *
 * @see birintsev.artplace.services.DefaultPublicService
 * */
@Component
public class PublicationPreparer
implements Converter<PublishRequest, Publication> {

    private final PublicRepo publicRepo;

    private final TariffRepo tariffRepo;

    private final FileService fileService;

    private final URL publicationsFilesStorage;

    public PublicationPreparer(
        PublicRepo publicRepo,
        TariffRepo tariffRepo,
        FileService fileService,
        @Value(value = "${ap.publication-files.save-url}")
            URL publicationsFilesStorage
    ) {
        this.publicRepo = publicRepo;
        this.tariffRepo = tariffRepo;
        this.fileService = fileService;
        this.publicationsFilesStorage = publicationsFilesStorage;
    }

    @Override
    public Publication convert(PublishRequest source) {
        return new Publication(
            UUID.randomUUID(),
            source.getTitle(),
            source.getPublicationText(),
            findOrThrow(source.getParentPublicId(), publicRepo),
            findOrThrow(source.getTariffId(), tariffRepo),
            convertAttachments(source.getAttachments()),
            Timestamp.valueOf(LocalDateTime.now())
        );
    }

    private Set<File> convertAttachments(Map<String, InputStream> filesInput) {
        return filesInput.entrySet()
            .stream()
            .map(entry -> {
                final String fileName = entry.getKey();
                final InputStream fis = entry.getValue(); // File Input Stream
                return new File(
                    UUID.randomUUID(),
                    fileService.saveFile(
                        fis,
                        fileName,
                        publicationsFilesStorage
                    ),
                    fileName
                );
            })
            .collect(Collectors.toSet());
    }

    private <ID, ENTITY> ENTITY findOrThrow(
        ID id,
        JpaRepository<ENTITY, ID> repository
    ) {
        return repository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException(
                    String.format(
                        "Entity (id = %s) does not exist in the repository: %s",
                        id,
                        repository
                    )
                )
            );
    }
}
