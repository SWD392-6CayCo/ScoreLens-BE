package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.ModeRequest;
import com.scorelens.DTOs.Response.ModeResponse;
import com.scorelens.Entity.Mode;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;

import java.util.List;

public interface IModeService {
    public ModeResponse createMode(ModeRequest request);

    public List<ModeResponse> getAll();

    public ModeResponse getById(Integer id);

    public ModeResponse updateMode(Integer id, ModeRequest request);
    public Integer delete(Integer id);
}
