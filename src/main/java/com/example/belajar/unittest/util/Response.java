package com.example.belajar.unittest.util;

import lombok.Data;

@Data
public class Response {

    private Object data;
    private String message;
    private Boolean result;

    public Response(Object data, String message, Boolean result) {
        this.data = data;
        this.message = message;
        this.result = result;
    }

}
