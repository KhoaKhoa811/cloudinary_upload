package com.example.imageUpdate.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.imageUpdate.entity.Image;
import com.example.imageUpdate.service.CloudinaryService;
import com.example.imageUpdate.service.ImageService;

@RestController
@RequestMapping("/cloudinary")
public class CloudinaryImageController {

	@Autowired
	CloudinaryService cloudinaryService;
	@Autowired
	ImageService imageService;
	
	@GetMapping("/list")
	public ResponseEntity<List<Image>> list() {
		List<Image> list = imageService.list();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/upload", consumes = "multipart/form-data")
	@ResponseBody
	public ResponseEntity<String> updoad(@RequestParam("image") MultipartFile multipartFile) throws IOException {
		BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
		if (bi == null) {
			return new ResponseEntity<>("Image non valide!", HttpStatus.BAD_REQUEST);
		}
		Map result = cloudinaryService.uploadImage(multipartFile, "test1");
		Image image = new Image((String) result.get("original_filename"),
				(String) result.get("url"),
				(String) result.get("public_id"));
		imageService.save(image);
		return new ResponseEntity<>("image success!", HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") int id) {
		Optional<Image> imageOptional = imageService.getOne(id);
		if (imageOptional.isEmpty()) {
			return new ResponseEntity<>("not exists!", HttpStatus.NOT_FOUND);
		}
		Image image = imageOptional.get();
		String cloudinaryImageId = image.getImageId();
		try {
			cloudinaryService.deleteImage(cloudinaryImageId, "test1");
		} catch (IOException e){
			return new ResponseEntity<>("Failed to delete image from Cloudinary", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		imageService.delete(id);
		return new ResponseEntity<>("image deleted!", HttpStatus.OK);
	}
}
