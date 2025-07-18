package com.scorelens.Controller.v3;


import com.scorelens.Service.QRCodeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "QRCode", description = "QRCode Creation")
@RestController
@RequestMapping("v3/qr_codes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QRCodeV3Controller {

    QRCodeService qrCodeService;

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCode(@RequestParam String text) {
        try {
            byte[] image = qrCodeService.generateQRCodeImage(text, 400, 400);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qrcode.png\"")
                    .body(image);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
