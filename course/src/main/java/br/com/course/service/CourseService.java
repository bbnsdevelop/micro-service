package br.com.course.service;

import org.springframework.data.domain.Pageable;

import br.com.course.entities.CourseEntity;

public interface CourseService {
	
	Iterable<CourseEntity> list(Pageable pageable);

}
