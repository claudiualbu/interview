package com.interview.service;

import com.interview.common.exception.ConflictException;
import com.interview.common.exception.NotFoundException;
import com.interview.dto.*;
import com.interview.entity.Invoice;
import com.interview.entity.InvoiceLineItem;
import com.interview.entity.InvoiceStatus;
import com.interview.entity.RepairOrder;
import com.interview.mapper.InvoiceLineItemMapper;
import com.interview.mapper.InvoiceMapper;
import com.interview.repository.InvoiceRepository;
import com.interview.repository.RepairOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class InvoiceService {
    private final RepairOrderRepository repairOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceLineItemMapper invoiceLineItemMapper;
    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    public InvoiceService(RepairOrderRepository repairOrderRepository,
                          InvoiceRepository invoiceRepository,
                          InvoiceMapper invoiceMapper, InvoiceLineItemMapper invoiceLineItemMapper) {
        this.repairOrderRepository = repairOrderRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.invoiceLineItemMapper = invoiceLineItemMapper;
    }

    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {
        log.info("Creating invoice for repairOrderId={}", request.repairOrderId());

        Long roId = request.repairOrderId();

        RepairOrder repairOrder = repairOrderRepository.findById(roId)
                .orElseThrow(() -> new NotFoundException("RepairOrder not found"));

        if(invoiceRepository.existsByRepairOrderId(roId)) {
            throw new ConflictException("Invoice already exist for this repair order");
        }

        String invoiceNumber = generateInvoiceNumber();
        InvoiceStatus status = InvoiceStatus.DRAFT;

        Invoice invoice = invoiceMapper.toEntity(repairOrder, invoiceNumber, status);
        Invoice saved = invoiceRepository.save(invoice);

        log.info("Created invoice id={} number={} for repairOrderId={}",
                saved.getId(), saved.getInvoiceNumber(), request.repairOrderId());

        return invoiceMapper.toResponse(saved);
    }

    @Transactional
    public InvoiceResponse updateStatus(Long id, UpdateInvoiceStatusRequest request) {
        log.info("Updating invoice status id={} to={}", id, request.status());

        Invoice invoice = invoiceRepository.findByIdWithLineItems(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        if (!Objects.equals(invoice.getVersion(), request.version())) {
            throw new ConflictException("Invoice was modified by another request");
        }

        if (invoice.getStatus() == InvoiceStatus.ISSUED) {
            throw new ConflictException("Cannot modify ISSUED invoice");
        }

        if (request.status() == InvoiceStatus.ISSUED) {
            if (invoice.getLineItems().isEmpty()) {
                throw new ConflictException("Cannot issue invoice without line items");
            }
            invoice.markIssued();
        }

        log.info("Updated invoice id={} status={}", id, invoice.getStatus());

        return invoiceMapper.toResponse(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceListItemResponse> list() {
        return invoiceRepository.findAllByOrderByIdAsc().stream()
                .map(this::toListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<InvoiceListItemResponse> list(Pageable pageable) {
        return invoiceRepository.findAllByOrderByIdAsc(pageable)
                .map(this::toListItem);
    }

    @Transactional(readOnly = true)
    public InvoiceDetailsResponse details(Long id) {
        Invoice invoice = invoiceRepository.findDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        int count = invoice.getLineItems().size();
        long total = totalCents(invoice.getLineItems());

        List<InvoiceLineItemResponse> items = invoice.getLineItems().stream()
                .map(invoiceLineItemMapper::toResponse)
                .toList();

        return new InvoiceDetailsResponse(
                invoice.getId(),
                invoice.getVersion(),
                invoice.getRepairOrder().getId(),
                invoice.getInvoiceNumber(),
                invoice.getStatus(),
                count,
                total,
                items,
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }

    private InvoiceListItemResponse toListItem(Invoice invoice) {
        int count = invoice.getLineItems().size();
        long total = totalCents(invoice.getLineItems());

        return new InvoiceListItemResponse(
                invoice.getId(),
                invoice.getRepairOrder().getId(),
                invoice.getInvoiceNumber(),
                invoice.getStatus(),
                count,
                total
        );
    }

    private long totalCents(List<InvoiceLineItem> items) {
        return items.stream()
                .mapToLong(InvoiceLineItem::lineTotalCents)
                .sum();
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
