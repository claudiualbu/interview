package com.interview.controller;

import com.interview.dto.CreateInvoiceRequest;
import com.interview.dto.InvoiceResponse;
import com.interview.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService invoiceService) {
        this.service = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceResponse created = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/invoices/" + created.id()))
                .body(created);
    }
}
