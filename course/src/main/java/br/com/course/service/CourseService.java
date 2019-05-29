package br.com.course.service;

import org.springframework.data.domain.Pageable;

import br.com.core.model.CourseEntity;

public interface CourseService {
	
	Iterable<CourseEntity> list(Pageable pageable);

}
