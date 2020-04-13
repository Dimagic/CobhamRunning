package net.ddns.dimag.cobhamrunning.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javafx.beans.property.SimpleStringProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "measurements")
public class Measurements {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

//	ToDo: constructor

	@Column(nullable = false, length = 128)
	private String measName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date measDate;

	@Column(length = 128)
	private String measMin;

	@Column(columnDefinition = "TEXT")
	private String measVal;

	@Column(length = 128)
	private String measMax;

	@Column(name = "measureNumber")
	private int measureNumber;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "test_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Tests test;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMeasName() {
		return measName;
	}

	public void setMeasName(String measName) {
		setDateMeas(new Date());
		this.measName = measName;
	}

	public java.util.Date getDateMeas() {
		return measDate;
	}

	public void setDateMeas(Date measDate) {
		this.measDate = measDate;
	}

	public String getMeasVal() {
		return measVal;
	}

	public void setMeasVal(String measVal) {
		this.measVal = measVal;
	}

	public String getMeasMin() {
		return measMin;
	}

	public void setMeasMin(String measMin) {
		this.measMin = measMin;
	}

	public String getMeasMax() {
		return measMax;
	}

	public void setMeasMax(String measMax) {
		this.measMax = measMax;
	}

	public Tests getTest() {
		return this.test;
	}

	public void setTest(Tests test) {
		this.test = test;
	}

	public int getMeasureNumber() {
		return measureNumber;
	}

	public void setMeasureNumber(int measureNumber) {
		this.measureNumber = measureNumber;
	}

	public Date getMeasDate() {
		return measDate;
	}

	public void setMeasDate(Date measDate) {
		this.measDate = measDate;
	}

	public SimpleStringProperty measNameProperty(){
		return new SimpleStringProperty(getMeasName());
	}

	public SimpleStringProperty measMinProperty(){
		return new SimpleStringProperty(getMeasMin());
	}

	public SimpleStringProperty measMaxProperty(){
		return new SimpleStringProperty(getMeasMax());
	}

	public SimpleStringProperty measValProperty(){
		return new SimpleStringProperty(getMeasVal());
	}

	public SimpleStringProperty measStatusProperty(){
		String status = "FAIL";
		if (measMin.equals("\"\"") &&  measMax.equals("\"\"")) {
			status = "PASS";
		} else if((measMin.isEmpty() &&  measMax.isEmpty())){
			status = "PASS";
		} else {
			try {
				double measValTmp = Double.parseDouble(measVal);
				double measMinTmp = Double.parseDouble(measMin);
				double measMaxTmp = Double.parseDouble(measMax);
				if (measMinTmp <= measValTmp || measValTmp <= measMaxTmp){
					status = "PASS";
				}
			} catch (Exception e){
				if (measMin.equals(measMax) &&  measMin.equals(measVal)){
					status = "PASS";
				}
			}
		}
		return new SimpleStringProperty(status);
	}

	@Override
	public String toString() {
		return "Measurements{" +
				"id=" + id +
				", measName='" + measName + '\'' +
				", measDate=" + measDate +
				", measMin='" + measMin + '\'' +
				", measVal='" + measVal + '\'' +
				", measMax='" + measMax + '\'' +
				", measureNumber=" + measureNumber +
				", test=" + test +
				'}';
	}
}
