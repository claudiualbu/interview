package com.interview.repository;

import com.interview.entity.Invoice;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByRepairOrderId(Long repairOrderId);

    @EntityGraph(attributePaths = {"repairOrder", "lineItems"})
    List<Invoice> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"repairOrder", "lineItems"})
    @Query("select i from Invoice i where i.id = :id")
    Optional<Invoice> findDetailsById(Long id);

    @EntityGraph(attributePaths = {"lineItems"})
    @Query("select i from Invoice i where i.id = :id")
    Optional<Invoice> findByIdWithLineItems(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Invoice i WHERE i.id = :id")
    Optional<Invoice> findByIdForUpdate(Long id);

    @EntityGraph(attributePaths = {"repairOrder", "lineItems"})
    Page<Invoice> findAllByOrderByIdAsc(Pageable pageable);
}
