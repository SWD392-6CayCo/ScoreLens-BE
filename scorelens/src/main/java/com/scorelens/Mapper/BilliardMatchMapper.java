package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.BilliardMatchCreateRequest;
import com.scorelens.DTOs.Request.BilliardMatchUpdateRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.Entity.BilliardMatch;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = { GameSetMapper.class, TeamMapper.class })
public interface BilliardMatchMapper {

    // Map create request to entity
//    @Mapping(target = "billiardMatchID", ignore = true)
//    @Mapping(target = "winner", ignore = true)
//    @Mapping(target = "startTime", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "endTime", ignore = true)
//    @Mapping(target = "status", constant = "pending")
//    @Mapping(target = "code", ignore = true)
//    @Mapping(target = "sets", ignore = true)
//    @Mapping(target = "teams", ignore = true)
    BilliardMatch toBilliardMatch(BilliardMatchCreateRequest request);

    @Mapping(target = "billiardTableID", source = "billardTable.billardTableID")
    @Mapping(target = "modeID", source = "mode.modeID")
    @Mapping(target = "byStaff", source = "staff.staffID")
    @Mapping(target = "byCustomer", source = "customer.customerID")
    @Mapping(target = "status", source = "status")
    BilliardMatchResponse toBilliardMatchResponse(BilliardMatch match);

    @Mapping(target = "billiardTableID", source = "billardTable.billardTableID")
    @Mapping(target = "modeID", source = "mode.modeID")
    @Mapping(target = "byStaff", source = "staff.staffID")
    @Mapping(target = "byCustomer", source = "customer.customerID")
    @Mapping(target = "status", source = "status")
    List<BilliardMatchResponse> toBilliardMatchResponses(List<BilliardMatch> matchs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBilliardMatchFromRequest(BilliardMatchUpdateRequest request, @MappingTarget BilliardMatch match);
}