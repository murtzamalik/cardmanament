package com.cms.dal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RESPONSE_CODE")
public class ResponseCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "response_code_seq_gen")
    @SequenceGenerator(name = "response_code_seq_gen", sequenceName = "RESPONSE_CODE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", length = 20)
    private String code;

    @Column(name = "SHORT_DESCRIPTION", length = 500)
    private String shortDescription;

    @Column(name = "FULL_DESCRIPTION", length = 1000)
    private String fullDescription;

    @Column(name = "ALERT_TYPE", length = 50)
    private String alertType;

    @Column(name = "HTTP_RESPONSE_CODE", length = 10)
    private String httpResponseCode;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getHttpResponseCode() { return httpResponseCode; }
    public void setHttpResponseCode(String httpResponseCode) { this.httpResponseCode = httpResponseCode; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
