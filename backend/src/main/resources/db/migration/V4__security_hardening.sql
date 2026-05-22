alter table app_users add column must_change_password boolean not null default true;
alter table app_users add column failed_login_attempts integer not null default 0;
alter table app_users add column locked_until timestamp;
alter table audit_events add column ip_address varchar(80);
