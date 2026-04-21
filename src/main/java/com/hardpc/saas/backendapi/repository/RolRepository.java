package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
}