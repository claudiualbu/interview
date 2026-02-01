package com.interview.mapper;

import com.interview.dto.CreateRepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.entity.RepairOrder;
import com.interview.entity.RepairOrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepairOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(defaultStatus())")
    RepairOrder toEntity(CreateRepairOrderRequest request);

    RepairOrderResponse toResponse(RepairOrder entity);

    default RepairOrderStatus defaultStatus() {
        return RepairOrderStatus.OPEN;
    }
}
