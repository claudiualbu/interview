package com.interview.entity;

import com.interview.common.entity.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "repair_orders")
public class RepairOrder extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "vehicle_vin", nullable = false, length = 40)
    private String vehicleVin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RepairOrderStatus status;

    @Version
    private Long version;

    protected RepairOrder() {

    }

    public RepairOrder(String customerName, String vehicleVin, RepairOrderStatus status) {
        this.customerName = customerName;
        this.vehicleVin = vehicleVin;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getVehicleVin() {
        return vehicleVin;
    }

    public RepairOrderStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }

    public void update(String customerName, String vehicleVin, RepairOrderStatus status) {
        this.customerName = customerName;
        this.vehicleVin = vehicleVin;
        this.status = status;
    }
}
