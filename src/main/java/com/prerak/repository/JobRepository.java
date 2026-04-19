package com.prerak.repository;

import com.prerak.entity.Job;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
	
	Optional<Job> findById(Long id);
}