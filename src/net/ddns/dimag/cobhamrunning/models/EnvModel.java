package net.ddns.dimag.cobhamrunning.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;

@Entity
@Table(name = "envmodel", uniqueConstraints = { @UniqueConstraint(columnNames = "name")})
public class EnvModel {
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

    @Column(name = "name", length = 64, nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
