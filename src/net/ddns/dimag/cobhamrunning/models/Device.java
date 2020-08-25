package net.ddns.dimag.cobhamrunning.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.AsisService;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "device", uniqueConstraints = { @UniqueConstraint(columnNames = "asis_id"),
        @UniqueConstraint(columnNames = "sn") })
public class Device {
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

    @Column(name = "sn", length = 20, nullable = false, unique = true)
    private String sn;

    public Device() {
        this.dateCreate = new Date();
    }

    public Device(Asis asis, String sn) {
        this.asis = asis;
        this.sn = sn;
        this.dateCreate = new Date();
    }

    public Device getDeviceByAsisSn(){
        try {
            List<String> currSys = MsgBox.msgScanSystemBarcode();
            if (currSys == null) {
                return null;
            }
            String articleString = currSys.get(0);
            String asisString = currSys.get(1);
            DeviceService deviceService = new DeviceService();
            Device dbDeviceByAsis = deviceService.findDeviceByAsis(asisString);
            if (dbDeviceByAsis != null){
                return dbDeviceByAsis;
            }
            String snString = MsgBox.msgInputSN();
            if (snString == null){
                return null;
            }
            Device dbDeviceBySn = deviceService.findDeviceBySn(snString);
            if (dbDeviceByAsis == null && dbDeviceBySn == null) {
                AsisService asisService = new AsisService();
                Asis currAsis = asisService.findByName(asisString);
                if (currAsis == null) {
                    ArticleHeadersService articleHeadersService = new ArticleHeadersService();
                    ArticleHeaders currArticleHeaders = articleHeadersService.findArticleByName(articleString);
                    if (currArticleHeaders == null){
                        MsgBox.msgWarning("Warning", String.format("Article %s not found", articleString));
                        return null;
                    }
                    currAsis = new Asis(asisString, currArticleHeaders);
                    asisService.saveAsis(currAsis);
                }
                this.setAsis(currAsis);
                this.setSn(snString);
                deviceService.saveDevice(this);
                return this;
            } else if (dbDeviceByAsis != null && dbDeviceBySn != null){
                return dbDeviceBySn;
            } else if (dbDeviceByAsis != null){
                MsgBox.msgWarning("Warning", String.format("Found device with ASIS: %s\n" +
                        "that have SN: %s", asisString, dbDeviceByAsis.getSn()));
            } else if (dbDeviceBySn != null){
                MsgBox.msgWarning("Warning", String.format("Found device with SN: %s\n" +
                        "that have ASIS: %s", snString, dbDeviceBySn.getAsis().getAsis()));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MsgBox.msgException(e);
        }
        return null;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datecreate")
    private Date dateCreate;

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, optional = true)
    @JoinColumn(name = "deviceInfo_id")
    private DeviceInfo deviceInfo;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        this.deviceInfo.setDevice(this);
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asis_id")
    private Asis asis;

    public Asis getAsis() {
        return asis;
    }

    public void setAsis(Asis asis) {
        this.asis = asis;
    }

    public String getMac() {
        return asis.getMacAddress().getMac();
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String value) {
        this.sn = value;
    }

    @Column
    @ElementCollection(targetClass = Long.class)
    private Set<Tests> tests = new HashSet<Tests>();

    @OneToMany(mappedBy = "device", orphanRemoval = true)
    public Set<Tests> getTests() {
        return this.tests;
    }

    public void setTests(Set<Tests> tests) {
        this.tests = tests;
    }

    public void addTests(Tests tests) {
        tests.setDevice(this);
        getTests().add(tests);
    }

    public void removeTests(Tests tests) {
        getTests().remove(tests);
    }

    @OneToOne(mappedBy = "device", orphanRemoval = true)
    private ShippingSystem shippingSystem;

    public ShippingSystem getShippingSystem() {
        return shippingSystem;
    }

    public void setShippingSystem(ShippingSystem shippingSystem) {
        this.shippingSystem = shippingSystem;
    }

    public StringProperty commonVerProperty() {
        try {
            return new SimpleStringProperty(getDeviceInfo().getCommonVer());
        } catch (NullPointerException e) {
            return new SimpleStringProperty("");
        }

    }

    public StringProperty systemVerProperty() {
        try {
            return new SimpleStringProperty(getDeviceInfo().getSystemVer());
        } catch (NullPointerException e) {
            return new SimpleStringProperty("");
        }
	}

	public StringProperty targetVerProperty() {
        try {
            return new SimpleStringProperty(getDeviceInfo().getTargetVer());
        } catch (NullPointerException e) {
            return new SimpleStringProperty("");
        }
	}

	public StringProperty snProperty() {
		return new SimpleStringProperty(getSn());
	}

    public StringProperty articleProperty() {
        return new SimpleStringProperty(getAsis().getArticleHeaders().getArticle());
    }

    public StringProperty asisProperty() {
        return new SimpleStringProperty(getAsis().getAsis());
    }

	public StringProperty macProperty() {
        try {
            return new SimpleStringProperty(getMac());
        } catch (NullPointerException e) {
            return new SimpleStringProperty("");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return sn.equals(device.sn) &&
                asis.getAsis().equals(device.asis.getAsis());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sn, asis.getAsis());
    }
}
