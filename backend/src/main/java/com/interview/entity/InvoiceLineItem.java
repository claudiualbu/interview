package com.interview.entity;

import com.interview.common.entity.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "invoice_line_items")
public class InvoiceLineItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price_cents", nullable = false)
    private int unitPriceCents;

    @Version
    private Long version;

    protected InvoiceLineItem() {

    }

    public InvoiceLineItem(Invoice invoice, String description, int quantity, int unitPriceCents) {
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
    }

    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getUnitPriceCents() {
        return unitPriceCents;
    }

    public long lineTotalCents() {
        return (long) quantity * (long) unitPriceCents;
    }

    public Long getVersion() {
        return version;
    }

    public void update(String description, int quantity, int unitPriceCents) {
        this.description = description;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
    }

    void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
