package birintsev.artplace.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "ap_publications")
@AllArgsConstructor
@NoArgsConstructor
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

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "tariff_id",
        referencedColumnName = "id"
    )
    private SubscriptionTariff tariff;

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

    @Column(name = "publication_date")
    private Timestamp publicationDate;
}
