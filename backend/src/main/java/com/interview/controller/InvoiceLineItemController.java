package com.interview.controller;

import com.interview.dto.CreateInvoiceLineItemRequest;
import com.interview.dto.InvoiceLineItemResponse;
import com.interview.service.InvoiceLineItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices/{invoiceId}/line-items")
public class InvoiceLineItemController {

    private final InvoiceLineItemService service;

    public InvoiceLineItemController(InvoiceLineItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InvoiceLineItemResponse> create(@PathVariable Long invoiceId,
                                                          @Valid @RequestBody CreateInvoiceLineItemRequest request) {
        InvoiceLineItemResponse created = service.create(invoiceId, request);
        return ResponseEntity
                .created(URI.create("/api/v1/line-items/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<InvoiceLineItemResponse> list(@PathVariable Long invoiceId) {
        return service.listByInvoice(invoiceId);
    }
}
