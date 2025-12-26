package com.showise.movietype.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MovieTypeService {
	
	@Autowired
	MovieTypeRepository repository;
	@Autowired
	private SessionFactory sessionFactory;
	
	public MovieTypeVO getById(Integer movieTypeId) {
		Optional<MovieTypeVO> optional = repository.findById(movieTypeId);
		return optional.orElse(null);
	}
	
	public List<MovieTypeVO> listAll(){
		return repository.findAll();
	}

}
