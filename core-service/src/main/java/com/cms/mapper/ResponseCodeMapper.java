package com.cms.mapper;

import com.cms.dal.entity.ResponseCode;
import com.cms.dto.request.ResponseCodeCreateRequest;
import com.cms.dto.response.ResponseCodeResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseCodeMapper {

    public ResponseCodeResponse toResponse(ResponseCode e) {
        if (e == null) return null;
        ResponseCodeResponse r = new ResponseCodeResponse();
        r.setId(e.getId());
        r.setCode(e.getCode());
        r.setShortDescription(e.getShortDescription());
        r.setFullDescription(e.getFullDescription());
        r.setAlertType(e.getAlertType());
        r.setHttpResponseCode(e.getHttpResponseCode());
        return r;
    }

    public List<ResponseCodeResponse> toResponseList(List<ResponseCode> list) {
        if (list == null) return null;
        List<ResponseCodeResponse> out = new ArrayList<>();
        for (ResponseCode e : list) out.add(toResponse(e));
        return out;
    }

    public ResponseCode toEntity(ResponseCodeCreateRequest req) {
        if (req == null) return null;
        ResponseCode e = new ResponseCode();
        e.setCode(req.getCode());
        e.setShortDescription(req.getShortDescription());
        e.setFullDescription(req.getFullDescription());
        e.setAlertType(req.getAlertType());
        e.setHttpResponseCode(req.getHttpResponseCode());
        return e;
    }
}
