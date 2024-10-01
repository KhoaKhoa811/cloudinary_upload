package com.example.imageUpdate.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.imageUpdate.entity.Video;
import com.example.imageUpdate.service.CloudinaryService;
import com.example.imageUpdate.service.VideoService;

@RestController
@RequestMapping("/cloudinary/video")
public class CloudinaryVideoController {

	@Autowired
	CloudinaryService cloudinaryService;
	@Autowired
	VideoService videoService;
	
	@GetMapping("/list")
	public ResponseEntity<List<Video>> list() {
		List<Video> list = videoService.list();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/upload", consumes = "multipart/form-data")
	@ResponseBody
	public ResponseEntity<String> upload(@RequestParam("video") MultipartFile multipartFile) throws IOException {
		if (multipartFile.getContentType() == null || !multipartFile.getContentType().equals("video/mp4")) {
		    return new ResponseEntity<>("Tệp không phải video MP4!", HttpStatus.BAD_REQUEST);
		}
		Map result = cloudinaryService.uploadVideo(multipartFile, "test2");
		Video video = new Video((String) result.get("original_filename"),
				(String) result.get("url"),
				(String) result.get("public_id"));
		videoService.save(video);
		return new ResponseEntity<>("upload success!", HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") int id) {
		Optional<Video> videoOptional = videoService.getOne(id);
		if (videoOptional.isEmpty()) {
			return new ResponseEntity<>("not exists!", HttpStatus.NOT_FOUND);
		}
		
		Video video = videoOptional.get();
		String cloudinaryVideoId = video.getVideoId();
		try {
			cloudinaryService.deleteVideo(cloudinaryVideoId, "test2");
		} catch (IOException e) {
			return new ResponseEntity<>("delete failed!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		videoService.delete(id);
		return new ResponseEntity<>("delete successed!", HttpStatus.OK);
	}
}
