package com.scorelens.Service;

import com.scorelens.DTOs.Request.ModeRequest;
import com.scorelens.DTOs.Response.ModeResponse;
import com.scorelens.Entity.Mode;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Repository.ModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModeService {

    @Autowired
    private ModeRepository modeRepository;

    public ModeResponse createMode(ModeRequest request) {
        Mode mode = new Mode();
        mode.setName(request.getName());
        mode.setDescription(request.getDescription());
        mode.setActive(request.isActive());
        mode = modeRepository.save(mode);
        return mapToResponse(mode);
    }

    public ModeResponse getById(Integer id) {
        Mode mode = modeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));
        return mapToResponse(mode);
    }

    public ModeResponse updateMode(Integer id, ModeRequest request) {
        Mode mode = modeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));

        mode.setName(request.getName());
        mode.setDescription(request.getDescription());
        mode.setActive(request.isActive());
        return mapToResponse(modeRepository.save(mode));
    }

    public Integer delete(Integer id) {
        if (!modeRepository.existsById(id)) {
            throw new AppException(ErrorCode.MODE_NOT_FOUND);
        }
        modeRepository.deleteById(id);
        return id;
    }

    private ModeResponse mapToResponse(Mode mode) {
        ModeResponse response = new ModeResponse();
        response.setModeID(mode.getModeID());
        response.setName(mode.getName());
        response.setDescription(mode.getDescription());
        response.setActive(mode.isActive());
        return response;
    }
}

