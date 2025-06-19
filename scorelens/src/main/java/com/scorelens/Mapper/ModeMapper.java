package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.ModeRequest;
import com.scorelens.DTOs.Response.ModeResponse;
import com.scorelens.Entity.Mode;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModeMapper {

    Mode toMode(ModeRequest request);

    ModeResponse toResponse(Mode mode);

    List<ModeResponse> toResponses(List<Mode> mode);
}
