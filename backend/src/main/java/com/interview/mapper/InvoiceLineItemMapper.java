package com.interview.mapper;

import com.interview.dto.InvoiceLineItemResponse;
import com.interview.entity.InvoiceLineItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceLineItemMapper {

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "lineTotalCents", expression = "java(entity.lineTotalCents())")
    InvoiceLineItemResponse toResponse(InvoiceLineItem entity);
}
