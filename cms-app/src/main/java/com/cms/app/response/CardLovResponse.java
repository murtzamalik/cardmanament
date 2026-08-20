package com.cms.app.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CardLovResponse {
    private String type;
    private Map<String, String> lov = new HashMap<>();
}
