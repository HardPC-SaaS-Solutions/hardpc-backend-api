package com.hardpc.saas.backendapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stocks_locales", uniqueConstraints = {
        @UniqueConstraint(name = "uk_producto_local", columnNames = {"id_producto", "id_local"})
})
@JsonPropertyOrder({"idStockLocal", "producto", "local", "cantidadActual", "stockMinimo", "fechaCreacion", "fechaActualizacion"})
public class StockLocal extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStockLocal;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull(message = "El local es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @NotNull(message = "La cantidad actual es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;
}