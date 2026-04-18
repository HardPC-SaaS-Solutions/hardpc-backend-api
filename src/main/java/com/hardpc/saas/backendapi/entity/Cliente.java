package com.hardpc.saas.backendapi.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id_cliente")
public class Cliente extends Persona {
    // Ojo: No agregar @Id aqui, lo hereda de Persona
}