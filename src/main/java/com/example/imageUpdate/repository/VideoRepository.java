package com.example.imageUpdate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.imageUpdate.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer>{

	List<Video> findByOrderById();
}
