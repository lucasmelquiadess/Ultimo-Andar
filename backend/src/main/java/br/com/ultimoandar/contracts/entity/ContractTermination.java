package br.com.ultimoandar.contracts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contract_terminations")
public class ContractTermination extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private LeaseContract contract;

    @Column(name = "termination_date", nullable = false)
    private LocalDate terminationDate;

    @Column(nullable = false, columnDefinition = "text")
    private String reason;

    @Column(name = "has_pending_debts", nullable = false)
    private boolean hasPendingDebts;

    @Column(name = "penalty_amount", precision = 14, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(name = "proportional_rent_amount", precision = 14, scale = 2)
    private BigDecimal proportionalRentAmount = BigDecimal.ZERO;

    @Column(name = "pending_charges_amount", precision = 14, scale = 2)
    private BigDecimal pendingChargesAmount = BigDecimal.ZERO;

    @Column(name = "repairs_amount", precision = 14, scale = 2)
    private BigDecimal repairsAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "text")
    private String observations;

    @Column(name = "additional_statements", columnDefinition = "text")
    private String additionalStatements;

    public LeaseContract getContract() {
        return contract;
    }

    public void setContract(LeaseContract contract) {
        this.contract = contract;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isHasPendingDebts() {
        return hasPendingDebts;
    }

    public void setHasPendingDebts(boolean hasPendingDebts) {
        this.hasPendingDebts = hasPendingDebts;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public BigDecimal getProportionalRentAmount() {
        return proportionalRentAmount;
    }

    public void setProportionalRentAmount(BigDecimal proportionalRentAmount) {
        this.proportionalRentAmount = proportionalRentAmount;
    }

    public BigDecimal getPendingChargesAmount() {
        return pendingChargesAmount;
    }

    public void setPendingChargesAmount(BigDecimal pendingChargesAmount) {
        this.pendingChargesAmount = pendingChargesAmount;
    }

    public BigDecimal getRepairsAmount() {
        return repairsAmount;
    }

    public void setRepairsAmount(BigDecimal repairsAmount) {
        this.repairsAmount = repairsAmount;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getAdditionalStatements() {
        return additionalStatements;
    }

    public void setAdditionalStatements(String additionalStatements) {
        this.additionalStatements = additionalStatements;
    }
}
