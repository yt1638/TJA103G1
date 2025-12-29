package com.showise.cinema.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class CinemaService {
	
	@Autowired
	CinemaRepository repository;
	
	public CinemaVO getById(Integer CinemaId) {
		Optional<CinemaVO> optional = repository.findById(CinemaId);
		return optional.orElse(null);
	}
	
	public List<CinemaVO> getAllCinema(){
		return repository.findAll();
	}
	
	
	
	

}
