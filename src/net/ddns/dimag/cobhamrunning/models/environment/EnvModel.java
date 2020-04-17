package net.ddns.dimag.cobhamrunning.models.environment;

import javafx.beans.property.SimpleStringProperty;
import net.ddns.dimag.cobhamrunning.models.Device;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "envmodel", uniqueConstraints = { @UniqueConstraint(columnNames = "name")})
public class EnvModel {
    private static final Logger LOGGER = LogManager.getLogger(Device.class.getName());

    public EnvModel(){}

    public EnvModel(String name, EnvManuf envManuf, EnvType envType) {
        this.name = name;
        this.envManuf = envManuf;
        this.envType = envType;
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

    @Column(name = "name", length = 64, nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envmanuf_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnvManuf envManuf;

    public EnvManuf getEnvManuf() {
        return envManuf;
    }

    public void setEnvManuf(EnvManuf envManuf) {
        this.envManuf = envManuf;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envtype_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnvType envType;

    public EnvType getEnvType() {
        return envType;
    }

    public void setEnvType(EnvType envType) {
        this.envType = envType;
    }

    public SimpleStringProperty nameProperty(){
        return new SimpleStringProperty(getName());
    }
}
