package br.com.course.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.course.entities.CourseEntity;
import br.com.course.service.CourseService;

@RestController
@RequestMapping("/api")
public class CourseController {
	
	@Autowired
	private CourseService service;

	@GetMapping("/course")
	public ResponseEntity<Iterable<CourseEntity>> getListCourse(Pageable pageable){
		return ResponseEntity.status(HttpStatus.OK).body(this.service.list(pageable));
	}
	
	
}
