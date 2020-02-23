package net.ddns.dimag.cobhamrunning.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.ddns.dimag.cobhamrunning.services.DeviceInfoService;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "device", uniqueConstraints = { @UniqueConstraint(columnNames = "asis_id"),
        @UniqueConstraint(columnNames = "sn") })
public class Device {
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

    @Column(name = "sn", length = 8, nullable = false, unique = true)
    private String sn;

    public Device() {
    }

    public Device(Asis asis, String sn) {
        this.asis = asis;
        this.sn = sn;
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

}
