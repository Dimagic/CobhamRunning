package net.ddns.dimag.cobhamrunning.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "envdevice", uniqueConstraints = { @UniqueConstraint(columnNames = "sn")})
public class EnvDevice {
    private static final Logger LOGGER = LogManager.getLogger(Device.class.getName());

    public EnvDevice(){
    }

    public EnvDevice(String sn){
        this.sn = sn;
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

    @Column(name = "sn", length = 64, nullable = false, unique = true)
    private String sn;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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
    @JoinColumn(name = "envlocation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnvLocation envLocation;

    public EnvLocation getEnvLocation() {
        return envLocation;
    }

    public void setEnvLocation(EnvLocation envLocation) {
        this.envLocation = envLocation;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envstatus_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnvStatus envStatus;

    public EnvStatus getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(EnvStatus envStatus) {
        this.envStatus = envStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "envhistory_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnvHistory envHistory;

    public EnvHistory getEnvHistory() {
        return envHistory;
    }

    public void setEnvHistory(EnvHistory envHistory) {
        this.envHistory = envHistory;
    }

    public StringProperty snProperty() {
        return new SimpleStringProperty(getSn());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EnvDevice{");
        sb.append("id=").append(id);
        sb.append(", sn='").append(sn).append('\'');
        sb.append(", envManuf=").append(envManuf);
        sb.append(", envLocation=").append(envLocation);
        sb.append(", envStatus=").append(envStatus);
        sb.append('}');
        return sb.toString();
    }
}
