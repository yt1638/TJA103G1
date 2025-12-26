package com.showise.ticket.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketTypeJDBCDAO implements TicketTypeDAO_interface {
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/db02?serverTimezone=Asia/Taipei";
	String userid = "root";
	String passwd = "123456";

	public static final String INSERT_STMT = 
			"INSERT INTO ticket_type (ticket_NAME, ticket_PRICE, ticket_DESCRIPTION, ticket_IMAGES) VALUES (?, ?, ?, ?)";
	public static final String GET_ALL_STMT = 
			"SELECT ticket_type_id, ticket_NAME, ticket_PRICE, ticket_DESCRIPTION, ticket_IMAGES FROM ticket_type order by ticket_type_id";
	public static final String GET_ONE_STMT = 
			"SELECT ticket_type_id, ticket_NAME, ticket_PRICE, ticket_DESCRIPTION, ticket_IMAGES FROM ticket_type where ticket_type_id = ?";
	public static final String DELETE = 
			"DELETE FROM ticket_type where ticket_type_id = ?";
	public static final String UPDATE = 
			"UPDATE ticket_type set ticket_NAME=?, ticket_PRICE=?, ticket_DESCRIPTION=?, ticket_IMAGES=? where ticket_type_id = ?";

	@Override
	public void insert(TicketTypeVO tktVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			pstmt.setInt(1, tktVO.getTicketID());
			pstmt.setString(2, tktVO.getTicketName());		
			pstmt.setInt(3, tktVO.getTicketPrice());
			pstmt.setString(4, tktVO.getTicketDescription());
			pstmt.setBytes(5, tktVO.getTicketImage());

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void update(TicketTypeVO tktVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			pstmt.setInt(1, tktVO.getTicketID());
			pstmt.setString(2, tktVO.getTicketName());		
			pstmt.setInt(3, tktVO.getTicketPrice());
			pstmt.setString(4, tktVO.getTicketDescription());
			pstmt.setBytes(5, tktVO.getTicketImage());

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void delete(Integer TicketID) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, TicketID);

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public TicketTypeVO findByPrimaryKey(Integer TicketID) {

		TicketTypeVO tktVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1, TicketID);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// empVo 也稱為 Domain objects
				tktVO = new TicketTypeVO();
				tktVO.setTicketID(rs.getInt("TicketID"));
				tktVO.setTicketName(rs.getString("TKTNAME"));
				tktVO.setTicketPrice(rs.getInt("TKTPRICE"));
				tktVO.setTicketDescription(rs.getString("TKTDESC"));
				tktVO.setTicketImage(rs.getBytes("TKTIMAGE"));

			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return tktVO;
	}

	@Override
	public List<TicketTypeVO> getAll() {
		List<TicketTypeVO> list = new ArrayList<TicketTypeVO>();
		TicketTypeVO tktVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// empVO 也稱為 Domain objects
				tktVO = new TicketTypeVO();
				tktVO.setTicketID(rs.getInt("ticket_type_id"));
				tktVO.setTicketName(rs.getString("ticket_NAME"));
				tktVO.setTicketPrice(rs.getInt("ticket_PRICE"));
				tktVO.setTicketDescription(rs.getString("ticket_DESCRIPTION"));
				tktVO.setTicketImage(rs.getBytes("ticket_IMAGES"));
				list.add(tktVO); // Store the row in the list
			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return list;
	}

	public static void main(String[] args) {

		TicketTypeJDBCDAO tao = new TicketTypeJDBCDAO();

		// 新增
//		EmpVO empVO1 = new EmpVO();
//		empVO1.setEname("吳永志1");
//		empVO1.setJob("MANAGER");
//		empVO1.setHiredate(java.sql.Date.valueOf("2005-01-01"));
//		empVO1.setSal(Double.valueOf(50000));
//		empVO1.setComm(Double.valueOf(500));
//		empVO1.setDeptno(10);
//		dao.insert(empVO1);

		// 修改
//		EmpVO empVO2 = new EmpVO();
//		empVO2.setEmpno(7001);
//		empVO2.setEname("吳永志2");
//		empVO2.setJob("MANAGER2");
//		empVO2.setHiredate(java.sql.Date.valueOf("2002-01-01"));
//		empVO2.setSal(Double.valueOf(20000));
//		empVO2.setComm(Double.valueOf(200));
//		empVO2.setDeptno(20);
//		dao.update(empVO2);

		// 刪除
//		dao.delete(7014);

		// 查詢
//		EmpVO empVO3 = dao.findByPrimaryKey(7001);
//		System.out.print(empVO3.getEmpno() + ",");
//		System.out.print(empVO3.getEname() + ",");
//		System.out.print(empVO3.getJob() + ",");
//		System.out.print(empVO3.getHiredate() + ",");
//		System.out.print(empVO3.getSal() + ",");
//		System.out.print(empVO3.getComm() + ",");
//		System.out.println(empVO3.getDeptno());
//		System.out.println("---------------------");

		// 查詢
		List<TicketTypeVO> list = tao.getAll();
		for (TicketTypeVO atkt : list) {
			System.out.print(atkt.getTicketID() + ",");
			System.out.print(atkt.getTicketName() + ",");
			System.out.print(atkt.getTicketPrice() + ",");
			System.out.print(atkt.getTicketDescription() + ",");
			System.out.print(atkt.getTicketImage() + ",");
			System.out.println();
		}
	}
}