package birintsev.artplace.services;

import birintsev.artplace.converters.PublicationPreparer;
import birintsev.artplace.dto.PublishRequest;
import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import birintsev.artplace.model.db.UserPermanentPublication;
import birintsev.artplace.model.db.embeddable.UserPublicationAssociation;
import birintsev.artplace.model.db.repo.PublicRepo;
import birintsev.artplace.model.db.repo.PublicationRepo;
import birintsev.artplace.model.db.repo.UserPermanentPublicationRepo;
import birintsev.artplace.services.exceptions.UnauthorizedOperationException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultPublicationService implements PublicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DefaultPublicationService.class
    );

    private final PublicationRepo publicationRepo;

    private final PublicRepo publicRepo;

    private final ApplicationEventPublisher eventPublisher;

    private final ConversionService conversionService;

    private final UserPermanentPublicationRepo permanentPublicationRepo;

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

    @Override
    public Slice<Publication> findByPublic(
        Public parentPublic,
        Pageable pageable
    ) {
        return publicationRepo.findAllByParentPublic(parentPublic, pageable);
    }

    @Override
    public int getTotalPublicationsCount(Public aPublic) {
        return publicationRepo.countAllByParentPublic(aPublic);
    }

    @Override
    public void bindForPaidSubscribers(Publication publication) {
        if (!isPaid(publication)) {
            LOGGER.info(
                String.format(
                    "The publication (id = %s) is free. Skipping binding.",
                    publication.getId()
                )
            );
            return;
        }
        permanentPublicationRepo.saveAll(
            publicRepo.findSubscribersByPublicAndTariff(
                publication.getParentPublic(),
                publication.getTariff()
            ).stream()
                .map(user -> new UserPermanentPublication(
                    new UserPublicationAssociation(user, publication)
                )).collect(Collectors.toList())
        );
    }

    @Override
    public Slice<Publication> findPermanentPublicationsByUser(
        User subscriber,
        Pageable pageable
    ) {
        return permanentPublicationRepo.findAllByUser(subscriber, pageable);
    }

    private boolean isPaid(Publication publication) {
        return BigInteger.ZERO.compareTo(
            publication.getTariff().getPrice()
        ) < 0;
    }
}
