insert into owners (
    id, created_at, updated_at, person_type, name, document, identity_number, nationality,
    marital_status, profession, postal_code, street, number, complement, neighborhood, city,
    state, country, phone, email, bank_details, notes, active
) values (
    '11111111-1111-1111-1111-111111111111',
    timestamp '2026-05-21 10:00:00',
    timestamp '2026-05-21 10:00:00',
    'PHYSICAL',
    'Marina Albuquerque',
    '52998224725',
    'MG-12.345.678',
    'brasileira',
    'casada',
    'engenheira',
    '30140071',
    'Rua dos Timbiras',
    '1200',
    'Sala 402',
    'Funcionários',
    'Belo Horizonte',
    'MG',
    'Brasil',
    '(31) 99999-1000',
    'marina.albuquerque@example.com',
    'Banco 001, agência 1234, conta 56789-0',
    'Locadora de exemplo para testes locais.',
    true
);

insert into tenants (
    id, created_at, updated_at, person_type, name, document, identity_number, nationality,
    marital_status, profession, postal_code, street, number, complement, neighborhood, city,
    state, country, phone, email, spouse_data, guarantor_data, notes, active
) values (
    '22222222-2222-2222-2222-222222222222',
    timestamp '2026-05-21 10:05:00',
    timestamp '2026-05-21 10:05:00',
    'PHYSICAL',
    'Lucas Ferreira',
    '11144477735',
    'SP-22.333.444',
    'brasileiro',
    'solteiro',
    'analista de sistemas',
    '01310930',
    'Avenida Paulista',
    '1000',
    'Apto 81',
    'Bela Vista',
    'São Paulo',
    'SP',
    'Brasil',
    '(11) 98888-2000',
    'lucas.ferreira@example.com',
    null,
    'Fiador: Roberto Ferreira, CPF 39053344705',
    'Locatário de exemplo para testes locais.',
    true
);

insert into rental_properties (
    id, created_at, updated_at, code, type, postal_code, street, number, complement,
    neighborhood, city, state, country, description, monthly_rent, condominium_fee,
    iptu_value, status, owner_id, internal_notes
) values (
    '33333333-3333-3333-3333-333333333333',
    timestamp '2026-05-21 10:10:00',
    timestamp '2026-05-21 10:10:00',
    'UA-IMV-0001',
    'APARTMENT',
    '30140071',
    'Rua dos Timbiras',
    '1200',
    'Apto 1402',
    'Funcionários',
    'Belo Horizonte',
    'MG',
    'Brasil',
    'Apartamento de 2 quartos, sala, cozinha, área de serviço e uma vaga de garagem.',
    3200.00,
    640.00,
    180.00,
    'RENTED',
    '11111111-1111-1111-1111-111111111111',
    'Imóvel usado nos dados iniciais para validar geração de contrato.'
);

insert into lease_contracts (
    id, created_at, updated_at, contract_number, property_id, owner_id, tenant_id,
    monthly_rent, rent_due_day, term_type, start_date, end_date, adjustment_index,
    guarantee_type, payment_method, notes, extra_clauses, status, generated_at
) values (
    '44444444-4444-4444-4444-444444444444',
    timestamp '2026-05-21 10:20:00',
    timestamp '2026-05-21 10:20:00',
    'UA-2026-00001',
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    3200.00,
    10,
    'MONTHS_12',
    date '2026-06-01',
    date '2027-05-31',
    'IPCA',
    'GUARANTOR',
    'boleto bancário',
    'Contrato de exemplo gerado para navegação inicial.',
    'As partes poderão formalizar vistoria complementar em até 5 dias úteis.',
    'ACTIVE',
    timestamp '2026-05-21 10:20:00'
);
