package net.ddns.dimag.cobhamrunning.models.environment;

import net.ddns.dimag.cobhamrunning.models.Device;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "envdevice", uniqueConstraints = { @UniqueConstraint(columnNames = "sn")})
public class EnvDevice {
    private static final Logger LOGGER = LogManager.getLogger(Device.class.getName());

    public EnvDevice() {
    }

    public EnvDevice(EnvLocation location){
        this.sn = "";
        this.envLocation = location;
    }

    public EnvDevice(String sn, EnvModel envModel, EnvLocation envLocation, EnvStatus envStatus) {
        this.sn = sn;
        this.envModel = envModel;
        this.envLocation = envLocation;
        this.envStatus = envStatus;
    }

    public EnvDevice(String sn, EnvModel envModel, EnvLocation envLocation, EnvStatus envStatus, Date calibrDate) {
        this.sn = sn;
        this.envModel = envModel;
        this.envLocation = envLocation;
        this.envStatus = envStatus;
        this.envCalibrDate = calibrDate;
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
    @JoinColumn(name = "envmodel_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnvModel envModel;

    public EnvModel getEnvModel() {
        return envModel;
    }

    public void setEnvModel(EnvModel envModel) {
        this.envModel = envModel;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "calibrdate")
    private Date envCalibrDate;

    public Date getEnvCalibrDate() {
        return envCalibrDate;
    }

    public void setEnvCalibrDate(Date envCalibrDate) {
        this.envCalibrDate = envCalibrDate;
    }

    public String getLocation(){
        return getEnvLocation().getLocation();
    }

    public String getManuf(){
        return getEnvModel().getEnvManuf().getName();
    }

    public String getModel(){
        return getEnvModel().getName();
    }

    public String getTypeDev(){
        return getEnvModel().getEnvType().getName();
    }

    public String getCalibrDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.format(getEnvCalibrDate());
        } catch (NullPointerException e){
            return "";
        }
    }

    public String getStatus(){
        return getEnvStatus().getStatus();
    }

//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("EnvDevice{");
//        sb.append("id=").append(id);
//        sb.append(", sn='").append(sn).append('\'');
//        sb.append(", envModel=").append(getModel());
//        sb.append(", envLocation=").append(getLocation());
//        sb.append(", envStatus=").append(getStatus());
//        sb.append(", envHistory=").append(envHistory);
//        sb.append('}');
//        return sb.toString();
//    }
}
