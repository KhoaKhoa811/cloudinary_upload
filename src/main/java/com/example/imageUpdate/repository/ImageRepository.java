package com.example.imageUpdate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.imageUpdate.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer>{

	List<Image> findByOrderById();
}
