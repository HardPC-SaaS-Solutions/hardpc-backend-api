package com.hardpc.saas.backendapi.entity;

import com.hardpc.saas.backendapi.enums.EstadoCaja;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "caja_sesiones")
public class CajaSesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCajaSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @Column(nullable = false)
    private LocalDateTime fechaApertura;

    private LocalDateTime fechaCierre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoApertura;

    @Column(precision = 12, scale = 2)
    private BigDecimal montoCierreEfectivoReal;

    @Column(precision = 12, scale = 2)
    private BigDecimal montoCierreSistema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCaja estado;
}