package com.scorelens.Service;

import com.scorelens.DTOs.Request.ModeRequest;
import com.scorelens.DTOs.Response.ModeResponse;
import com.scorelens.Entity.Mode;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.ModeMapper;
import com.scorelens.Repository.ModeRepository;
import com.scorelens.Service.Interface.IModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeService implements IModeService {

    @Autowired
    private ModeRepository modeRepository;

    @Autowired
    private ModeMapper modeMapper;

    @Override
    public ModeResponse createMode(ModeRequest request) {
        Mode mode = new Mode();
        mode.setName(request.getName());
        mode.setDescription(request.getDescription());
        mode.setActive(request.isActive());
        mode = modeRepository.save(mode);
        return modeMapper.toResponse(mode);
    }

    @Override
    public List<ModeResponse> getAll() {
        List<Mode> modes = modeRepository.findAll();
        return modeMapper.toResponses(modes);
    }

    @Override
    public ModeResponse getById(Integer id) {
        Mode mode = modeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));
        return modeMapper.toResponse(modeRepository.save(mode));
    }

    @Override
    public ModeResponse updateMode(Integer id, ModeRequest request) {
        Mode mode = modeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));

        mode.setName(request.getName());
        mode.setDescription(request.getDescription());
        mode.setActive(request.isActive());
        return modeMapper.toResponse(modeRepository.save(mode));
    }

    @Override
    public Integer delete(Integer id) {
        if (!modeRepository.existsById(id)) {
            throw new AppException(ErrorCode.MODE_NOT_FOUND);
        }
        modeRepository.deleteById(id);
        return id;
    }
}

