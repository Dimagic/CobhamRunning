package net.ddns.dimag.cobhamrunning.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.ddns.dimag.cobhamrunning.services.DeviceInfoService;

@Entity
@Table(name = "device", uniqueConstraints = { @UniqueConstraint(columnNames = "asis"),
		@UniqueConstraint(columnNames = "sn") })
public class Device {
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

	@NotNull
    @Size(max = 4)
    @Column(name = "asis", unique = true)
	private StringProperty asis;

	@NotNull
    @Size(max = 17)
	@Column(name = "mac", unique = true)
	private StringProperty mac;

	@NotNull
    @Size(max = 8)
	@Column(name = "sn", unique = true)
	private StringProperty sn;

	public Device() {
		this.asis = new SimpleStringProperty(null);
		this.mac = new SimpleStringProperty(null);
		this.sn = new SimpleStringProperty(null);
	}

	public Device(String asis, String mac) {
		this.asis = new SimpleStringProperty(asis);
		this.mac = new SimpleStringProperty(mac);
		this.sn = new SimpleStringProperty(null);
	}

	public Device(String asis, String mac, String sn) {
		this.asis = new SimpleStringProperty(asis);
		this.mac = new SimpleStringProperty(mac);
		this.sn = new SimpleStringProperty(sn);
	}

	@OneToOne(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
	private DeviceInfo deviceInfo;
	
	public DeviceInfo getDeviceInfo() {
		DeviceInfoService deviceInfoService = new DeviceInfoService();
		
//		DeviceInfo deviceInfo = (DeviceInfo)sessionObj.load(DeviceInfo.class, new Integer(emp_id1));
		DeviceInfo deviceInfo = deviceInfoService.findDeviceInfoById((long) 1);
		System.out.println(deviceInfo);
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
		this.deviceInfo.setDevice(this);
	}

	@Access(value = AccessType.PROPERTY)
	public String getAsis() {
		return asis.get();
	}

	@Access(value = AccessType.PROPERTY)
	public String getMac() {
		return mac.get();
	}

	@Access(value = AccessType.PROPERTY)
	public String getSn() {
		return sn.get();
	}

	public void setAsis(String value) {
		this.asis.set(value);
	}

	public void setMac(String value) {
		this.mac.set(value);
	}

	public void setSn(String value) {
		this.sn.set(value);
	}

	/*
	 * Add tests to device
	 */
	@Column
	@ElementCollection(targetClass = Long.class)
	private Set<Tests> tests = new HashSet<Tests>();

	@OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<Tests> getTests() {
		return this.tests;
	}

	public void setTests(Set<Tests> tests) {
		this.tests = tests;
	}

	public void addTests(Tests tests) {
		tests.setDevice(this);
		getTests().add(tests);
	}

	public void removeTests(Tests tests) {
		getTests().remove(tests);
	}

//	@ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "article_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//	private ArticleHeaders article;
//
////	@ManyToOne
////	@JoinColumn(name = "article_id")
//	public ArticleHeaders getArticle() {
//		return this.article;
//	}
//
//	public void setArticle(ArticleHeaders article) {
//		this.article = article;
//		// ArticleHeadersDao articleHeadersDao = new ArticleHeadersDao();
//		// List<ArticleHeaders> articleHeaders =
//		// articleHeadersDao.findArticleHeadersByName(article);
//		// if (articleHeaders.size() == 0) {
//		// ArticleHeaders newArticle = new ArticleHeaders();
//		// System.out.println(article);
//		//// System.out.println(this);
//		// newArticle.setName(article);
//		//// newArticle.addDevice(this);
//		// new ArticleHeadersService().saveArticle(newArticle);
//		// System.out.println(newArticle.getName());
//		// } else {
//		// this.article.setName(articleHeaders.get(0).getName());
//		// }
//	}
	
	@OneToOne(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
	private ShippingSystem shippingSystem;
	
	public ShippingSystem getShippingSystem() {
		return shippingSystem;
	}

	public void setShippingSystem(ShippingSystem shippingSystem) {
		this.shippingSystem = shippingSystem;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((article.getName() == null) ? 0 : article.getName().hashCode());
//		result = prime * result + ((asis.getName() == null) ? 0 : asis.getName().hashCode());
//		return result;
//	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Device other = (Device) obj;
//		if (article.getName() == null) {
//			if (other.article.getName() != null)
//				return false;
//		} else if (!article.getName().equals(other.article.getName()))
//			return false;
//		if (asis.getValue() == null) {
//			if (other.asis.getValue() != null)
//				return false;
//		} else if (!asis.getValue().equals(other.asis.getValue()))
//			return false;
//		return true;
//	}

//	@Override
//	public String toString() {
//		return "Device [id=" + id + ", asis=" + asis.getValue() + ", mac=" + mac.getValue() + ", sn=" + sn.getValue()
//				+ ", article=" + article.getName() + "]";
//	}

}
