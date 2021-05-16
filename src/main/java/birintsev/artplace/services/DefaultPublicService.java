package birintsev.artplace.services;

import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.User;
import birintsev.artplace.model.db.repo.PublicRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service("DefaultPublicService")
@AllArgsConstructor
public class DefaultPublicService implements PublicService {

    private final PublicRepo publicRepo;

    @Override
    public Page<Public> userSubscriptions(User subscriber) {
        return publicRepo.findAllBySubscriber(
            subscriber,
            defaultSubscriptionsFirstPage()
        );
    }

    @Override
    public Optional<Public> findById(UUID publicId) {
        return publicRepo.findById(publicId);
    }

    @Override
    public int getTotalSubscribersAmount(Public aPublic) {
        return publicRepo.getTotalSubscribersCount(aPublic);
    }

    /**
     * A default {@link Pageable} for querying the first page
     * of a user subscriptions.
     * */
    private Pageable defaultSubscriptionsFirstPage() {
        final int defaultPageSize = 5;
        return PageRequest.of(0, defaultPageSize, Sort.by("name"));
    }
}
