package com.myprop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A MyAccount.
 */
@Entity
@Table(name = "mp_account")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "myaccount")
public class MyAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name_surname", length = 100, nullable = false)
    private String name_surname;

    @Column(name = "mobile")
    private String mobile;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;


    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "mp_account_unit",
               joinColumns = @JoinColumn(name="my_accounts_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="units_id", referencedColumnName="ID"))
    private Set<Unit> units = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName_surname() {
        return name_surname;
    }

    public void setName_surname(String name_surname) {
        this.name_surname = name_surname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Unit> getUnits() {
        return units;
    }

    public void setUnits(Set<Unit> units) {
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyAccount myAccount = (MyAccount) o;
        if(myAccount.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, myAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MyAccount{" +
            "id=" + id +
            ", name_surname='" + name_surname + "'" +
            ", mobile='" + mobile + "'" +
            '}';
    }
}
