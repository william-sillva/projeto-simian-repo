package com.developer.simianproject.api.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "dna_base")
public class DnaBase implements Serializable {

    private static final long serialVersionUID = 2985874668680345431L;

    private Long id;

    private Date dtInclude;

    private String dna;

    private Boolean isSimian;

    public DnaBase() { }

    public DnaBase(Boolean isSimian) {
        this.isSimian = isSimian;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "dt_include", nullable = false)
    public Date getDtInclude() {
        return dtInclude;
    }

    public void setDtInclude(Date dtInclude) {
        this.dtInclude = dtInclude;
    }

    @Column(name = "dna", nullable = false)
    public String getDna() {
        return dna;
    }

    public void setDna(String dna) {
        this.dna = dna;
    }

    @Column(name = "is_simian", nullable = false)
    public Boolean getSimian() {
        return isSimian;
    }

    public void setSimian(Boolean simian) {
        isSimian = simian;
    }

    @Override
    public String toString() {
        return "DnaBase {" +
                    "id=" + getId() +
                    ", dtInclude=" + getDtInclude() +
                    ", dna=" + getDna() +
                    ", isSimian=" + getSimian() +
                "}";
    }
}
