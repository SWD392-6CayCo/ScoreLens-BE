package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.DTOs.Response.StoreDataResponse;
import com.scorelens.DTOs.Response.StoreResponse;

import java.util.List;

public interface IStoreService {
    StoreResponse createStore(StoreRequest storeRequest);
    List<StoreResponse> findAllStores();
    StoreResponse findStoreById(String storeID);
    StoreResponse updateStore(String storeID, StoreRequest storeRequest);
    StoreResponse updateStore(String storeID, String status);
    StoreDataResponse getStoreData(String storeID);
}
