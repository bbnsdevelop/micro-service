package br.com.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import br.com.core.model.CourseEntity;

@Repository
public interface CourseRepository extends PagingAndSortingRepository<CourseEntity, Long>{

}
