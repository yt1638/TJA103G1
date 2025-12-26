package com.showise.tickettype.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("tickettype")
public class TicketTypeService {

	@Autowired
	TicketTypeRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	


	public TicketTypeVO getOneById(Integer ticketTypeId) {
		Optional<TicketTypeVO> optional = repository.findById(ticketTypeId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<TicketTypeVO> getAll() {
		return repository.findAll();
	}
	
	public void updateTicket (TicketTypeVO ticketTypeVO) {
		repository.save(ticketTypeVO);
	}
	


}