package br.com.ultimoandar.contracts.entity;

import br.com.ultimoandar.contracts.entity.enums.ContractStatus;
import br.com.ultimoandar.contracts.entity.enums.GuaranteeType;
import br.com.ultimoandar.contracts.entity.enums.LeaseTermType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "lease_contracts")
public class LeaseContract extends AuditableEntity {

    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private RentalProperty property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "monthly_rent", nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "rent_due_day", nullable = false)
    private Integer rentDueDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false)
    private LeaseTermType termType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "adjustment_index")
    private String adjustmentIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "guarantee_type", nullable = false)
    private GuaranteeType guaranteeType;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "extra_clauses", columnDefinition = "text")
    private String extraClauses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.ACTIVE;

    @Column(name = "generated_at")
    private Instant generatedAt;

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public RentalProperty getProperty() {
        return property;
    }

    public void setProperty(RentalProperty property) {
        this.property = property;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public Integer getRentDueDay() {
        return rentDueDay;
    }

    public void setRentDueDay(Integer rentDueDay) {
        this.rentDueDay = rentDueDay;
    }

    public LeaseTermType getTermType() {
        return termType;
    }

    public void setTermType(LeaseTermType termType) {
        this.termType = termType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getAdjustmentIndex() {
        return adjustmentIndex;
    }

    public void setAdjustmentIndex(String adjustmentIndex) {
        this.adjustmentIndex = adjustmentIndex;
    }

    public GuaranteeType getGuaranteeType() {
        return guaranteeType;
    }

    public void setGuaranteeType(GuaranteeType guaranteeType) {
        this.guaranteeType = guaranteeType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getExtraClauses() {
        return extraClauses;
    }

    public void setExtraClauses(String extraClauses) {
        this.extraClauses = extraClauses;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
