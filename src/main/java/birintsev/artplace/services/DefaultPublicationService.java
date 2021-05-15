package birintsev.artplace.services;

import birintsev.artplace.converters.PublicationPreparer;
import birintsev.artplace.dto.PublishRequest;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import birintsev.artplace.model.db.repo.PublicRepo;
import birintsev.artplace.model.db.repo.PublicationRepo;
import birintsev.artplace.services.exceptions.UnauthorizedOperationException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultPublicationService implements PublicationService {

    private final PublicationRepo publicationRepo;

    private final PublicRepo publicRepo;

    private final ApplicationEventPublisher eventPublisher;

    private final ConversionService conversionService;

    @Override
    public Slice<Publication> findForUser(User subscriber, Pageable pageable) {
        return publicationRepo.findAllBySubscriber(
            subscriber,
            pageable
        );
    }

    /**
     * @see PublicationPreparer See how the publishRequest
     *                          is converted to a Publication
     * */
    @Override
    public Publication publish(
        User publisher,
        PublishRequest publishRequest
    ) {
        if (
            !publicRepo.isOwner(publisher, publishRequest.getParentPublicId())
        ) {
            throw new UnauthorizedOperationException(
                String.format(
                    "The user (id = %s) is not the public (id = %s) owner.",
                    publisher.getId(),
                    publishRequest.getParentPublicId()
                )
            );
        }
        return publicationRepo.save(
            conversionService.convert(
                publishRequest,
                Publication.class
            )
        );
    }
}
