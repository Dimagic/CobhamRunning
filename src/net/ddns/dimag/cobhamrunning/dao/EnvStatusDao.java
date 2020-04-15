package net.ddns.dimag.cobhamrunning.dao;

import javax.persistence.*;

@Entity
@Table(name = "envstatus", uniqueConstraints = { @UniqueConstraint(columnNames = "status")})
public class EnvStatusDao implements UniversalDao {
    public EnvStatusDao() {
    }

    public EnvStatusDao(String status) {
        this.status = status;
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

    @Column(name = "status", length = 64, nullable = false, unique = true)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
