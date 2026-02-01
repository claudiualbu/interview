package com.interview.mapper;

import com.interview.dto.InvoiceResponse;
import com.interview.entity.Invoice;
import com.interview.entity.InvoiceStatus;
import com.interview.entity.RepairOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "repairOrder", source = "repairOrder")
    @Mapping(target = "invoiceNumber", source = "invoiceNumber")
    @Mapping(target = "status", source = "status")
    Invoice toEntity(RepairOrder repairOrder, String invoiceNumber, InvoiceStatus status);

    @Mapping(target = "repairOrderId", source = "repairOrder.id")
    InvoiceResponse toResponse(Invoice entity);
}
