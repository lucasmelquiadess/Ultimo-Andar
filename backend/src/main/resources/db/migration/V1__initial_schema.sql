create table owners (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    person_type varchar(30) not null,
    name varchar(255) not null,
    document varchar(20) not null unique,
    identity_number varchar(80),
    nationality varchar(120),
    marital_status varchar(120),
    profession varchar(120),
    postal_code varchar(20),
    street varchar(255),
    number varchar(40),
    complement varchar(255),
    neighborhood varchar(160),
    city varchar(160),
    state varchar(60),
    country varchar(80),
    phone varchar(80),
    email varchar(180),
    bank_details varchar(500),
    notes text,
    active boolean not null
);

create table tenants (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    person_type varchar(30) not null,
    name varchar(255) not null,
    document varchar(20) not null unique,
    identity_number varchar(80),
    nationality varchar(120),
    marital_status varchar(120),
    profession varchar(120),
    postal_code varchar(20),
    street varchar(255),
    number varchar(40),
    complement varchar(255),
    neighborhood varchar(160),
    city varchar(160),
    state varchar(60),
    country varchar(80),
    phone varchar(80),
    email varchar(180),
    spouse_data text,
    guarantor_data text,
    notes text,
    active boolean not null
);

create table rental_properties (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    code varchar(80) not null unique,
    type varchar(40) not null,
    postal_code varchar(20),
    street varchar(255),
    number varchar(40),
    complement varchar(255),
    neighborhood varchar(160),
    city varchar(160),
    state varchar(60),
    country varchar(80),
    description text,
    monthly_rent numeric(14, 2) not null,
    condominium_fee numeric(14, 2),
    iptu_value numeric(14, 2),
    status varchar(40) not null,
    owner_id uuid not null references owners(id),
    internal_notes text
);

create table property_photos (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    property_id uuid not null references rental_properties(id) on delete cascade,
    file_name varchar(255) not null,
    original_file_name varchar(255),
    content_type varchar(120),
    file_path varchar(600) not null,
    size_bytes bigint
);

create table lease_contracts (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    contract_number varchar(80) not null unique,
    property_id uuid not null references rental_properties(id),
    owner_id uuid not null references owners(id),
    tenant_id uuid not null references tenants(id),
    monthly_rent numeric(14, 2) not null,
    rent_due_day integer not null,
    term_type varchar(40) not null,
    start_date date not null,
    end_date date,
    adjustment_index varchar(120),
    guarantee_type varchar(40) not null,
    payment_method varchar(180),
    notes text,
    extra_clauses text,
    status varchar(40) not null,
    generated_at timestamp
);

create table contract_addendums (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    contract_id uuid not null references lease_contracts(id),
    addendum_type varchar(40) not null,
    description text not null,
    addendum_date date not null,
    new_monthly_rent numeric(14, 2),
    new_term varchar(120),
    new_end_date date,
    specific_changes text,
    observations text
);

create table contract_terminations (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    contract_id uuid not null references lease_contracts(id),
    termination_date date not null,
    reason text not null,
    has_pending_debts boolean not null,
    penalty_amount numeric(14, 2),
    proportional_rent_amount numeric(14, 2),
    pending_charges_amount numeric(14, 2),
    repairs_amount numeric(14, 2),
    observations text,
    additional_statements text
);

create table generated_documents (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    document_type varchar(40) not null,
    contract_id uuid references lease_contracts(id),
    source_id uuid,
    title varchar(255) not null,
    file_name varchar(255) not null,
    storage_path varchar(600) not null,
    content_type varchar(120) not null,
    size_bytes bigint not null,
    generated_at timestamp not null,
    archived boolean not null
);

create table document_templates (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    document_type varchar(40) not null,
    name varchar(255) not null,
    content text not null,
    active boolean not null
);

create index idx_properties_owner on rental_properties(owner_id);
create index idx_properties_status on rental_properties(status);
create index idx_contracts_status on lease_contracts(status);
create index idx_contracts_end_date on lease_contracts(end_date);
create index idx_documents_type on generated_documents(document_type);
create index idx_documents_contract on generated_documents(contract_id);
