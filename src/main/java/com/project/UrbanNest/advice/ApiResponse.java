package com.project.UrbanNest.advice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private T Data;
    private ApiError error;

    public ApiResponse(){
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(T Data){
        this();
        this.Data=Data;
    }

    public ApiResponse(ApiError error){
        this();
        this.error=error;
    }



}
