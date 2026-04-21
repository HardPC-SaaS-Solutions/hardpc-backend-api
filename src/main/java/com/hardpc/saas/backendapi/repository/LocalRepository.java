package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
}