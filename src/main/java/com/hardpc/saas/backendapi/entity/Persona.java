package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia para heredar a Cliente y Usuario
public abstract class Persona extends AuditoriaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;
}