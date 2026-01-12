package com.showise.movie.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MovieService {
	
	@Autowired
	MovieRepository repository;
	
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
	
	public List<MovieVO> findAllOrderByStatusAndMovieId(){
		return repository.findAllOrderByStatusAndMovieId();
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
	
	public List<MovieVO> listByDate(LocalDate searchDate){
		return repository.findByDate(searchDate);
	}
	
	public List<MovieVO> listByStatusAndType(Integer status,Integer movieTypeId){
		return repository.findByStatusAndType(status, movieTypeId);
	}
	
	public List<MovieVO> findMovieToRemind(LocalDate releaseDate){
		return repository.findMovieToRemind(releaseDate);
	}
}
