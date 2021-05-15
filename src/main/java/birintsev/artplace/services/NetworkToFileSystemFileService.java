package birintsev.artplace.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Saves incoming files from the network to files system.
 * */
@Service
@AllArgsConstructor
public class NetworkToFileSystemFileService implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        NetworkToFileSystemFileService.class
    );

    /**
     * {@inheritDoc}
     *
     * This method does not close the passed input stream.
     * */
    @Override
    public URI saveFile(
        InputStream fileStream,
        String fileName,
        URL location
    ) {
        validProtocol(location);
        final File targetFile;
        try {
            targetFile = new File(new File(location.toURI()), fileName);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(
                "An error happened during converting location URL -> URI",
                e
            );
        }
        try {
            return Files.write(
                targetFile.toPath(),
                fileStream.readAllBytes(),
                // TODO: configure StandardOpenOptions to not to overwrite files
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE
            ).toUri();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(
                "An error happened during file writing",
                e
            );
        }
    }

    @Override
    public InputStream getFile(URI fileId) throws FileNotFoundException {
        validProtocol(fileId);
        return new FileInputStream(Paths.get(fileId).toFile());
    }

    private void validProtocol(URI uri) {
        if (!isOfSupportedProtocol(uri)) {
            throw new UnsupportedOperationException(
                String.format("The protocol of %s is not supported", uri)
            );
        }
    }

    private void validProtocol(URL url) {
        if (!isOfSupportedProtocol(url)) {
            throw new UnsupportedOperationException(
                String.format("The protocol of %s is not supported", url)
            );
        }
    }

    private boolean isOfSupportedProtocol(URL url) {
        return url != null && isProtocolSupported(url.getProtocol());
    }

    private boolean isOfSupportedProtocol(URI uri) {
        return uri != null && isProtocolSupported(uri.getScheme());
    }

    private boolean isProtocolSupported(String protocol) {
        return "file".equalsIgnoreCase(protocol);
    }
}
