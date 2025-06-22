package com.scorelens.DTOs.Request;

import com.scorelens.Enums.WebSocketCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketReq {
    private WebSocketCode code;
    private Object data;


}
