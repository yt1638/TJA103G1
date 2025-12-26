package com.showise.tickettype.model;

import java.util.List;

public interface TicketTypeDAO_interface {
          public void insert(TicketTypeVO tktVO);
          public void update(TicketTypeVO tktVO);
          public void delete(Integer tktno);
          public TicketTypeVO findByPrimaryKey(Integer tktno);
          public List<TicketTypeVO> getAll();
          //萬用複合查詢(傳入參數型態Map)(回傳 List)
//        public List<EmpVO> getAll(Map<String, String[]> map); 
}
