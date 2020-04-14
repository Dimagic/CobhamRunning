package net.ddns.dimag.cobhamrunning.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;

@Entity
@Table(name = "envstatus", uniqueConstraints = { @UniqueConstraint(columnNames = "status")})
public class EnvStatus {
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

    @Column(name = "status", length = 64, nullable = false, unique = true)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
