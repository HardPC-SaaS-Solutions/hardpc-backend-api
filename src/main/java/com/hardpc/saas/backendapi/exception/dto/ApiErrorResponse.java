package com.hardpc.saas.backendapi.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Oculta los campos nulos en el JSON de salida
public class ApiErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "America/Lima")
    private ZonedDateTime timestamp;

    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;

    private List<FieldErrorDTO> detalles;
}