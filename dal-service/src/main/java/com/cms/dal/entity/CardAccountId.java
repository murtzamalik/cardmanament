package com.cms.dal.entity;

import java.io.Serializable;
import java.util.Objects;

public class CardAccountId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pan;
    private String accountNum;

    public CardAccountId() {}

    public CardAccountId(String pan, String accountNum) {
        this.pan = pan;
        this.accountNum = accountNum;
    }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getAccountNum() { return accountNum; }
    public void setAccountNum(String accountNum) { this.accountNum = accountNum; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardAccountId that = (CardAccountId) o;
        return Objects.equals(pan, that.pan) && Objects.equals(accountNum, that.accountNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pan, accountNum);
    }
}
