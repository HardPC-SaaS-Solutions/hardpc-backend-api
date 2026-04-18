package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "locales")
public class Local extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLocal;
}