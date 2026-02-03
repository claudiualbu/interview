package com.interview.service;

import com.interview.common.exception.ConflictException;
import com.interview.common.exception.NotFoundException;
import com.interview.dto.CreateRepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.dto.UpdateRepairOrderRequest;
import com.interview.entity.RepairOrder;
import com.interview.mapper.RepairOrderMapper;
import com.interview.repository.InvoiceRepository;
import com.interview.repository.RepairOrderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class RepairOrderService {
    private final RepairOrderRepository repairOrderRepository;
    private final RepairOrderMapper repairOrderMapper;
    private final InvoiceRepository invoiceRepository;
    private static final Logger log = LoggerFactory.getLogger(RepairOrderService.class);

    public RepairOrderService(RepairOrderRepository repairOrderRepository,
                              RepairOrderMapper repairOrderMapper,
                              InvoiceRepository invoiceRepository) {
        this.repairOrderRepository = repairOrderRepository;
        this.repairOrderMapper = repairOrderMapper;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public RepairOrderResponse create(CreateRepairOrderRequest request) {
        log.info("Creating repair order for customer={}", request.customerName());

        RepairOrder entity = repairOrderMapper.toEntity(request);
        RepairOrder saved = repairOrderRepository.save(entity);

        log.info("Created repair order id={}", saved.getId());

        return repairOrderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RepairOrderResponse> list() {
        return repairOrderRepository.findAllByOrderByIdAsc().stream()
                .map(repairOrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RepairOrderResponse get(Long id) {
        RepairOrder entity = repairOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RepairOrder Not Found"));
        return repairOrderMapper.toResponse(entity);
    }

    @Transactional
    public RepairOrderResponse update(Long id, UpdateRepairOrderRequest request) {
        log.info("Updating repair order id={}", id);

        RepairOrder entity = repairOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RepairOrder Not Found"));

        if (!Objects.equals(entity.getVersion(), request.version())) {
            throw new ConflictException("RepairOrder was modified by another request");
        }

        entity.update(request.customerName(), request.vehicleVin(), request.status());

        log.info("Updated repair order id={}", id);

        return repairOrderMapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting repair order id={}", id);

        if (!repairOrderRepository.existsById(id)) {
            throw new NotFoundException("RepairOrder Not Found");
        }
        if (invoiceRepository.existsByRepairOrderId(id)) {
            throw new ConflictException("Cannot delete RepairOrder with associated Invoice");
        }
        repairOrderRepository.deleteById(id);

        log.info("Deleted repair order id={}", id);
    }
}
