package com.showise.employeedata.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class HibernateUtil_CompositeQuery_employee_data {

    public static List<EmployeeDataVO> getAllC(Map<String, String[]> map, Session session) {

        List<EmployeeDataVO> list = new ArrayList<>();

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<EmployeeDataVO> cq = cb.createQuery(EmployeeDataVO.class);
            Root<EmployeeDataVO> root = cq.from(EmployeeDataVO.class);

            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : map.entrySet()) {

                String key = entry.getKey();

                if ("action".equals(key) || "page".equals(key)) {
                    continue;
                }

                String[] values = entry.getValue();
                if (values == null || values.length == 0) continue;

                String value = values[0];
                if (value == null || value.trim().isEmpty()) continue;
                value = value.trim();

                switch (key) {

                    case "empId":
                        predicates.add(cb.equal(root.get("empId"), Integer.valueOf(value)));
                        break;

                    case "empName":
                        predicates.add(cb.like(root.get("empName"), "%" + value + "%"));
                        break;
                        
                    case "empCreateTimeFrom":
                        LocalDate from = LocalDate.parse(value);  
                        predicates.add(cb.greaterThanOrEqualTo(root.get("empCreateTime"), from));
                        break;

                    case "empCreateTimeTo":
                        LocalDate to = LocalDate.parse(value);
                        predicates.add(cb.lessThanOrEqualTo(root.get("empCreateTime"), to));
                        break;
 
                }
            }

            cq.select(root);
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[0]));
            }

            cq.orderBy(cb.asc(root.get("empId")));

            list = session.createQuery(cq).getResultList();

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return list;
    }
}
