package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
@PrimaryKeyJoinColumn(name = "id_usuario") // Opcional pero recomendado
public class Usuario extends Persona {
    // Ojo: No agregar @Id aqui, lo hereda de Persona
}