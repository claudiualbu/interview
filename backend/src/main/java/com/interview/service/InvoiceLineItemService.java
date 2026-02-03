package com.interview.service;

import com.interview.common.exception.ConflictException;
import com.interview.common.exception.NotFoundException;
import com.interview.dto.CreateInvoiceLineItemRequest;
import com.interview.dto.InvoiceLineItemResponse;
import com.interview.dto.UpdateInvoiceLineItemRequest;
import com.interview.entity.Invoice;
import com.interview.entity.InvoiceLineItem;
import com.interview.entity.InvoiceStatus;
import com.interview.mapper.InvoiceLineItemMapper;
import com.interview.repository.InvoiceLineItemRepository;
import com.interview.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class InvoiceLineItemService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineItemRepository invoiceLineItemRepository;
    private final InvoiceLineItemMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(InvoiceLineItemService.class);

    public InvoiceLineItemService(InvoiceRepository invoiceRepository,
                                  InvoiceLineItemRepository invoiceLineItemRepository,
                                  InvoiceLineItemMapper mapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineItemRepository = invoiceLineItemRepository;
        this.mapper = mapper;
    }

    @Transactional
    public InvoiceLineItemResponse create(Long invoiceId,CreateInvoiceLineItemRequest request) {
        log.info("Creating line item for invoiceId={}", invoiceId);

        Invoice invoice = invoiceRepository.findByIdForUpdate(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.ISSUED) {
            throw new ConflictException("Cannot add line items to ISSUED invoice");
        }

        InvoiceLineItem entity = new InvoiceLineItem(
                invoice,
                request.description(),
                request.quantity(),
                request.unitPriceCents()
        );
        invoice.addLineItem(entity);
        InvoiceLineItem saved = invoiceLineItemRepository.save(entity);

        log.info("Created line item id={} for invoiceId={}", saved.getId(), invoiceId);

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
        log.info("Updating line item id={}", itemId);

        InvoiceLineItem entity = invoiceLineItemRepository.findByIdWithInvoice(itemId)
                .orElseThrow(() -> new NotFoundException("Line item not found"));

        if (entity.getInvoice().getStatus() == InvoiceStatus.ISSUED) {
            throw new ConflictException("Cannot modify line items of ISSUED invoice");
        }

        if (!Objects.equals(entity.getVersion(), request.version())) {
            throw new ConflictException("Line item was modified by another request");
        }

        entity.update(request.description(), request.quantity(), request.unitPriceCents());

        log.info("Updated line item id={}", itemId);

        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long itemId) {
        log.info("Deleting line item id={}", itemId);

        InvoiceLineItem item = invoiceLineItemRepository.findByIdWithInvoice(itemId)
                .orElseThrow(() -> new NotFoundException("Line item not found"));

        if (item.getInvoice().getStatus() == InvoiceStatus.ISSUED) {
            throw new ConflictException("Cannot delete line items from ISSUED invoice");
        }

        invoiceLineItemRepository.delete(item);

        log.info("Deleted line item id={}", itemId);
    }
}