package br.com.course.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.core.model.CourseEntity;
import br.com.core.repository.CourseRepository;
import br.com.course.service.CourseService;

/**
 * @author ddnsdevelop
 */
@Service
public class CouserServiceImpl implements CourseService {
	private static final Logger log = LoggerFactory.getLogger(CouserServiceImpl.class);

	
	@Autowired
    private CourseRepository courseRepository;

	@Override
    public Iterable<CourseEntity> list(Pageable pageable) {
		//create();
        log.info("Listing all courses");
        return courseRepository.findAll(pageable);
    }
	private void  create() {
		CourseEntity courseEntity = new CourseEntity();
		courseEntity.setTitle("teste");
		
		this.courseRepository.save(courseEntity);
	}
}
