alter table repair_orders add column version bigint not null default 0;
alter table invoices add column version bigint not null default 0;
