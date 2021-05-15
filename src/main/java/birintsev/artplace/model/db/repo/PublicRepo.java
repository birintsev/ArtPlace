package birintsev.artplace.model.db.repo;

import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Set;
import java.util.UUID;

public interface PublicRepo
extends JpaRepository<Public, UUID> {

    @Query(
        value = "select distinct p "
            + "from Public p "
            + "where p.id in "
            + "(select ps.publicId "
            + "from PublicSubscription ps "
            + "where ps.user = :subscriber)",
        countQuery = "select count(distinct ps.publicId) "
            + "from PublicSubscription ps "
            + "where ps.user = :subscriber"
    )
    Page<Public> findAllBySubscriber(User subscriber, Pageable pageable);

    /**
     * Unpaged version
     * of {@link PublicRepo#findAllBySubscriber(User, Pageable)} method
     * */
    default Set<Public> findAllBySubscriber(User subscriber) {
        return findAllBySubscriber(subscriber, Pageable.unpaged()).toSet();
    }

    /**
     * Checks if passed {@link User}
     * is an owner of the {@link Public}
     * specified by passed {@link Public#getId() ID}.
     *
     * @param   user     a user to be tested
     * @param   publicId an identifier of the public
     *
     * @return           {@code true} if and only if the {@link Public}
     *                   with such ID exists and is owned by passed user,
     *                   {@code false} otherwise
     * */
    @Query(
        value = "select "
            + "case when (count(p) > 0) then true else false end "
            + "from Public p "
            + "where p.owner = :user and p.id = :publicId"
    )
    boolean isOwner(User user, UUID publicId);
}
