package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "productos")
@AllArgsConstructor
@NoArgsConstructor
public class Producto extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @NotNull
    @Column(unique = true, nullable = false)
    //SKU único = Control de inventario real
    private String codigoSku;

    @NotBlank
    private String descripcion;

    @NotNull
    @Positive
    private BigDecimal precioSoles;

    @NotNull
    private Integer mesesGarantia;

    @NotNull
    private Boolean esSerializado;

    //Relaciones
    //FetchType.LAZY = Evita cargar datos innecesarios
    //FetchType.LAZY = Mejora performance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @NotNull
    private Boolean estado;

    private String imagenUrl;
}