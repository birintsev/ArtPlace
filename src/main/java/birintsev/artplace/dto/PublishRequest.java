package birintsev.artplace.dto;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * A general interface of a request
 * of a {@link birintsev.artplace.model.db.Publication publication}
 * to be posted in a {@link birintsev.artplace.model.db.Public public}.
 * */
public interface PublishRequest {

    String getTitle();

    String getPublicationText();

    UUID getParentPublicId();

    Map<String, InputStream> getAttachments();

    UUID getTariffId();
}
