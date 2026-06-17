package com.hardpc.saas.backendapi.dto;

import lombok.Data;

@Data
public class SerialDisponibleDTO {
    private Long idItemSerial;
    private String numeroSerie;
    private Long idProducto; // Lo usaremos para agrupar rápidamente en memoria

    // Constructor vacío (obligatorio para Jackson)
    public SerialDisponibleDTO() {}

    // Constructor para la consulta optimizada
    public SerialDisponibleDTO(Long idItemSerial, String numeroSerie, Long idProducto) {
        this.idItemSerial = idItemSerial;
        this.numeroSerie = numeroSerie;
        this.idProducto = idProducto;
    }

    // Getters y Setters...
    public Long getIdItemSerial() { return idItemSerial; }
    public void setIdItemSerial(Long idItemSerial) { this.idItemSerial = idItemSerial; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
}