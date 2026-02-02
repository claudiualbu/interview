package com.interview.service;

import com.interview.common.exception.ConflictException;
import com.interview.common.exception.NotFoundException;
import com.interview.dto.CreateInvoiceLineItemRequest;
import com.interview.dto.InvoiceLineItemResponse;
import com.interview.dto.UpdateInvoiceLineItemRequest;
import com.interview.entity.Invoice;
import com.interview.entity.InvoiceLineItem;
import com.interview.mapper.InvoiceLineItemMapper;
import com.interview.repository.InvoiceLineItemRepository;
import com.interview.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvoiceLineItemService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineItemRepository invoiceLineItemRepository;
    private final InvoiceLineItemMapper mapper;

    public InvoiceLineItemService(InvoiceRepository invoiceRepository,
                                  InvoiceLineItemRepository invoiceLineItemRepository,
                                  InvoiceLineItemMapper mapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineItemRepository = invoiceLineItemRepository;
        this.mapper = mapper;
    }

    @Transactional
    public InvoiceLineItemResponse create(Long invoiceId,CreateInvoiceLineItemRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        InvoiceLineItem entity = mapper.toEntity(invoice, request);
        InvoiceLineItem saved = invoiceLineItemRepository.save(entity);

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InvoiceLineItemResponse> listByInvoice(Long invoiceId) {
        List<InvoiceLineItem> items = invoiceLineItemRepository.findAllByInvoiceIdOrderByIdAsc(invoiceId);

        if(items.isEmpty() && !invoiceRepository.existsById(invoiceId)) {
            throw new NotFoundException("Invoice not found");
        }

        return items.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public InvoiceLineItemResponse update(Long itemId, UpdateInvoiceLineItemRequest request) {
        InvoiceLineItem entity = invoiceLineItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Line item not found"));

        if (!java.util.Objects.equals(entity.getVersion(), request.version())) {
            throw new ConflictException("Line item was modified by another request");
        }

        entity.update(request.description(), request.quantity(), request.unitPriceCents());
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long itemId) {
        InvoiceLineItem item = invoiceLineItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Line item not found"));
        invoiceLineItemRepository.delete(item);
    }
}
