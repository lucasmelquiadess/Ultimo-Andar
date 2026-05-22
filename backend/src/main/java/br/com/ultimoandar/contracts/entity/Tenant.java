package br.com.ultimoandar.contracts.entity;

import br.com.ultimoandar.contracts.entity.enums.PersonType;
import br.com.ultimoandar.contracts.security.SensitiveStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenants")
public class Tenant extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false)
    private PersonType personType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String document;

    @Column(name = "identity_number")
    @Convert(converter = SensitiveStringConverter.class)
    private String identityNumber;

    private String nationality;

    @Column(name = "marital_status")
    private String maritalStatus;

    private String profession;

    @Embedded
    private Address address = new Address();

    private String phone;
    private String email;

    @Column(name = "spouse_data", columnDefinition = "text")
    @Convert(converter = SensitiveStringConverter.class)
    private String spouseData;

    @Column(name = "guarantor_data", columnDefinition = "text")
    @Convert(converter = SensitiveStringConverter.class)
    private String guarantorData;

    @Column(columnDefinition = "text")
    @Convert(converter = SensitiveStringConverter.class)
    private String notes;

    @Column(nullable = false)
    private boolean active = true;

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpouseData() {
        return spouseData;
    }

    public void setSpouseData(String spouseData) {
        this.spouseData = spouseData;
    }

    public String getGuarantorData() {
        return guarantorData;
    }

    public void setGuarantorData(String guarantorData) {
        this.guarantorData = guarantorData;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
