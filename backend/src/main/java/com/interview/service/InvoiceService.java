package com.interview.service;

import com.interview.common.exception.ConflictException;
import com.interview.common.exception.NotFoundException;
import com.interview.dto.CreateInvoiceRequest;
import com.interview.dto.InvoiceResponse;
import com.interview.dto.RepairOrderResponse;
import com.interview.entity.Invoice;
import com.interview.entity.InvoiceStatus;
import com.interview.entity.RepairOrder;
import com.interview.mapper.InvoiceMapper;
import com.interview.repository.InvoiceRepository;
import com.interview.repository.RepairOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InvoiceService {
    private final RepairOrderRepository repairOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceService(RepairOrderRepository repairOrderRepository,
                          InvoiceRepository invoiceRepository,
                          InvoiceMapper invoiceMapper) {
        this.repairOrderRepository = repairOrderRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {
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

        return invoiceMapper.toResponse(saved);
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
