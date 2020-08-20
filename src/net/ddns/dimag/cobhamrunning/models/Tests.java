package net.ddns.dimag.cobhamrunning.models;

import javafx.beans.property.SimpleStringProperty;
import net.ddns.dimag.cobhamrunning.services.MeasurementsService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.RmvUtils;
import net.ddns.dimag.cobhamrunning.utils.Utils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	@OnDelete(action = OnDeleteAction.CASCADE)
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

	public Long getDateTestMilliseconds(){
		return testDate.getTime();
	}

	public String getFormattedDateTest(){
		return Utils.getFormattedDate(getDateTest());
	}

	public void setDateTest(Date testDate) {
		this.testDate = testDate;
	}

	@Column(name = "testStatus")
	private int testStatus;

	public int getTestStatus() {
		return testStatus;
	}

	public String getStringTestStatus(){
		return getTestStatus() == 0 ? "PASS" : "FAIL";
	}

	public void setTestStatus(int testStatus) {
		this.testStatus = testStatus;
	}

	@Column(name = "testTime")
	private Integer testTime;

	public Integer getTestTime() {
		return testTime;
	}

	public String getTestTimeHMSM(){
		return Utils.formatHMSM(getTestTime());
	}

	public void setTestTime(Integer testTime) {
		this.testTime = testTime;
	}

	@Column(name = "HeaderID")
	private Long HeaderID;

	public Long getHeaderID() {
		return HeaderID;
	}

	public void setHeaderID(Long headerID) {
		HeaderID = headerID;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		// ToDo: if test loaded from rmv, temp. fix
		if (getDateTest() == null){
			setDateTest(new Date());
		}
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

	public SimpleStringProperty nameProperty(){
		return new SimpleStringProperty(getName());
	}

	public SimpleStringProperty testDateProperty() {
		try {
			return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDateTest()));
		} catch (NullPointerException e) {}
		return new SimpleStringProperty("");
	}

	public SimpleStringProperty testTimeProperty() {
		return new SimpleStringProperty(Utils.formatHMSM(getTestTime()));
	}

	public SimpleStringProperty testStatusProperty(){
		if (getId() != null) {
			try {
				MeasurementsService measurementsService = new MeasurementsService();
				List<Measurements> measList = measurementsService.getMeasureSetByTest(this);
				if (measList.size() == 0) {
					return null;
				}
				for (Measurements meas : measList) {
					if (meas.getMeasStatus() == 0){
						return new SimpleStringProperty("FAIL");
					}
				}
			} catch (CobhamRunningException e) {
				return null;
			}
			return new SimpleStringProperty("PASS");
		} else {
			return getTestStatus() == 0 ? new SimpleStringProperty("PASS") :
					new SimpleStringProperty("FAIL");
		}
	}

	public String getArticle(){
		return device.getAsis().getArticleHeaders().getArticle();
	}

	public String getAsis(){
		return device.getAsis().getAsis();
	}

	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private String computerName;

	public String getComputerName() {
		return computerName;
	}

	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tests{");
		sb.append("name='").append(name).append('\'');
		sb.append(", testDate=").append(testDate);
		sb.append(", device=").append(device);
		sb.append(", id=").append(id);
		sb.append(", testStatus=").append(testStatus);
		sb.append(", testTime=").append(testTime);
		sb.append(", HeaderID=").append(HeaderID);
		sb.append(", meas=").append(meas);
		sb.append('}');
		return sb.toString();
	}
}
