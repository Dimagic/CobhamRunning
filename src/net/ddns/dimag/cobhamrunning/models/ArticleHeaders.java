package net.ddns.dimag.cobhamrunning.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

@Entity
@Table(name = "articleHeaders", uniqueConstraints = { @UniqueConstraint(columnNames = "article") })
public class ArticleHeaders {	
	public ArticleHeaders() {
	}
	
	public ArticleHeaders(String name, String shortDescript, String longDescript, String revision) {
		this.name = name;
		this.revision = revision;
		this.shortDescript = shortDescript;
		this.longDescript = longDescript;
	}
		
	public ArticleHeaders(String name, String shortDescript, String longDescript, String revision, Set<LabelTemplate> labelTemplates) {
		this.name = name;
		this.revision = revision;
		this.shortDescript = shortDescript;
		this.longDescript = longDescript;
		this.labelTemplates = labelTemplates;
	}

	public ArticleHeaders(ArticleHeaders another){
        this.article = another.getArticle();
        this.shortDescript = another.getShortDescript();
        this.longDescript = another.getLongDescript();
        this.labelTemplates = another.getTemplates();
        this.needmac = another.getNeedmac();
    }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false, length = 60)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setArticle();
	}

	@Column(name = "article", nullable = false, length = 60)
	private String article;

	public String getArticle() {
		return article;
	}

	public void setArticle() {
		this.article = String.format("%s%s", this.getName(), this.getRevision());
	}

	@Column(name = "short_descript", nullable = false, length = 60)
	private String shortDescript;

	public String getShortDescript() {
		return shortDescript;
	}

	public void setShortDescript(String shortDescript) {
		this.shortDescript = shortDescript;
	}

	@Column(name = "long_descript", nullable = false, length = 256)
	private String longDescript;

	public String getLongDescript() {
		return longDescript;
	}

	public void setLongDescript(String longDescript) {
		this.longDescript = longDescript;
	}

	@Column(name = "revision", nullable = false, length = 1)
	private String revision;

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
		setArticle();
	}

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "need_mac")
	private Boolean needmac;

	public Boolean getNeedmac() {
		return needmac;
	}

	public void setNeedmac(Boolean needmac) {
		this.needmac = needmac;
	}

	@Column
	@ElementCollection(targetClass = Long.class)
	@OneToMany(mappedBy = "asis", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Asis> asisset = new HashSet<Asis>(0);

	public Set<Asis> getAsis() {
		return this.asisset;
	}

	public void setAsis(Set<Asis> asis) {
		this.asisset = asis;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<LabelTemplate> labelTemplates = new HashSet<LabelTemplate>();

	
	@JoinTable(name = "articleHeaders_labelTemplate", joinColumns = {
	@JoinColumn(name = "articleHeaders_id", nullable = false, updatable = false) }, inverseJoinColumns = {
	@JoinColumn(name = "labelTemplate_id", nullable = false, updatable = false) })	
	public Set<LabelTemplate> getTemplates() {
		return this.labelTemplates;
	}

	public void setTemplates(Set<LabelTemplate> templates) {
		this.labelTemplates = templates;
	}
	
	@OneToMany(mappedBy = "article")
    private List<AsisPrintJob> asisPrintJob = new ArrayList<AsisPrintJob>();

	public SimpleStringProperty articleProperty() {
		return new SimpleStringProperty(getArticle());
	}
	
	public SimpleStringProperty nameProperty() {
		return new SimpleStringProperty(getName());
	}

	public SimpleStringProperty revisionProperty() {
		return new SimpleStringProperty(getRevision());
	}

	public SimpleStringProperty shortDescriptProperty() {
		return new SimpleStringProperty(getShortDescript());
	}

	public SimpleStringProperty longDescriptProperty() {
		return new SimpleStringProperty(getLongDescript());
	}

	public SimpleBooleanProperty isNeedMacProperty() {
		return new SimpleBooleanProperty(getNeedmac());
	}

    @Override
    public String toString() {
        return "ArticleHeaders{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", article='" + article + '\'' +
                ", shortDescript='" + shortDescript + '\'' +
                ", longDescript='" + longDescript + '\'' +
                ", revision='" + revision + '\'' +
                ", needmac=" + needmac +
                '}';
    }
}
