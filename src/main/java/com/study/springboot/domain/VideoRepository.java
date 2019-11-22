package com.study.springboot.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoEntity, Long>{
	Optional<VideoEntity> findByName(String videoName);
}
