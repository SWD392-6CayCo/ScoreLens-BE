package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.Store;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    List<StoreResponse> toStoreResponseList(List<Store> storeList);
    Store toStore(StoreRequest storeRequest);
    StoreResponse toStoreResponse(Store store);

}
