alter table invoices drop constraint fk_invoice_repair_order;
alter table invoices add constraint fk_invoice_repair_order
    foreign key (repair_order_id) references repair_orders(id) on delete restrict;

alter table invoice_line_items drop constraint fk_invoice_line_items_invoice;
alter table invoice_line_items add constraint fk_invoice_line_items_invoice
    foreign key (invoice_id) references invoices(id) on delete cascade;