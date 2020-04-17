package net.ddns.dimag.cobhamrunning.models.environment;

import net.ddns.dimag.cobhamrunning.models.Device;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;

@Entity
@Table(name = "envhistory")
public class EnvHistory {
    private static final Logger LOGGER = LogManager.getLogger(Device.class.getName());

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
