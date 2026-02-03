package com.interview.controller;

import com.interview.dto.CreateRepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.dto.UpdateRepairOrderRequest;
import com.interview.service.RepairOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/repair-orders")
public class RepairOrderController {

    private final RepairOrderService service;

    public RepairOrderController(RepairOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RepairOrderResponse> create(@Valid @RequestBody CreateRepairOrderRequest request) {
        RepairOrderResponse created = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/repair-orders/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<RepairOrderResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public RepairOrderResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public RepairOrderResponse update(@PathVariable Long id,
                                      @Valid @RequestBody UpdateRepairOrderRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
