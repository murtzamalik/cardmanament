package com.cms.common.enums;

public enum ResponseCodeEnum {
    Success(200),
    Warning(300),
    InternalServerError(500),
    BadRequest(400),
    Forbidden(403),
    Invalid(600),
    InvalidCardID(101),
    CustomerNotAvailable(102),
    CustomerNotFound(103),
    BranchNotMatchWithCard(104),
    CardNotFound(105),
    TransactionFailed(106),
    IncorrectPassword(107),
    InvalidUsername(108),
    BranchNotExist(109),
    Conflict(409),
    Unauthorized(401),
    NotFound(404),
    BatchNotProvided(110),
    BatchNotFound(111),
    ProgramNotFound(112),
    BinRangeUnavailable(113),
    CardReqNonPersonalizeNotExist(114),
    ProductCodeNotExist(115),
    IMDFetchFailed(116),
    PINGenerationFailed(117),
    HSMError(118),
    PINPrintingFailed(119),
    UnableToOpenPINMailerPort(120),
    ExportFailed(121),
    IncorrectFormat(122),
    PANGenerationFailed(123),
    CardPrintingFailed(125),
    GroupAlreadyAssigned(124);

    private final int httpCode;

    ResponseCodeEnum(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getCode() {
        return String.valueOf(httpCode);
    }
}
