package br.com.ultimoandar.contracts.entity;

import br.com.ultimoandar.contracts.entity.enums.PropertyStatus;
import br.com.ultimoandar.contracts.entity.enums.PropertyType;
import br.com.ultimoandar.contracts.security.SensitiveStringConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rental_properties")
public class RentalProperty extends AuditableEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Embedded
    private Address address = new Address();

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "monthly_rent", nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyRent = BigDecimal.ZERO;

    @Column(name = "condominium_fee", precision = 14, scale = 2)
    private BigDecimal condominiumFee = BigDecimal.ZERO;

    @Column(name = "iptu_value", precision = 14, scale = 2)
    private BigDecimal iptuValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "internal_notes", columnDefinition = "text")
    @Convert(converter = SensitiveStringConverter.class)
    private String internalNotes;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyPhoto> photos = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public BigDecimal getCondominiumFee() {
        return condominiumFee;
    }

    public void setCondominiumFee(BigDecimal condominiumFee) {
        this.condominiumFee = condominiumFee;
    }

    public BigDecimal getIptuValue() {
        return iptuValue;
    }

    public void setIptuValue(BigDecimal iptuValue) {
        this.iptuValue = iptuValue;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public List<PropertyPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PropertyPhoto> photos) {
        this.photos = photos;
    }
}
