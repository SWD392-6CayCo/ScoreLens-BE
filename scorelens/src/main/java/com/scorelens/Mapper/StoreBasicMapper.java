package com.scorelens.Mapper;

import com.scorelens.DTOs.Response.StoreBasicResponse;
import com.scorelens.Entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreBasicMapper {
    
    @Mapping(source = "storeID", target = "storeID")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "description", target = "description")
    StoreBasicResponse toStoreBasicResponse(Store store);
}
