package net.ddns.dimag.cobhamrunning.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import javafx.beans.property.SimpleStringProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "shippingJournal", uniqueConstraints = { @UniqueConstraint(columnNames = "device_id")})
public class ShippingSystem {
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
	public ShippingSystem(){}
	
	public ShippingSystem(Device device){
		this.device = device;
		this.dateShip = new Date();
	}
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "device_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Device device;
		
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateShip;
	
	public Date getDateShip() {
		return dateShip;
	}

	public void setDateShip(Date dateShip) {
		this.dateShip = dateShip;
	}

	public SimpleStringProperty dateShipProperty() {
		try {
			return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").format(getDateShip()));
		} catch (NullPointerException e) {}
		return new SimpleStringProperty("");
	}
	
	
	
}
