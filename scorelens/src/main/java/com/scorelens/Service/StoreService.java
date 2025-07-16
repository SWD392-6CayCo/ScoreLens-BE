package com.scorelens.Service;

import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.DTOs.Response.CustomerMatchResponse;
import com.scorelens.DTOs.Response.StoreDataResponse;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.BilliardTable;
import com.scorelens.Entity.Store;
import com.scorelens.Enums.TableStatus;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.StoreMapper;
import com.scorelens.Repository.StoreRepo;
import com.scorelens.Service.Interface.IStoreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
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
        return storeMapper.toStoreResponse(storeRepo.save(store));
    }

    @Override
    public List<StoreResponse> findAllStores() {
        List<Store> allStores = storeRepo.findAll();
        if (allStores.isEmpty()) throw new AppException(ErrorCode.EMPTY_LIST);
        return storeMapper.toStoreResponseList(allStores);
    }

    @Override
    public StoreResponse findStoreById(String storeID) {
        Store store = storeRepo.findById(storeID)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));
        return storeMapper.toStoreResponse(store);
    }

    @Override
    public StoreResponse updateStore(String storeID, StoreRequest storeRequest) {
        Store updateStore = storeRepo.findById(storeID)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));
        storeMapper.updateStore(updateStore, storeRequest);
        storeRepo.save(updateStore);
        return storeMapper.toStoreResponse(updateStore);
    }

    @Override
    public StoreResponse updateStore(String storeID, String status) {
        Store updateStore = storeRepo.findById(storeID)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));
        updateStore.setStatus(status);
        storeRepo.save(updateStore);
        return storeMapper.toStoreResponse(updateStore);
    }

    @Override
    public StoreDataResponse getStoreData(String storeId) {
        Store store = storeRepo.findById(storeId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

        int total = store.getBilliardTables().size();
        int playing = 0;
        int available = 0;
        int broken = 0;

        Map<String, Integer> customerMatchMap = new HashMap<>();

        for (BilliardTable table : store.getBilliardTables()) {
            TableStatus status = table.getStatus();
            if (status == TableStatus.inUse) {
                playing++;
            }
            else if (status == TableStatus.available) {
                available++;
            }
            else if (status == TableStatus.underMaintainance) {
                broken++;
            }
        }

        List<Object[]> raw = storeRepo.countMatchesByCustomerInStore(storeId);
        List<CustomerMatchResponse> customers = raw.stream()
                .map(r -> new CustomerMatchResponse(
                        (String) r[1],
                        ((Long) r[2]).intValue()
                ))
                .collect(Collectors.toList());
        return new StoreDataResponse(total, playing, available, broken, customers);
    }



}
