package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.StockLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockLocalRepository extends JpaRepository<StockLocal, Long> {
}