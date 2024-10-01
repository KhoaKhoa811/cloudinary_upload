package com.example.imageUpdate.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.imageUpdate.entity.Video;
import com.example.imageUpdate.repository.VideoRepository;

@Service
public class VideoService {

	@Autowired
	VideoRepository videoRepository;
	
	public List<Video> list() {return videoRepository.findByOrderById();}
	
	public Optional<Video> getOne(int id) {return videoRepository.findById(id);}
	
	public void save(Video video) {videoRepository.save(video);}
	
	public void delete(int id) {videoRepository.deleteById(id);}
	
	public boolean exists(int id) {return videoRepository.existsById(id);}
}
