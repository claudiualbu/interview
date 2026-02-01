package com.interview.service;

import com.interview.common.exception.NotFoundException;
import com.interview.dto.CreateRepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.dto.UpdateRepairOrderRequest;
import com.interview.entity.RepairOrder;
import com.interview.mapper.RepairOrderMapper;
import com.interview.repository.RepairOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RepairOrderService {
    private final RepairOrderRepository repository;
    private final RepairOrderMapper mapper;

    public RepairOrderService(RepairOrderRepository repository, RepairOrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public RepairOrderResponse create(CreateRepairOrderRequest request) {
        RepairOrder entity = mapper.toEntity(request);
        RepairOrder saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RepairOrderResponse> list() {
        return repository.findAllByOrderByIdAsc().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RepairOrderResponse get(Long id) {
        RepairOrder entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RepairOrder Not Found"));
        return mapper.toResponse(entity);
    }

    @Transactional
    public RepairOrderResponse update(Long id, UpdateRepairOrderRequest request) {
        RepairOrder entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RepairOrder Not Found"));

        entity.update(request.customerName(), request.vehicleVin(), request.status());
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("RepairOrder Not Found");
        }
        repository.deleteById(id);
    }
}
