package net.ddns.dimag.cobhamrunning.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "tests")
public class Tests {
	@Column(nullable = false)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, insertable = true, updatable = false)
	private Date testDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public java.util.Date getDateTest() {
		return testDate;
	}

	public void setDateTest(Date testDate) {
		this.testDate = testDate;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		setDateTest(new Date());
		this.device = device;
	}

	/*
	 * Add measure to test
	 */
	@Column
	@ElementCollection(targetClass = Long.class)
	private Set<Measurements> meas = new HashSet<Measurements>();

	@OneToMany(mappedBy = "test", orphanRemoval = true)
	public Set<Measurements> getMeas() {
		return this.meas;
	}

	public void setMeas(Set<Measurements> meas) {
		this.meas = meas;
	}

	public void addMeas(Measurements meas) {
		meas.setTest(this);
		getMeas().add(meas);
	}

	public void removeTests(Measurements meas) {
		getMeas().remove(meas);
	}

	@Override
	public String toString() {
		return "Tests{" +
				"name='" + name + '\'' +
				", testDate=" + testDate +
				", device=" + device +
				", id=" + id +
				'}';
	}
}
