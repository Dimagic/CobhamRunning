package net.ddns.dimag.cobhamrunning.models.environment;

import net.ddns.dimag.cobhamrunning.models.Device;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;

@Entity
@Table(name = "envlocation", uniqueConstraints = { @UniqueConstraint(columnNames = "location")})
public class EnvLocation {
    private static final Logger LOGGER = LogManager.getLogger(Device.class.getName());

    public EnvLocation() {
    }

    public EnvLocation(String location) {
        this.location = location;
    }

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

    @Column(name = "location", length = 64, nullable = false, unique = true)
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
