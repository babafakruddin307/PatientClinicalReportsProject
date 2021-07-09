package com.baba.clients.api.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baba.clients.api.model.Patient;

public interface PatientRepo extends JpaRepository<Patient, Integer> {

}
