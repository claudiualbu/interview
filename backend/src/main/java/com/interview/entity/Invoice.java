package com.interview.entity;

import com.interview.common.entity.AuditableEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repair_order_id", nullable = false, unique = true)
    private RepairOrder repairOrder;

    @Column(name = "invoice_number", nullable = false, length = 50, unique = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    @Version
    private Long version;

    protected Invoice() {

    }

    public Invoice(RepairOrder repairOrder, String invoiceNumber, InvoiceStatus status) {
        this.repairOrder = repairOrder;
        this.invoiceNumber = invoiceNumber;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public RepairOrder getRepairOrder() {
        return repairOrder;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public List<InvoiceLineItem> getLineItems() {
        return lineItems;
    }

    public Long getVersion() {
        return version;
    }

    public void markIssued() {
        this.status = InvoiceStatus.ISSUED;
    }
}
