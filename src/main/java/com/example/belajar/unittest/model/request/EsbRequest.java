package com.example.belajar.unittest.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EsbRequest {
    private transient HttpEntity payload;
    private Boolean isPlain;
    private LinkedMultiValueMap<String, String> params;
}
