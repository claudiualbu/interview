package com.interview.controller;

import com.interview.dto.InvoiceLineItemResponse;
import com.interview.dto.UpdateInvoiceLineItemRequest;
import com.interview.service.InvoiceLineItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/line-items")
public class LineItemController {

    private final InvoiceLineItemService service;

    public LineItemController(InvoiceLineItemService service) {
        this.service = service;
    }

    @PutMapping("/{itemId}")
    public InvoiceLineItemResponse update(@PathVariable Long itemId,
                                          @Valid @RequestBody UpdateInvoiceLineItemRequest request) {
        return service.update(itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable Long itemId) {
        service.delete(itemId);
        return ResponseEntity.noContent().build();
    }
}
