package com.scorelens.Service;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.BilliardTableResponse;
import com.scorelens.Entity.BilliardTable;
import com.scorelens.Entity.Store;
import com.scorelens.Enums.TableStatus;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.BilliardTableMapper;
import com.scorelens.Repository.BilliardTableRepo;
import com.scorelens.Repository.StoreRepo;
import com.scorelens.Service.Interface.IBilliardTableService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;



import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BilliardTableService implements IBilliardTableService {

    BilliardTableRepo billiardTableRepo;

    BilliardTableMapper billiardTableMapper;

    S3Service s3Service;

    QRCodeService qrCodeService;

    StoreRepo storeRepo;

    String webUrl = "https://score-lens.vercel.app/";

    BilliardMatchService billiardMatchService;

    @Override
    @Transactional
    public BilliardTableResponse createBilliardTable(BilliardTableRequest request) {
        try {
            // Check và lấy store
            Store store = getStoreById(request.getStoreID());

            // Map và set table info
            BilliardTable table = billiardTableMapper.toBilliardTable(request);
            table.setTableCode(generateID(request.getName()));
            table.setStore(store);

            // Save table để lấy ID (vẫn trong transaction)
            billiardTableRepo.save(table);

            // Tạo QR code, nếu fail sẽ throw và rollback transaction
            String qrCodeUrl = generateQRCodeAndUpload(webUrl + table.getBillardTableID());
            table.setQrCode(qrCodeUrl);

            // Save lại với QR code
            billiardTableRepo.save(table);

            // Trả về response
            return billiardTableMapper.toBilliardTableResponse(table);

        } catch (Exception e) {
            log.error("Failed to create Billiard Table: {}", e.getMessage());
            throw new AppException(ErrorCode.CREATE_TABLE_FAILED);
        }
    }


    @Override
    public List<BilliardTableResponse> getAllBilliardTables() {
        List<BilliardTable> billiardTables = billiardTableRepo.findAll();
        if (billiardTables.isEmpty()) throw new AppException(ErrorCode.EMPTY_LIST);
        return billiardTableMapper.toBilliardTableResponsesList(billiardTables);
    }

    @Override
    public BilliardTableResponse findBilliardTableById(String billiardTableID) {
        BilliardTable billiardTable = billiardTableRepo.findById(billiardTableID)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_FOUND));
        return billiardTableMapper.toBilliardTableResponse(billiardTable);
    }

    @Override
    public BilliardTableResponse updateBilliardTable(String billiardTableID, BilliardTableRequest request) {
        BilliardTable billiardTable = billiardTableRepo.findById(billiardTableID)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_FOUND));
        billiardTableMapper.updateBilliardTable(billiardTable, request);
        //thay đổi table code theo tên đã update
        billiardTable.setTableCode(generateID(request.getName()));
        billiardTableRepo.save(billiardTable);
        return billiardTableMapper.toBilliardTableResponse(billiardTable);
    }

    @Override
    public BilliardTableResponse updateBilliardTable(String billiardTableID, TableStatus status) {
        BilliardTable billiardTable = billiardTableRepo.findById(billiardTableID)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_FOUND));
        billiardTable.setStatus(status);
        billiardTableRepo.save(billiardTable);
        return billiardTableMapper.toBilliardTableResponse(billiardTable);
    }

    @Override
    public void setInUse(String billiardTableID) {
        updateBilliardTable(billiardTableID, TableStatus.inUse);
    }

    @Override
    public void setAvailable(String billiardTableID) {
        updateBilliardTable(billiardTableID, TableStatus.available);
    }

    @Override
    public void setUnderMaintenance(String billiardTableID) {
        updateBilliardTable(billiardTableID, TableStatus.underMaintainance);
    }


    @Override
    @Transactional
    public boolean deleteBilliardTable(String billiardTableID) {
        // Tìm table, nếu không có thì ném exception
        BilliardTable table = billiardTableRepo.findById(billiardTableID)
                .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_FOUND));
        // Lưu QR code URL để xử lý sau
        String qrCodeUrl = table.getQrCode();
        // Xóa record trong DB (sẽ rollback nếu exception sau đó)
        billiardTableRepo.delete(table);
        // Xóa QR code trên S3 nếu có URL
        s3Service.deleteQrCodeFromS3(qrCodeUrl, table.getBillardTableID());
        return true;
    }


    @Override
    public List<BilliardTableResponse> getTablesByStore(String storeID) {
        List<BilliardTable> list = billiardTableRepo.findAllByStore_StoreID(storeID);
        List<BilliardTableResponse> response = billiardTableMapper.toBilliardTableResponsesList(list);
        //add match to table when table is in use
        for (BilliardTableResponse table : response) {
            if (table.getStatus().equals(TableStatus.inUse)){
                BilliardMatchResponse rs = billiardMatchService.getById(Integer.parseInt(table.getBillardTableID()));
                table.setMatchResponse(rs);
            }
        }
        if (list.isEmpty()) throw new AppException(ErrorCode.EMPTY_LIST);
        return response;
    }


    private Store getStoreById(String storeId) {
        return storeRepo.findById(storeId)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));
    }

    private String generateID(String billiardTableName) {
        if (billiardTableName == null || billiardTableName.isEmpty()) {
            return "";
        }
        StringBuilder codeBuilder = new StringBuilder();
        // Tách các từ bằng khoảng trắng
        String[] words = billiardTableName.trim().split("\\s+");

        // Regex số Ả Rập
        String arabicRegex = "\\d+";
        // Regex số La Mã (đến 3999)
        String romanRegex = "^(M{0,3})(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";

        for (String word : words) {
            if (word.matches(arabicRegex) || word.matches(romanRegex)) {
                // Nếu là số (Ả Rập hoặc La Mã), giữ nguyên
                codeBuilder.append(word);
            } else if (!word.isEmpty()) {
                // Nếu là chữ, lấy ký tự đầu viết hoa
                codeBuilder.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return codeBuilder.toString();
    }


    public String generateQRCodeAndUpload(String text) {
        try {
            // Generate QR code image as byte[]
            byte[] qrCodeBytes = qrCodeService.generateQRCodeImage(text, 200, 200);
            // save qr to s3
            // Return public URL
            return s3Service.uploadFile(qrCodeBytes,"image/png", "png");
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate and upload QR code: " + e.getMessage());
        }
    }


}
