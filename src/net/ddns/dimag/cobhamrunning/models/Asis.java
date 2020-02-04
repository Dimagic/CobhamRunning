package net.ddns.dimag.cobhamrunning.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import javafx.beans.property.SimpleStringProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "asis")
public class Asis {
	
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

	public Asis() {
	}

	public Asis(String asis) {
		this.asis = asis;
		this.articleHeaders = null;
	}

	public Asis(String asis, ArticleHeaders articleHeaders) {
		this.asis = asis;
		this.articleHeaders = articleHeaders;
	}

	@Column(name = "asis", length = 4, nullable = false, unique = true)
	private String asis;

	public String getAsis() {
		return asis;
	}

	public SimpleStringProperty asisProperty() {
		return new SimpleStringProperty(getAsis());
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
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "is_imported")
	private Boolean isImported;

	public Boolean getImported() {
		return isImported;
	}

	public void setImported(Boolean imported) {
		isImported = imported;
	}

	public Boolean getNeedmac() {
		return isImported;
	}

	public void setNeedmac(Boolean needmac) {
		this.isImported = needmac;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "articleHeaders_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private ArticleHeaders articleHeaders;

	public ArticleHeaders getArticleHeaders() {
		return articleHeaders;
	}

	public void setArticleHeaders(ArticleHeaders articleHeaders) {
		this.articleHeaders = articleHeaders;
	}
	
	@ManyToOne
    @JoinColumn(name = "printJob_id")
	private AsisPrintJob printJob;
	
	public AsisPrintJob getPrintJob() {
		return printJob;
	}

	public void setPrintJob(AsisPrintJob printJob) {
		this.printJob = printJob;
	}

	@OneToOne(mappedBy = "asis", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = true)
	private MacAddress macAddress;

	public MacAddress getMacAddress() {
		return macAddress;
	}
	
	public void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}

	@OneToOne(mappedBy = "asis", cascade = CascadeType.ALL,
			fetch = FetchType.LAZY, optional = true)
	private Device device;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Asis{");
		sb.append("id=").append(id);
		sb.append(", asis='").append(asis).append('\'');
		sb.append(", dateCreate=").append(dateCreate);
		sb.append(", isImported=").append(isImported);
		sb.append(", articleHeaders=").append(articleHeaders.getArticle());
		sb.append(", printJob=").append(printJob.getId());
		sb.append(", macAddress=").append(macAddress.getMac());
		sb.append(", device=").append(device.getDeviceInfo());
		sb.append('}');
		return sb.toString();
	}
}
