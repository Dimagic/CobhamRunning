package net.ddns.dimag.cobhamrunning.models.environment;

import javafx.beans.property.SimpleStringProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "envservice", uniqueConstraints = { @UniqueConstraint(columnNames = "name")})
public class EnvService {

    public EnvService() {
    }

    public EnvService(String name) {
        this.name = name;
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

    @Column(name = "name", length = 64, nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleStringProperty nameProperty(){
        return new SimpleStringProperty(getName());
    }
}
