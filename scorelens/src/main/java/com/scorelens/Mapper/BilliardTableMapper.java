package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.DTOs.Response.BilliardTableResponse;
import com.scorelens.Entity.BilliardTable;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StoreMapper.class})
public interface BilliardTableMapper {

    @Mapping(source = "store.name", target = "storeName")
    BilliardTableResponse toBilliardTableResponse(BilliardTable billiardTable);

    BilliardTable toBilliardTable(BilliardTableResponse billiardTableResponse);
    List<BilliardTableResponse> toBilliardTableResponsesList(List<BilliardTable> billiardTables);


    // Map request to entity, ignore store
    @Mapping(target = "store", ignore = true)
    BilliardTable toBilliardTable(BilliardTableRequest billiardTableRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBilliardTable(@MappingTarget BilliardTable billiardTable, BilliardTableRequest billiardTableRequest);

}
