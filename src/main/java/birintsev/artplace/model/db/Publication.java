package birintsev.artplace.model.db;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "ap_publications")
public class Publication {

    @Id
    private UUID id;

    @Column
    private String title;

    @Column(name = "publication_text")
    private String publicationText;

    @ManyToOne(optional = false)
    @JoinColumn(name = "public_id", referencedColumnName = "id")
    private Public parentPublic;

    @ManyToMany
    @JoinTable(
        name = "ap_publication_files",
        joinColumns = {
            @JoinColumn(name = "publication_id", referencedColumnName = "id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "file_id", referencedColumnName = "id")
        }
    )
    private Set<File> attachments;
}
