package com.bits.opensociety.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A VisitingFlat.
 */
@Entity
@Table(name = "visiting_flat")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class VisitingFlat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "flat_no", nullable = false)
    private String flatNo;

    @ManyToMany(mappedBy = "visitingFlats")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "visitingFlats" }, allowSetters = true)
    private Set<Visitor> visitors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VisitingFlat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlatNo() {
        return this.flatNo;
    }

    public VisitingFlat flatNo(String flatNo) {
        this.setFlatNo(flatNo);
        return this;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public Set<Visitor> getVisitors() {
        return this.visitors;
    }

    public void setVisitors(Set<Visitor> visitors) {
        if (this.visitors != null) {
            this.visitors.forEach(i -> i.removeVisitingFlat(this));
        }
        if (visitors != null) {
            visitors.forEach(i -> i.addVisitingFlat(this));
        }
        this.visitors = visitors;
    }

    public VisitingFlat visitors(Set<Visitor> visitors) {
        this.setVisitors(visitors);
        return this;
    }

    public VisitingFlat addVisitor(Visitor visitor) {
        this.visitors.add(visitor);
        visitor.getVisitingFlats().add(this);
        return this;
    }

    public VisitingFlat removeVisitor(Visitor visitor) {
        this.visitors.remove(visitor);
        visitor.getVisitingFlats().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VisitingFlat)) {
            return false;
        }
        return id != null && id.equals(((VisitingFlat) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitingFlat{" +
            "id=" + getId() +
            ", flatNo='" + getFlatNo() + "'" +
            "}";
    }
}
