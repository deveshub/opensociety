package com.bits.opensociety.domain;

import com.bits.opensociety.domain.enumeration.MemberType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Member.
 */
@Table("member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("mobile")
    private String mobile;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Column("member_type")
    private MemberType memberType;

    @Transient
    private User user;

    @Transient
    @JsonIgnoreProperties(value = { "flats", "flat" }, allowSetters = true)
    private Flat flat;

    @Column("user_id")
    private Long userId;

    @Column("flat_id")
    private Long flatId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Member id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Member name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return this.mobile;
    }

    public Member mobile(String mobile) {
        this.setMobile(mobile);
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return this.email;
    }

    public Member email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MemberType getMemberType() {
        return this.memberType;
    }

    public Member memberType(MemberType memberType) {
        this.setMemberType(memberType);
        return this;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Member user(User user) {
        this.setUser(user);
        return this;
    }

    public Flat getFlat() {
        return this.flat;
    }

    public void setFlat(Flat flat) {
        this.flat = flat;
        this.flatId = flat != null ? flat.getId() : null;
    }

    public Member flat(Flat flat) {
        this.setFlat(flat);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getFlatId() {
        return this.flatId;
    }

    public void setFlatId(Long flat) {
        this.flatId = flat;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        return id != null && id.equals(((Member) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Member{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", email='" + getEmail() + "'" +
            ", memberType='" + getMemberType() + "'" +
            "}";
    }
}
