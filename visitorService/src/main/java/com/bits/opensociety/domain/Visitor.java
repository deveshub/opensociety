package com.bits.opensociety.domain;

import com.bits.opensociety.domain.enumeration.VisitorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Visitor.
 */
@Entity
@Table(name = "visitor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Visitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "visitor_type", nullable = false)
    private VisitorType visitorType;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "in_time")
    private Instant inTime;

    @Column(name = "out_time")
    private Instant outTime;

    @ManyToMany
    @JoinTable(
        name = "rel_visitor__visiting_flat",
        joinColumns = @JoinColumn(name = "visitor_id"),
        inverseJoinColumns = @JoinColumn(name = "visiting_flat_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "visitors" }, allowSetters = true)
    private Set<VisitingFlat> visitingFlats = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Visitor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VisitorType getVisitorType() {
        return this.visitorType;
    }

    public Visitor visitorType(VisitorType visitorType) {
        this.setVisitorType(visitorType);
        return this;
    }

    public void setVisitorType(VisitorType visitorType) {
        this.visitorType = visitorType;
    }

    public String getMobile() {
        return this.mobile;
    }

    public Visitor mobile(String mobile) {
        this.setMobile(mobile);
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVehicleNumber() {
        return this.vehicleNumber;
    }

    public Visitor vehicleNumber(String vehicleNumber) {
        this.setVehicleNumber(vehicleNumber);
        return this;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public Visitor address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Instant getInTime() {
        return this.inTime;
    }

    public Visitor inTime(Instant inTime) {
        this.setInTime(inTime);
        return this;
    }

    public void setInTime(Instant inTime) {
        this.inTime = inTime;
    }

    public Instant getOutTime() {
        return this.outTime;
    }

    public Visitor outTime(Instant outTime) {
        this.setOutTime(outTime);
        return this;
    }

    public void setOutTime(Instant outTime) {
        this.outTime = outTime;
    }

    public Set<VisitingFlat> getVisitingFlats() {
        return this.visitingFlats;
    }

    public void setVisitingFlats(Set<VisitingFlat> visitingFlats) {
        this.visitingFlats = visitingFlats;
    }

    public Visitor visitingFlats(Set<VisitingFlat> visitingFlats) {
        this.setVisitingFlats(visitingFlats);
        return this;
    }

    public Visitor addVisitingFlat(VisitingFlat visitingFlat) {
        this.visitingFlats.add(visitingFlat);
        visitingFlat.getVisitors().add(this);
        return this;
    }

    public Visitor removeVisitingFlat(VisitingFlat visitingFlat) {
        this.visitingFlats.remove(visitingFlat);
        visitingFlat.getVisitors().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visitor)) {
            return false;
        }
        return id != null && id.equals(((Visitor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Visitor{" +
            "id=" + getId() +
            ", visitorType='" + getVisitorType() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", vehicleNumber='" + getVehicleNumber() + "'" +
            ", address='" + getAddress() + "'" +
            ", inTime='" + getInTime() + "'" +
            ", outTime='" + getOutTime() + "'" +
            "}";
    }
}
