create table app_users (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    username varchar(80) not null unique,
    display_name varchar(160) not null,
    password_hash varchar(120) not null,
    role varchar(30) not null,
    active boolean not null
);

create table audit_events (
    id uuid primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    actor varchar(80) not null,
    action varchar(80) not null,
    resource_type varchar(80) not null,
    resource_id varchar(120),
    details varchar(500)
);

create index idx_users_username on app_users(username);
create index idx_users_active on app_users(active);
create index idx_audit_created_at on audit_events(created_at);
create index idx_audit_actor on audit_events(actor);
