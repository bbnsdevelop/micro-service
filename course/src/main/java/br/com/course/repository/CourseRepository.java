package br.com.course.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import br.com.course.entities.CourseEntity;

@Repository
public interface CourseRepository extends PagingAndSortingRepository<CourseEntity, Long>{

}
