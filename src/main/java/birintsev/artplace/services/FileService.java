package birintsev.artplace.services;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * This is a generic interface to work with files (save/retrieve/remove)
 * */
public interface FileService {

    /**
     * Saves a file to the specified destination.
     *
     * @param     fileStream a stream from which the file might be read.
     *                       <strong>Note: </strong>
     *                       it is implementation-specific
     *                       if the stream is closed
     *                       after the method worked out.
     * @param     fileName   the file name/identifier
     * @param     location   the place, where the file will be stored.
     *                       For example, this parameter may point to
     *                       a file system folder, an FTP server folder,
     *                       DB table or something else
     *                       (depending on implementation).
     *
     * @return               such an address using which it is possible
     *                       to get saved file
     *                       using {@link #getFile(URI)} method.
     *
     * @exception UnsupportedOperationException
     *                       if the {@code location} protocol is not supported.
     * */
    URI saveFile(
        InputStream fileStream,
        String fileName,
        URL location
    );

    /**
     * Retrieves a file from an implementation-specific storage
     * (e.g. file system, FTP server or DB table).
     *
     * @return                       an input stream from which the caller
     *                               may read the file.
     *
     * @throws FileNotFoundException if the requested file not found.
     * */
    InputStream getFile(URI fileId) throws FileNotFoundException;

    // TODO: create remove(URI); method
}
