package com.scorelens.DTOs.Request;

import com.scorelens.Enums.WSFCMCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketReq {
    private WSFCMCode code;
    private Object data;


}
