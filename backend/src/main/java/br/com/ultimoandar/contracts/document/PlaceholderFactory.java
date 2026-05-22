package br.com.ultimoandar.contracts.document;

import br.com.ultimoandar.contracts.config.CompanyProperties;
import br.com.ultimoandar.contracts.entity.ContractAddendum;
import br.com.ultimoandar.contracts.entity.ContractTermination;
import br.com.ultimoandar.contracts.entity.LeaseContract;
import br.com.ultimoandar.contracts.util.Formatters;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderFactory {

    private final CompanyProperties company;

    public PlaceholderFactory(CompanyProperties company) {
        this.company = company;
    }

    public Map<String, String> forContract(LeaseContract contract) {
        Map<String, String> values = base(contract);
        values.put("valor_aluguel", Formatters.money(contract.getMonthlyRent()));
        values.put("data_vencimento", String.valueOf(contract.getRentDueDay()));
        values.put("prazo_contrato", term(contract));
        values.put("data_inicio", Formatters.date(contract.getStartDate()));
        values.put("data_termino", Formatters.date(contract.getEndDate()));
        values.put("indice_reajuste", Formatters.blank(contract.getAdjustmentIndex(), "IGP-M ou índice legal substituto"));
        values.put("periodicidade_reajuste", "12 meses");
        values.put("tipo_garantia", guarantee(contract.getGuaranteeType().name()));
        values.put("forma_pagamento", Formatters.blank(contract.getPaymentMethod(), "boleto, transferência ou outro meio informado"));
        values.put("favorecido_pagamento", company.name());
        values.put("multa_mora_percentual", "2");
        values.put("juros_mora_percentual", "1");
        values.put("multa_rescisoria", "três aluguéis vigentes, proporcional ao período restante quando aplicável");
        values.put("finalidade_locacao", "residencial ou comercial conforme cadastro do imóvel");
        values.put("clausulas_extras", Formatters.blank(contract.getExtraClauses(), "Não há cláusulas extras registradas."));
        values.put("observacoes_adicionais", Formatters.blank(contract.getNotes(), "Sem observações adicionais."));
        values.put("nome_testemunha_1", "________________");
        values.put("cpf_testemunha_1", "________________");
        values.put("nome_testemunha_2", "________________");
        values.put("cpf_testemunha_2", "________________");
        return values;
    }

    public Map<String, String> forAddendum(ContractAddendum addendum) {
        Map<String, String> values = base(addendum.getContract());
        values.put("tipo_aditivo", addendum.getAddendumType().name());
        values.put("descricao_aditivo", addendum.getDescription());
        values.put("data_inicio_aditivo", Formatters.date(addendum.getAddendumDate()));
        values.put("novo_valor_aluguel", Formatters.money(addendum.getNewMonthlyRent()));
        values.put("novo_prazo_contrato", Formatters.blank(addendum.getNewTerm(), "conforme contrato original"));
        values.put("nova_data_termino", Formatters.date(addendum.getNewEndDate()));
        values.put("alteracoes_partes_garantias_clausulas", Formatters.blank(addendum.getSpecificChanges(), addendum.getDescription()));
        values.put("observacoes_aditivo", Formatters.blank(addendum.getObservations(), "Sem observações adicionais."));
        values.put("nome_testemunha_1", "________________");
        values.put("cpf_testemunha_1", "________________");
        values.put("nome_testemunha_2", "________________");
        values.put("cpf_testemunha_2", "________________");
        return values;
    }

    public Map<String, String> forTermination(ContractTermination termination) {
        Map<String, String> values = base(termination.getContract());
        BigDecimal total = zero(termination.getPenaltyAmount())
                .add(zero(termination.getProportionalRentAmount()))
                .add(zero(termination.getPendingChargesAmount()))
                .add(zero(termination.getRepairsAmount()));
        values.put("data_encerramento", Formatters.date(termination.getTerminationDate()));
        values.put("motivo_encerramento", termination.getReason());
        values.put("existe_debitos_pendentes", termination.isHasPendingDebts() ? "sim" : "não");
        values.put("valor_aluguel_proporcional", Formatters.money(termination.getProportionalRentAmount()));
        values.put("valor_multa_rescisoria", Formatters.money(termination.getPenaltyAmount()));
        values.put("valor_encargos_pendentes", Formatters.money(termination.getPendingChargesAmount()));
        values.put("valor_reparos", Formatters.money(termination.getRepairsAmount()));
        values.put("outros_valores", Formatters.blank(termination.getObservations(), "não informados"));
        values.put("valor_total_encerramento", Formatters.money(total));
        values.put("declaracoes_adicionais", Formatters.blank(termination.getAdditionalStatements(), "Não há declarações adicionais."));
        values.put("local_entrega_chaves", company.address());
        values.put("responsavel_recebimento_chaves", company.name());
        values.put("data_vistoria_saida", Formatters.date(termination.getTerminationDate()));
        values.put("pendencias_vistoria", Formatters.blank(termination.getObservations(), "sem pendências registradas até a geração deste termo"));
        values.put("forma_pagamento_encerramento", "conforme orientação formal da administradora");
        values.put("prazo_restituicao_caucao", "prazo legal e contratual aplicável");
        values.put("dados_pagamento_restituicao", "dados bancários cadastrados");
        values.put("nome_testemunha_1", "________________");
        values.put("cpf_testemunha_1", "________________");
        values.put("nome_testemunha_2", "________________");
        values.put("cpf_testemunha_2", "________________");
        return values;
    }

    private Map<String, String> base(LeaseContract contract) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("numero_contrato", contract.getContractNumber());
        values.put("nome_locador", contract.getOwner().getName());
        values.put("cpf_cnpj_locador", contract.getOwner().getDocument());
        values.put("endereco_locador", contract.getOwner().getAddress().formatted());
        values.put("nome_locatario", contract.getTenant().getName());
        values.put("cpf_cnpj_locatario", contract.getTenant().getDocument());
        values.put("endereco_locatario", contract.getTenant().getAddress().formatted());
        values.put("email_locador", Formatters.blank(contract.getOwner().getEmail(), "não informado"));
        values.put("email_locatario", Formatters.blank(contract.getTenant().getEmail(), "não informado"));
        values.put("telefone_locatario", Formatters.blank(contract.getTenant().getPhone(), "não informado"));
        values.put("endereco_imovel", contract.getProperty().getAddress().formatted());
        values.put("codigo_imovel", contract.getProperty().getCode());
        values.put("data_contrato_original", Formatters.date(contract.getStartDate()));
        values.put("data_inicio", Formatters.date(contract.getStartDate()));
        values.put("data_termino", Formatters.date(contract.getEndDate()));
        values.put("prazo_contrato", term(contract));
        values.put("valor_aluguel", Formatters.money(contract.getMonthlyRent()));
        values.put("data_vencimento", String.valueOf(contract.getRentDueDay()));
        values.put("cidade", Formatters.blank(contract.getProperty().getAddress().getCity(), company.defaultCity()));
        values.put("data_geracao", Formatters.date(LocalDate.now()));
        values.put("foro", company.defaultForum());
        values.put("cnpj_administradora", company.cnpj());
        values.put("endereco_administradora", company.address());
        values.put("email_privacidade", company.privacyEmail());
        values.put("tipo_garantia", guarantee(contract.getGuaranteeType().name()));
        return values;
    }

    private String term(LeaseContract contract) {
        return switch (contract.getTermType()) {
            case MONTHS_12 -> "12 meses";
            case MONTHS_24 -> "24 meses";
            case MONTHS_36 -> "36 meses";
            case INDETERMINATE -> "prazo indeterminado";
        };
    }

    private String guarantee(String value) {
        return switch (value) {
            case "CAUTION" -> "caução";
            case "GUARANTOR" -> "fiador";
            case "INSURANCE_BOND" -> "seguro fiança";
            case "NONE" -> "sem garantia";
            default -> "outra garantia ajustada entre as partes";
        };
    }

    private BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
