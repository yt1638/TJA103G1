package com.showise.tickettype.model;

import java.util.List;

public class TicketTypeService {

	public TicketTypeDAO_interface tao;

	public TicketTypeService() {
		tao = new TicketTypeDAO();
	}

	public TicketTypeVO addTicket(String TicketName, Integer TicketPrice,
			String TicketDescription, byte[] TicketTypeImage ) {

		TicketTypeVO ticketVO = new TicketTypeVO();

		ticketVO.setTicketName(TicketName);
		ticketVO.setTicketPrice(TicketPrice);
		ticketVO.setTicketDescription(TicketDescription);
		ticketVO.setTicketImage(TicketTypeImage);
		tao.insert(ticketVO);

		return ticketVO;
	}

	// 在 TicketTypeService.java 裡

	public TicketTypeVO updateTicket(Integer ticketID, String ticketName,
	                             Integer ticketPrice, String ticketDescription,
	                             byte[] ticketImage) {

	    TicketTypeVO ticketVO = new TicketTypeVO();
	    ticketVO.setTicketID(ticketID);               // ★ 要帶 ID 進去，才知道要改哪一筆
	    ticketVO.setTicketName(ticketName);
	    ticketVO.setTicketPrice(ticketPrice);
	    ticketVO.setTicketDescription(ticketDescription);
	    ticketVO.setTicketImage(ticketImage);

	    tao.update(ticketVO);                         // ★ 呼叫 DAO 做 UPDATE
	    return ticketVO;
	}


	public void deleteTicket(Integer TicketID) {
		tao.delete(TicketID);
	}

	public TicketTypeVO getOneticket(Integer TicketID) {
		return tao.findByPrimaryKey(TicketID);
	}

	public List<TicketTypeVO> getAll() {
		return tao.getAll();
	}

	public TicketTypeVO getOneTicket(Integer TicketID) {
		// TODO Auto-generated method stub
		return tao.findByPrimaryKey(TicketID);
	}


}
