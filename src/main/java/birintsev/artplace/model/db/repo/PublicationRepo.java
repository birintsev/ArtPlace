package birintsev.artplace.model.db.repo;

import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface PublicationRepo
extends JpaRepository<Publication, UUID> {

    @Query(
        value = "select p "
            + "from Publication p, "
            + "     PublicSubscription ps "
            + "where "
            + "     ps.user = :subscriber"
            + "     and (p.tariff = ps.subscriptionTariff "
            + "         or ps.subscriptionTariff.price = 0)"
            + "     and p.parentPublic = ps.subscribedPublic"
    )
    Slice<Publication> findAllBySubscriber(
        User subscriber,
        Pageable pageable
    );

    Slice<Publication> findAllByParentPublic(
        Public parentPublic,
        Pageable pageable
    );

    @Query(
        value = "select count(p) "
            + "from Publication p "
            + "where p.parentPublic = :parentPublic"
    )
    int countAllByParentPublic(Public parentPublic);
}
