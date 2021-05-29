package birintsev.artplace.model.db.repo;

import birintsev.artplace.model.db.PublicSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicSubscriptionRepo
extends JpaRepository<
    PublicSubscription,
    PublicSubscription.PublicSubscriptionId
> {

}
