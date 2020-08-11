package net.ddns.dimag.cobhamrunning.models;

import java.util.*;

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

	public Measurements(){

	}

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

	@Column(name = "measStatus")
	private int measStatus;

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
		setMeasDate(new Date());
		this.measName = measName;
	}

	public void setMeasName(String measName, int measNum) {
		setMeasDate(new Date());
		setMeasureNumber(measNum);
		this.measName = measName;
	}

//	public java.util.Date getDateMeas() {
//		return measDate;
//	}
//
//	public void setDateMeas(Date measDate) {
//		this.measDate = measDate;
//	}

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

	public int getMeasStatus() {
		return measStatus;
	}

	public String getStringMeasStatus(){
		return getMeasStatus() == 1 ? "PASS" : "FAIL";
	}

	public void setMeasStatus(int status){
		this.measStatus = status;
	}

	public void setMeasStatus() {
		int status = 0;
		try{
			if (measVal.equals(measMin) || measVal.equals(measMax)) {
				status = 1;
			} else if (measMin.equals("\"\"") &&  measMax.equals("\"\"")) {
				status = 1;
			} else if((measMin.isEmpty() &&  measMax.isEmpty())){
				status = 1;
			} else {
				try {
					double measValTmp = Double.parseDouble(measVal);
					double measMinTmp = Double.parseDouble(measMin);
					double measMaxTmp = Double.parseDouble(measMax);
					if (measMinTmp <= measValTmp && measValTmp <= measMaxTmp){
						status = 1;
					}
				} catch (Exception e){
					if (measMin.equals(measMax) &&  measMin.equals(measVal)){
						status = 1;
					}
				}
			}
		} catch (NullPointerException ignored){

		}
		this.measStatus = status;
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
		return new SimpleStringProperty(getMeasStatus() == 1 ? "PASS" : "FAIL");


	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Measurements{");
		sb.append("id=").append(id);
		sb.append(", measName='").append(measName).append('\'');
		sb.append(", measDate=").append(measDate);
		sb.append(", measMin='").append(measMin).append('\'');
		sb.append(", measVal='").append(measVal).append('\'');
		sb.append(", measMax='").append(measMax).append('\'');
		sb.append(", measureNumber=").append(measureNumber);
		sb.append(", measStatus=").append(measStatus);
		sb.append('}');
		return sb.toString();
	}
}
