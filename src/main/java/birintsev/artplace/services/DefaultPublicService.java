package birintsev.artplace.services;

import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.PublicSubscription;
import birintsev.artplace.model.db.User;
import birintsev.artplace.model.db.repo.PublicRepo;
import birintsev.artplace.model.db.repo.PublicSubscriptionRepo;
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

    private final PublicSubscriptionRepo publicSubscriptionRepo;

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

    @Override
    public void unsubscribe(User user, Public subscribedPublic) {
        Optional<PublicSubscription> optionalPublicSubscription =
            publicSubscriptionRepo.findById(
                new PublicSubscription.PublicSubscriptionId(
                    user.getId(),
                    subscribedPublic.getId()
                )
            );
        optionalPublicSubscription.ifPresent(publicSubscriptionRepo::delete);
    }

    @Override
    public boolean isSubscriber(User user, Public aPublic) {
        return publicRepo.isSubscriber(user, aPublic);
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
