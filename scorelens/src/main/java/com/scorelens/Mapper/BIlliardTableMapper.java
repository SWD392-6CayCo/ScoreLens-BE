package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.DTOs.Response.BilliardTableResponse;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.BilliardTable;
import com.scorelens.Entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StoreMapper.class})
public interface BIlliardTableMapper {

    @Mapping(source = "store", target = "storeResponse")
    BilliardTableResponse toBilliardTableResponse(BilliardTable billiardTable);


    BilliardTable toBilliardTable(BilliardTableResponse billiardTableResponse);
    List<BilliardTableResponse> toBilliardTableResponsesList(List<BilliardTable> billiardTables);


    // Map request to entity, ignore store
    @Mapping(target = "store", ignore = true)
    BilliardTable toBilliardTable(BilliardTableRequest billiardTableRequest);

}
