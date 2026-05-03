package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "productos")
@JsonPropertyOrder({"idProducto", "codigoSku", "descripcion", "precioUsd", "mesesGarantia", "marca", "categoria", "unidadMedida", "esSerializado", "estado", "fechaCreacion", "fechaActualizacion"})
public class Producto extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @NotBlank(message = "El código SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede exceder los 50 caracteres")
    @Column(name = "codigo_sku", nullable = false, unique = true, length = 50)
    private String codigoSku;

    @NotBlank(message = "La descripción del producto es obligatoria")
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    @Column(name = "precio_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUsd;

    @NotNull(message = "Los meses de garantía son obligatorios")
    @Min(value = 0, message = "Los meses de garantía no pueden ser negativos")
    @Column(name = "meses_garantia", nullable = false)
    private Integer mesesGarantia;

    @NotNull(message = "Debe especificar si es serializado")
    @Column(name = "es_serializado", nullable = false)
    private Boolean esSerializado;

    @ManyToOne
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;
}