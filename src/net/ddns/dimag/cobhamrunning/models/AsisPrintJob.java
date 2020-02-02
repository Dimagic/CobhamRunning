package net.ddns.dimag.cobhamrunning.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javafx.beans.property.SimpleStringProperty;

@Entity
@Table(name = "asisPrintJob")
public class AsisPrintJob {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public Long getId() {
		return id;
	}

	public AsisPrintJob(){
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article")
	private ArticleHeaders article;
	
	public ArticleHeaders getArticle() {
		return article;
	}

	public void setArticle(ArticleHeaders article) {
		this.article = article;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateprint")
	private Date datePrint;

	public Date getDatePrint() {
		return datePrint;
	}

	public void setDatePrint(Date datePrint) {
		this.datePrint = datePrint;
	}

	@OneToMany(mappedBy = "printJob", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Asis> asisSet = new HashSet<Asis>();
	
	public Set<Asis> getAsisSet() {
		return this.asisSet;
	}

	public void setAsisSet(Set<Asis> asis) {
		this.asisSet = asis;
	}

	@Column(nullable = false, length = 4)
	private String start;

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}
	
	@Column(nullable = false, length = 4)
	private String stop;

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}
	
	@Column(nullable = false)
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public SimpleStringProperty articleProperty() {
		return article.articleProperty();
	}
	
	public SimpleStringProperty desctiptionProperty() {
		return article.shortDescriptProperty();
	}
	
	public SimpleStringProperty startProperty() {
		return new SimpleStringProperty(getStart());
	}
	
	public SimpleStringProperty stopProperty() {
		return new SimpleStringProperty(getStop());
	}
	
	public SimpleStringProperty countProperty() {
		return new SimpleStringProperty(Integer.toString(getCount()));
	}
	
	public SimpleStringProperty dateCreateProperty() {
		return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").format(getDateCreate()));
	}
	
	public SimpleStringProperty datePrintProperty() {
		try {
			return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").format(getDatePrint()));
		} catch (NullPointerException e) {}
		return new SimpleStringProperty("");	
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AsisPrintJob [id=");
		builder.append(id);
		builder.append(", article=");
		builder.append(article);
		builder.append(", dateCreate=");
		builder.append(dateCreate);
		builder.append(", datePrint=");
		builder.append(datePrint);
		builder.append(", asisSet=");
		builder.append(asisSet);
		builder.append(", start=");
		builder.append(start);
		builder.append(", stop=");
		builder.append(stop);
		builder.append(", count=");
		builder.append(count);
		builder.append("]");
		return builder.toString();
	}

	
}
