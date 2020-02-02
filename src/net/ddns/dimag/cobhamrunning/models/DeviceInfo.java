package net.ddns.dimag.cobhamrunning.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "deviceinfo")
public class DeviceInfo {
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
	
	@Column(name = "systemVer")
	private String systemVer;

	@Column(name = "commonVer")
	private String commonVer;

	@Column(name = "targetVer")
	private String targetVer;

	

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "device_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Device device;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
	public DeviceInfo(){
		
	}

	public DeviceInfo(String systemVer, String commonVer, String targetVer) {
		super();
		this.systemVer = systemVer;
		this.commonVer = commonVer;
		this.targetVer = targetVer;
	}

	public String getSystemVer() {
		return systemVer;
	}

	public void setSystemVer(String systemVer) {
		this.systemVer = systemVer;
	}

	public String getCommonVer() {
		return commonVer;
	}

	public void setCommonVer(String commonVer) {
		this.commonVer = commonVer;
	}

	public String getTargetVer() {
		return targetVer;
	}

	public void setTargetVer(String targetVer) {
		this.targetVer = targetVer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commonVer == null) ? 0 : commonVer.hashCode());
		result = prime * result + ((systemVer == null) ? 0 : systemVer.hashCode());
		result = prime * result + ((targetVer == null) ? 0 : targetVer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceInfo other = (DeviceInfo) obj;
		if (commonVer == null) {
			if (other.commonVer != null)
				return false;
		} else if (!commonVer.equals(other.commonVer))
			return false;
		if (systemVer == null) {
			if (other.systemVer != null)
				return false;
		} else if (!systemVer.equals(other.systemVer))
			return false;
		if (targetVer == null) {
			if (other.targetVer != null)
				return false;
		} else if (!targetVer.equals(other.targetVer))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("DeviceInfo [id=%s, systemVer=%s, commonVer=%s, targetVer=%s, device=%s]", id, systemVer,
				commonVer, targetVer, device);
	}

		
}
