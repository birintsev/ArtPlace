package birintsev.artplace.model.db.repo;

import birintsev.artplace.model.db.UserPermanentPublication;
import birintsev.artplace.model.db.embeddable.UserPublicationAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermanentPublicationRepo
extends JpaRepository<UserPermanentPublication, UserPublicationAssociation> {

}
