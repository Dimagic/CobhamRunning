package net.ddns.dimag.cobhamrunning.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "macaddress")
public class MacAddress {
	
	public MacAddress() {
	}
	
	public MacAddress(String mac) {
		this.mac = mac;
	}
	
	public MacAddress(String mac, Asis asis) {
		this.mac = mac;
		this.asis = asis;
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
	
	@Column(name = "mac", nullable = false, length = 17)
	private String mac;
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
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
	
}
