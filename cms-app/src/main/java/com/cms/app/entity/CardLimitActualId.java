package com.cms.app.entity;

import java.io.Serializable;
import java.util.Objects;

public class CardLimitActualId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pan;
    private String channelCode;
    private String tranCode;

    public CardLimitActualId() {}

    public CardLimitActualId(String pan, String channelCode, String tranCode) {
        this.pan = pan;
        this.channelCode = channelCode;
        this.tranCode = tranCode;
    }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getTranCode() { return tranCode; }
    public void setTranCode(String tranCode) { this.tranCode = tranCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardLimitActualId that = (CardLimitActualId) o;
        return Objects.equals(pan, that.pan)
                && Objects.equals(channelCode, that.channelCode)
                && Objects.equals(tranCode, that.tranCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pan, channelCode, tranCode);
    }
}
