package com.showise.order.model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Repository
@Transactional(readOnly=true)
public class OrderCompositeNativeSQLRepository {
	
	@Autowired
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<OrderVO> getAll(Map<String,String[]> map){
		StringBuilder sql=new StringBuilder("SELECT * FROM order_info WHERE 1=1");
		
		Map<String,Object> params = new HashMap<>();
		//訂單編號
		if(map.containsKey("orderId")) {
			String value =map.get("orderId")[0];
			if(value != null && value.trim().length() != 0) {
				sql.append(" AND order_id = :orderId");
				params.put("orderId", Integer.valueOf(value));
			}
		}
		//會員編號
		if(map.containsKey("memberId")){
			String value=map.get("memberId")[0];
			if(value != null && value.trim().length() != 0) {
				sql.append(" AND member_id = :memberId");
				params.put("memberId", Integer.valueOf(value));
			}
		}
		//訂單狀態
		if(map.containsKey("orderStatus")) {
			String value =map.get("orderStatus")[0];
			if(value != null && value.trim().length() != 0) {
				sql.append(" AND order_status = :orderStatus");
				params.put("orderStatus", Integer.valueOf(value));
			}
		}
		//起始日期
		if(map.containsKey("startDate")) {
			String value = map.get("startDate")[0];
			if(value != null && value.trim().length() != 0) {
				sql.append(" AND order_create_time >= :startDate");
				params.put("startDate",Timestamp.valueOf(value+" 00:00:00"));
			}
		}
		//結束日期
		if(map.containsKey("endDate")) {
			String value = map.get("endDate")[0];
			if(value != null && value.trim().length() != 0) {
				sql.append(" AND order_create_time <= :endDate");
				params.put("endDate", Timestamp.valueOf(value+" 23:59:59"));
			}
		}
		
//		sql.append(" ORDER BY order_id");
		
		Query query = em.createNativeQuery(sql.toString(),OrderVO.class);
		
		for(Map.Entry<String , Object> entry:params.entrySet()) {
			query.setParameter(entry.getKey() , entry.getValue());
		}
		return query.getResultList();
	}
}
