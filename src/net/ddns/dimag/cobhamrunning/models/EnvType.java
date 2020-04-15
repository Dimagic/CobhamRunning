package net.ddns.dimag.cobhamrunning.models;

import javax.persistence.*;

@Entity
@Table(name = "envtype", uniqueConstraints = { @UniqueConstraint(columnNames = "type")})
public class EnvType {
    public EnvType() {
    }

    public EnvType(String type) {
        this.type = type;
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

    @Column(name = "type", length = 64, nullable = false, unique = true)
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
