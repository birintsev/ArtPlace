package birintsev.artplace.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Currency;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "ap_subscr_tariffs")
public class SubscriptionTariff {

    public static final String FREE_TARIFF_NAME = "FREE";

    public static final String PAID_TARIFF_NAME = "PAID";

    @Id
    private UUID id;

    private String name;

    private BigInteger price;

    @Column(name = "currency_code")
    private Currency currency;
}
