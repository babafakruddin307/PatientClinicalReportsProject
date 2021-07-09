package com.baba.clients.api.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baba.clients.api.model.ClinicalData;

public interface ClinicalDataRepo extends JpaRepository<ClinicalData, Integer> {

}
