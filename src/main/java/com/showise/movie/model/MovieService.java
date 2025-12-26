package com.showise.movie.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MovieService {
	
	@Autowired
	MovieRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public void addMovie(MovieVO movieVO) {
		repository.save(movieVO);
	}
	@Transactional
	public void updateMovie(MovieVO movieVO) {
		repository.save(movieVO);
	}
	
	public MovieVO getById(Integer movieId) {
		Optional<MovieVO> optional = repository.findById(movieId);
		return optional.orElse(null);
	}
	
	public List<MovieVO> listAll(){
		return repository.findAll();
	}
	
	public List<MovieVO> findAllByOrderByMovieIdDesc(){
		return repository.findAllByOrderByMovieIdDesc();
	}
	
	public List<MovieVO> listByMovieName(String nameTw){
		return repository.findByMovieName(nameTw);
	}
	
	public List<MovieVO> listByStatus(Integer status){
		return repository.findByStatus(status);
	}
	
	public List<MovieVO> listByType(Integer movieTypeId){
		return repository.findByType(movieTypeId);
	}
}
