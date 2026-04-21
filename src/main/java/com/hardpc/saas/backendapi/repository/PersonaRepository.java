package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
}