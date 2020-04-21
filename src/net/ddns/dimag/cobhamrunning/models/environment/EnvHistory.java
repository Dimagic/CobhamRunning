package net.ddns.dimag.cobhamrunning.models.environment;

import javafx.beans.property.SimpleStringProperty;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.services.environment.EnvHistoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "envhistory")
public class EnvHistory {
    private static final Logger LOGGER = LogManager.getLogger(Device.class.getName());

    public EnvHistory() {
    }

    public EnvHistory(EnvDevice envDevice, String fieldChange, String valueChange) {
        setDate(new Date());
        this.envDevice = envDevice;
        this.fieldChange = fieldChange;
        this.valueChange = valueChange;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envdevice_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnvDevice envDevice;

    public EnvDevice getEnvDevice() {
        return envDevice;
    }

    public void setEnvDevice(EnvDevice envDevice) {
        this.envDevice = envDevice;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(nullable = false, length = 128)
    private String fieldChange;

    public String getFieldChange() {
        return fieldChange;
    }

    public void setFieldChange(String fieldChange) {
        this.fieldChange = fieldChange;
    }

    @Column(nullable = false, length = 128)
    private String valueChange;

    public String getValueChange() {
        return valueChange;
    }

    public void setValueChange(String valueChange) {
        this.valueChange = valueChange;
    }

    public SimpleStringProperty fieldProperty(){
        return new SimpleStringProperty(getFieldChange());
    }

    public SimpleStringProperty valueProperty(){
        return new SimpleStringProperty(getValueChange());
    }

    public SimpleStringProperty dateProperty(){
        return new SimpleStringProperty(dateToString(getDate()));
    }

    private String dateToString(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
