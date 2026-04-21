package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.ItemSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemSerialRepository extends JpaRepository<ItemSerial, Long> {
}