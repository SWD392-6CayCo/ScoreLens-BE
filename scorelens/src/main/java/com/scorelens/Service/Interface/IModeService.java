package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.ModeRequest;
import com.scorelens.DTOs.Response.ModeResponse;

public interface IModeService {
    ModeResponse createMode(ModeRequest request);
    ModeResponse getById(Integer id);
    ModeResponse updateMode(Integer id, ModeRequest request);
    Integer delete(Integer id);
}
