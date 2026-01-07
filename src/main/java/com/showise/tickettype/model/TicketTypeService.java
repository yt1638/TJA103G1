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
		return optional.orElse(null); 
	}

	public List<TicketTypeVO> getAll() {
		return repository.findAll();
	}
	
	public void updateTicket (TicketTypeVO ticketTypeVO) {
		repository.save(ticketTypeVO);
	}
	@Autowired
    private TicketTypeRepository ticketTypeRepository;

    public void update(TicketTypeVO formVO) {

        // 1️⃣ 先用 ID 找原資料（避免整筆被覆蓋成 null）
        TicketTypeVO dbVO = ticketTypeRepository
                .findById(formVO.getTicketTypeId())
                .orElseThrow(() -> new RuntimeException("找不到票種"));

        // 2️⃣ 只更新需要修改的欄位
        dbVO.setTicketPrice(formVO.getTicketPrice());
        dbVO.setTicketDescription(formVO.getTicketDescription());

        // 3️⃣ 存回資料庫
        ticketTypeRepository.save(dbVO);
    }
	


}