package com.interview.repository;

import com.interview.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    List<RepairOrder> findAllByOrderByIdAsc();
}
