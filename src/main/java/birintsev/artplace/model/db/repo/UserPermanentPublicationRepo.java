package birintsev.artplace.model.db.repo;

import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import birintsev.artplace.model.db.UserPermanentPublication;
import birintsev.artplace.model.db.embeddable.UserPublicationAssociation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermanentPublicationRepo
extends JpaRepository<UserPermanentPublication, UserPublicationAssociation> {

    /**
     * TODO: javadoc
     * */
    @Query(
        value = "select upp.id.publication "
            + "from UserPermanentPublication upp "
            + "where upp.id.user = :user"
    )
    Slice<Publication> findAllByUser(User user, Pageable pageable);
}
