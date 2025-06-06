package com.scorelens.Service;

import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.Store;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.StoreMapper;
import com.scorelens.Repository.StoreRepo;
import com.scorelens.Service.Interface.IStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoreService implements IStoreService {

    @Autowired
    private StoreRepo storeRepo;

    @Autowired
    private StoreMapper storeMapper;



    @Override
    public StoreResponse createStore(StoreRequest storeRequest) {
        if (storeRepo.existsByname(storeRequest.getName())) {
            throw new AppException(ErrorCode.STORE_EXIST);
        }
        Store store = storeMapper.toStore(storeRequest);
        StoreResponse storeResponse = storeMapper.toStoreResponse(storeRepo.save(store));
        return storeResponse;
    }

    @Override
    public List<StoreResponse> findAllStores() {
        List<Store> allStores = storeRepo.findAll();
        if (allStores.isEmpty()) throw new AppException(ErrorCode.EMPTY_LIST);
        return storeMapper.toStoreResponseList(allStores);
    }

    @Override
    public StoreResponse findStoreById(String storeID) {
        Optional<Store> store = storeRepo.findById(storeID);
        if (store.isEmpty()) throw new AppException(ErrorCode.STORE_NOT_FOUND);
        return storeMapper.toStoreResponse(store.get());
    }


}
