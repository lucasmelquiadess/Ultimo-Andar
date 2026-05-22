package br.com.ultimoandar.contracts.mapper;

import br.com.ultimoandar.contracts.dto.AddendumDto;
import br.com.ultimoandar.contracts.dto.ContractDto;
import br.com.ultimoandar.contracts.dto.DocumentDto;
import br.com.ultimoandar.contracts.dto.TemplateDto;
import br.com.ultimoandar.contracts.dto.TerminationDto;
import br.com.ultimoandar.contracts.entity.ContractAddendum;
import br.com.ultimoandar.contracts.entity.ContractTermination;
import br.com.ultimoandar.contracts.entity.DocumentTemplate;
import br.com.ultimoandar.contracts.entity.GeneratedDocument;
import br.com.ultimoandar.contracts.entity.LeaseContract;

public final class ContractMapper {

    private ContractMapper() {
    }

    public static ContractDto toDto(LeaseContract contract) {
        return new ContractDto(
                contract.getId(),
                contract.getContractNumber(),
                contract.getProperty().getId(),
                contract.getProperty().getCode(),
                contract.getProperty().getAddress().formatted(),
                contract.getOwner().getId(),
                contract.getOwner().getName(),
                contract.getTenant().getId(),
                contract.getTenant().getName(),
                contract.getMonthlyRent(),
                contract.getRentDueDay(),
                contract.getTermType(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getAdjustmentIndex(),
                contract.getGuaranteeType(),
                contract.getPaymentMethod(),
                contract.getNotes(),
                contract.getExtraClauses(),
                contract.getStatus(),
                contract.getGeneratedAt(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }

    public static DocumentDto documentToDto(GeneratedDocument document) {
        return new DocumentDto(
                document.getId(),
                document.getDocumentType(),
                document.getContract() == null ? null : document.getContract().getId(),
                document.getContract() == null ? null : document.getContract().getContractNumber(),
                document.getTitle(),
                document.getFileName(),
                document.getContentType(),
                document.getSizeBytes(),
                document.getGeneratedAt(),
                document.isArchived()
        );
    }

    public static AddendumDto addendumToDto(ContractAddendum addendum) {
        return new AddendumDto(
                addendum.getId(),
                addendum.getContract().getId(),
                addendum.getContract().getContractNumber(),
                addendum.getAddendumType(),
                addendum.getDescription(),
                addendum.getAddendumDate(),
                addendum.getNewMonthlyRent(),
                addendum.getNewTerm(),
                addendum.getNewEndDate(),
                addendum.getSpecificChanges(),
                addendum.getObservations(),
                addendum.getCreatedAt()
        );
    }

    public static TerminationDto terminationToDto(ContractTermination termination) {
        return new TerminationDto(
                termination.getId(),
                termination.getContract().getId(),
                termination.getContract().getContractNumber(),
                termination.getTerminationDate(),
                termination.getReason(),
                termination.isHasPendingDebts(),
                termination.getPenaltyAmount(),
                termination.getProportionalRentAmount(),
                termination.getPendingChargesAmount(),
                termination.getRepairsAmount(),
                termination.getObservations(),
                termination.getAdditionalStatements(),
                termination.getCreatedAt()
        );
    }

    public static TemplateDto templateToDto(DocumentTemplate template) {
        return new TemplateDto(
                template.getId(),
                template.getDocumentType(),
                template.getName(),
                template.getContent(),
                template.isActive(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}
