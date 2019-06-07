package br.com.course.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.core.model.CourseEntity;
import br.com.course.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/courses")
@Api(value = "Endpoints to manage course")
public class CourseController {
	
	@Autowired
	private CourseService service;

	@GetMapping
	@ApiOperation(value = "List all available courses", response = CourseEntity[].class)
	public ResponseEntity<Iterable<CourseEntity>> getListCourse(Pageable pageable){
		return ResponseEntity.status(HttpStatus.OK).body(this.service.list(pageable));
	}
	
	
}
