package birintsev.artplace.services;

import birintsev.artplace.dto.PublishRequest;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * This is a <strong>business logic</strong> providing interface.
 * */
@Service
public interface PublicationService {

    default Slice<Publication> findForUserFirstPage(User subscriber) {
        return findForUser(subscriber, defaultFirstPublicationPage());
    }

    private Pageable defaultFirstPublicationPage() {
        final int defaultPageSize = 10;
        return PageRequest.of(
            0,
            defaultPageSize,
            Sort.Direction.DESC,
            "publicationDate"
        );
    }

    Slice<Publication> findForUser(User user, Pageable pageable);

    /**
     * Creates a publication on behalf of passed {@link User publisher}
     *
     * @param     publisher      a user who creates the publication
     * @param     publishRequest a publication details container
     *
     * @return                   created publication
     *
     * @exception birintsev.artplace.services.exceptions.UnauthorizedOperationException
     *                           if publisher is not the owner of the public
     *                           specified by the
     *                           {@link PublishRequest#getParentPublicId() id}
     * */
    Publication publish(User publisher, PublishRequest publishRequest);
}
