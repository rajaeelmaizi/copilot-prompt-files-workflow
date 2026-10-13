package com.example.petshop.repository;

import com.example.petshop.model.PetOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PetOrderRepository extends JpaRepository<PetOrder, UUID> {

    boolean existsByOrderReference(String orderReference);
}
