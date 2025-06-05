package com.scorelens.Service;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.DTOs.Response.BilliardTableResponse;
import com.scorelens.Entity.BilliardTable;
import com.scorelens.Entity.Store;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.BIlliardTableMapper;
import com.scorelens.Repository.BilliardTableRepo;
import com.scorelens.Repository.StoreRepo;
import com.scorelens.Service.Interface.IBilliardTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BilliardTableService implements IBilliardTableService {

    @Autowired
    BilliardTableRepo billiardTableRepo;

    @Autowired
    BIlliardTableMapper billiardTableMapper;

    @Autowired
    StoreService storeService;

    @Autowired
    private StoreRepo storeRepo;

    @Override
    public BilliardTableResponse createBilliardTable(BilliardTableRequest request) {

        //map store thủ công để đảm bảo store có tồn tại, k map qua mapstruct
        Store store = getStoreById(request.getStoreID());

        BilliardTable billiardTable = billiardTableMapper.toBilliardTable(request);

        //set table code
        String tableCode = generateID(request.getName());
        billiardTable.setTableCode(tableCode);

        //set store
        billiardTable.setStore(store);

        BilliardTableResponse billiardTableResponse = billiardTableMapper.toBilliardTableResponse(billiardTableRepo.save(billiardTable));
        return billiardTableResponse;

    }

    @Override
    public List<BilliardTableResponse> getAllBilliardTables() {
        List<BilliardTable> billiardTables = billiardTableRepo.findAll();
        if (billiardTables == null || billiardTables.isEmpty()) throw new AppException(ErrorCode.EMPTY_LIST);
        return billiardTableMapper.toBilliardTableResponsesList(billiardTables);
    }

    @Override
    public BilliardTableResponse findBilliardTableById(String billardTableID) {
        return null;
    }


    private Store getStoreById(String storeId) {
        return storeRepo.findById(storeId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));
    }

    private String generateID(String billiardTableName){
        if (billiardTableName == null || billiardTableName.isEmpty()) {
            return "";
        }
        StringBuilder codeBuilder = new StringBuilder();
        // Tách các từ bằng khoảng trắng
        String[] words = billiardTableName.trim().split("\\s+");

        for (String word : words) {
            // Nếu là số thì giữ nguyên
            if (word.matches("\\d+")) {
                codeBuilder.append(word);
            }
            // Nếu là chữ thì lấy ký tự đầu viết hoa
            else if (!word.isEmpty()) {
                codeBuilder.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return codeBuilder.toString();
    }


}
