package com.cms.common.service;

import com.cms.common.dto.CmsResponse;
import com.cms.common.enums.ResponseCodeEnum;
import com.cms.common.model.IProcessMessage;
import com.cms.dal.entity.ResponseCode;
import com.cms.dal.repository.ResponseCodeRepository;
import org.springframework.stereotype.Service;

/**
 * Sets response on IProcessMessage from ResponseCodeEnum (and optionally from RESPONSE_CODE table).
 */
@Service
public class ResponseHelperService {

    private final ResponseCodeRepository responseCodeRepository;

    public ResponseHelperService(ResponseCodeRepository responseCodeRepository) {
        this.responseCodeRepository = responseCodeRepository;
    }

    public IProcessMessage setResponse(IProcessMessage responseData, ResponseCodeEnum responseCodeEnum) {
        String code = responseCodeEnum.getCode();
        responseData.setCmsResponse(toCmsResponse(code));
        responseData.setSuccess(responseCodeEnum == ResponseCodeEnum.Success);
        return responseData;
    }

    public IProcessMessage setResponse(IProcessMessage responseData, ResponseCodeEnum responseCodeEnum, String message) {
        setResponse(responseData, responseCodeEnum);
        responseData.setMessage(message);
        return responseData;
    }

    private CmsResponse toCmsResponse(String code) {
        CmsResponse out = new CmsResponse();
        out.setCode(code);
        out.setHttpCode(Integer.parseInt(code));
        responseCodeRepository.findByCode(code).ifPresent(rc -> {
            out.setShortDescription(rc.getShortDescription());
            out.setFullDescription(rc.getFullDescription());
            out.setAlertType(rc.getAlertType());
            if (rc.getHttpResponseCode() != null && !rc.getHttpResponseCode().isEmpty()) {
                try {
                    out.setHttpCode(Integer.parseInt(rc.getHttpResponseCode()));
                } catch (NumberFormatException ignored) {}
            }
        });
        return out;
    }
}
