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

	@Size(max = 4)
	@Column(name = "asis", nullable = false, unique = true)
	private String asis;

	public String getAsis() {
		return asis;
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

}
