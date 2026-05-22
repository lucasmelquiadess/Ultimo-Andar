package br.com.ultimoandar.contracts.entity;

import br.com.ultimoandar.contracts.entity.enums.AddendumType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contract_addendums")
public class ContractAddendum extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private LeaseContract contract;

    @Enumerated(EnumType.STRING)
    @Column(name = "addendum_type", nullable = false)
    private AddendumType addendumType;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "addendum_date", nullable = false)
    private LocalDate addendumDate;

    @Column(name = "new_monthly_rent", precision = 14, scale = 2)
    private BigDecimal newMonthlyRent;

    @Column(name = "new_term")
    private String newTerm;

    @Column(name = "new_end_date")
    private LocalDate newEndDate;

    @Column(name = "specific_changes", columnDefinition = "text")
    private String specificChanges;

    @Column(columnDefinition = "text")
    private String observations;

    public LeaseContract getContract() {
        return contract;
    }

    public void setContract(LeaseContract contract) {
        this.contract = contract;
    }

    public AddendumType getAddendumType() {
        return addendumType;
    }

    public void setAddendumType(AddendumType addendumType) {
        this.addendumType = addendumType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAddendumDate() {
        return addendumDate;
    }

    public void setAddendumDate(LocalDate addendumDate) {
        this.addendumDate = addendumDate;
    }

    public BigDecimal getNewMonthlyRent() {
        return newMonthlyRent;
    }

    public void setNewMonthlyRent(BigDecimal newMonthlyRent) {
        this.newMonthlyRent = newMonthlyRent;
    }

    public String getNewTerm() {
        return newTerm;
    }

    public void setNewTerm(String newTerm) {
        this.newTerm = newTerm;
    }

    public LocalDate getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(LocalDate newEndDate) {
        this.newEndDate = newEndDate;
    }

    public String getSpecificChanges() {
        return specificChanges;
    }

    public void setSpecificChanges(String specificChanges) {
        this.specificChanges = specificChanges;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
