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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javafx.beans.property.SimpleStringProperty;

@Entity
@Table(name = "labelTemplate")
public class LabelTemplate {

	public LabelTemplate() {
	}

	public LabelTemplate(String name, String template) {
		this.name = name;
		this.template = template;
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

	@Column(name = "name", nullable = false, unique = true, length = 60)
	private String name;

	@Column(name = "template", nullable = false, length = 1024)
	private String template;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<ArticleHeaders> articleHeaders = new HashSet<ArticleHeaders>(0);
	
	
	@JoinTable(name = "articleHeaders_labelTemplate", joinColumns = {
	@JoinColumn(name = "labelTemplate_id", nullable = false, updatable = false) }, inverseJoinColumns = {
	@JoinColumn(name = "articleHeaders_id", nullable = false, updatable = false) })	
	public Set<ArticleHeaders> getArticleHeaders() {
		return articleHeaders;
	}

	public void setArticleHeaders(Set<ArticleHeaders> articleHeaders) {
		this.articleHeaders = articleHeaders;
	}

	public SimpleStringProperty nameProperty() {
		return new SimpleStringProperty(getName());
	}

	public SimpleStringProperty templateProperty() {
		return new SimpleStringProperty(getTemplate());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LabelTemplate [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (template != null) {
			builder.append("template=");
			builder.append(template);
			builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}

}
