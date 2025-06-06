package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.DTOs.Response.StoreResponse;

import java.util.List;

public interface IStoreService {
    StoreResponse createStore(StoreRequest storeRequest);
    List<StoreResponse> findAllStores();
    StoreResponse findStoreById(String storeID);

}
