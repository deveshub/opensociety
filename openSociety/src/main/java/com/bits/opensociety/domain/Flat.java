package com.bits.opensociety.domain;

import com.bits.opensociety.domain.enumeration.ResidentialStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Flat.
 */
@Table("flat")
public class Flat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("flat_no")
    private String flatNo;

    @Column("residential_status")
    private ResidentialStatus residentialStatus;

    @Column("flat_area")
    private Integer flatArea;

    @Transient
    @Transient
    @JsonIgnoreProperties(value = { "user", "flat" }, allowSetters = true)
    private Set<Member> flats = new HashSet<>();

    @Transient
    @Transient
    private Society flat;

    @Column("flat_id")
    private Long flatId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Flat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlatNo() {
        return this.flatNo;
    }

    public Flat flatNo(String flatNo) {
        this.setFlatNo(flatNo);
        return this;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public ResidentialStatus getResidentialStatus() {
        return this.residentialStatus;
    }

    public Flat residentialStatus(ResidentialStatus residentialStatus) {
        this.setResidentialStatus(residentialStatus);
        return this;
    }

    public void setResidentialStatus(ResidentialStatus residentialStatus) {
        this.residentialStatus = residentialStatus;
    }

    public Integer getFlatArea() {
        return this.flatArea;
    }

    public Flat flatArea(Integer flatArea) {
        this.setFlatArea(flatArea);
        return this;
    }

    public void setFlatArea(Integer flatArea) {
        this.flatArea = flatArea;
    }

    public Set<Member> getFlats() {
        return this.flats;
    }

    public void setFlat(Society society) {
        this.flat = society;
        this.flatId = society != null ? society.getId() : null;
    }

    public Flat flats(Set<Member> members) {
        this.setFlats(members);
        return this;
    }

    public Flat addFlat(Member member) {
        this.flats.add(member);
        member.setFlat(this);
        return this;
    }

    public Flat removeFlat(Member member) {
        this.flats.remove(member);
        member.setFlat(null);
        return this;
    }

    public Society getFlat() {
        return this.flat;
    }

    public void setFlat(Society society) {
        this.flat = society;
        this.flatId = society != null ? society.getId() : null;
    }

    public Flat flat(Society society) {
        this.setFlat(society);
        return this;
    }

    public Long getFlatId() {
        return this.flatId;
    }

    public void setFlatId(Long society) {
        this.flatId = society;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Flat)) {
            return false;
        }
        return id != null && id.equals(((Flat) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Flat{" +
            "id=" + getId() +
            ", flatNo='" + getFlatNo() + "'" +
            ", residentialStatus='" + getResidentialStatus() + "'" +
            ", flatArea=" + getFlatArea() +
            "}";
    }
}
