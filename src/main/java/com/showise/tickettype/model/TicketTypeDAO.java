package com.showise.tickettype.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class TicketTypeDAO implements TicketTypeDAO_interface {

    private static DataSource ds;
    static {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/Ticket");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public static final String INSERT_STMT =
        "INSERT INTO ticket_type (ticket_NAME, ticket_PRICE, ticket_DESCRIPTION, ticket_IMAGES) VALUES (?, ?, ?, ?)";
    public static final String GET_ALL_STMT =
        "SELECT ticket_type_id, ticket_NAME, ticket_PRICE, ticket_DESCRIPTION, ticket_IMAGES FROM ticket_type order by ticket_type_id";
    public static final String GET_ONE_STMT =
        "SELECT ticket_type_id, ticket_NAME, ticket_PRICE, ticket_DESCRIPTION, ticket_IMAGES FROM ticket_type WHERE ticket_type_id = ?";
    public static final String DELETE =
        "DELETE FROM ticket_type where ticket_type_id = ?";
    public static final String UPDATE =
        "UPDATE ticket_type set ticket_NAME=?, ticket_PRICE=?, ticket_DESCRIPTION=?, ticket_IMAGES=? where ticket_type_id = ?";

    @Override
    public void insert(TicketTypeVO ticketVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(INSERT_STMT);

            pstmt.setString(1, ticketVO.getTicketName());
            pstmt.setInt(2, ticketVO.getTicketPrice());
            pstmt.setString(3, ticketVO.getTicketDescription());
            pstmt.setBytes(4, ticketVO.getTicketImage());

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. " + se.getMessage());
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException se) { se.printStackTrace(System.err); }
            if (con != null) try { con.close(); } catch (Exception e) { e.printStackTrace(System.err); }
        }
    }

    @Override
    public void update(TicketTypeVO ticketVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(UPDATE);

            pstmt.setString(1, ticketVO.getTicketName());
            pstmt.setInt(2, ticketVO.getTicketPrice());
            pstmt.setString(3, ticketVO.getTicketDescription());
            pstmt.setBytes(4, ticketVO.getTicketImage());

            // ★這個：where ticket_type_id = ?
            pstmt.setInt(5, ticketVO.getTicketID());

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. " + se.getMessage());
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException se) { se.printStackTrace(System.err); }
            if (con != null) try { con.close(); } catch (Exception e) { e.printStackTrace(System.err); }
        }
    }

    @Override
    public void delete(Integer ticketID) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(DELETE);

            pstmt.setInt(1, ticketID);

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. " + se.getMessage());
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException se) { se.printStackTrace(System.err); }
            if (con != null) try { con.close(); } catch (Exception e) { e.printStackTrace(System.err); }
        }
    }

    @Override
    public TicketTypeVO findByPrimaryKey(Integer ticketID) {
        TicketTypeVO ticketVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_ONE_STMT);
            pstmt.setInt(1, ticketID);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                ticketVO = new TicketTypeVO();
                // ★正確：從 ResultSet 取值塞進 VO
                ticketVO.setTicketID(rs.getInt("ticket_type_id"));
                ticketVO.setTicketName(rs.getString("ticket_NAME"));

                // 若 DB ticket_PRICE 可能為 NULL，用 getObject 才能拿到 null
                Integer price = (Integer) rs.getObject("ticket_PRICE");
                ticketVO.setTicketPrice(price);

                ticketVO.setTicketDescription(rs.getString("ticket_DESCRIPTION"));
                ticketVO.setTicketImage(rs.getBytes("ticket_IMAGES"));
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. " + se.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException se) { se.printStackTrace(System.err); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException se) { se.printStackTrace(System.err); }
            if (con != null) try { con.close(); } catch (Exception e) { e.printStackTrace(System.err); }
        }

        return ticketVO;
    }

    @Override
    public List<TicketTypeVO> getAll() {
        List<TicketTypeVO> list = new ArrayList<>();
        TicketTypeVO ticketVO = null;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_ALL_STMT);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ticketVO = new TicketTypeVO();
                ticketVO.setTicketID(rs.getInt("ticket_type_id"));
                ticketVO.setTicketName(rs.getString("ticket_NAME"));
                ticketVO.setTicketPrice((Integer) rs.getObject("ticket_PRICE"));
                ticketVO.setTicketDescription(rs.getString("ticket_DESCRIPTION"));
                ticketVO.setTicketImage(rs.getBytes("ticket_IMAGES"));
                list.add(ticketVO);
            }
            

        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. " + se.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException se) { se.printStackTrace(System.err); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException se) { se.printStackTrace(System.err); }
            if (con != null) try { con.close(); } catch (Exception e) { e.printStackTrace(System.err); }
        }
        return list;
    }
}
