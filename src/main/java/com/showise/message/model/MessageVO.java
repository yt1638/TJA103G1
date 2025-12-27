package com.showise.message.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.format.annotation.DateTimeFormat;

import com.showise.tickettype.model.TicketTypeVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "msg")
public class MessageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSGNO")
    private Integer msgNo;

    @Column(name = "MSGCONT", nullable = false)
    @NotNull(message = "發送內容: 請勿空白")
    private String msgCont;
    
    @Column(name = "MSGTIME", nullable = false)
    @NotNull(message = "發送時間: 請勿空白")
    private Integer msgTime;
    
    @Column(name = "MSGTYPE", nullable = false)
    @NotNull(message = "通知類型: 請勿空白")
    private Short msgType;





    public MessageVO getmsgNo(MessageVO msgNo) {
        return msgNo;

    }



	public void setMsgNo(Integer msgNo) {
		this.msgNo = msgNo;
	}



	public String getMsgCont() {
		return msgCont;
	}



	public void setMsgCont(String msgCont) {
		this.msgCont = msgCont;
	}



	public Integer getMsgTime() {
		return msgTime;
	}



	public void setMsgTime(Integer msgTime) {
		this.msgTime = msgTime;
	}



	public Short getMsgType() {
		return msgType;
	}



	public void setMsgType(Short msgType) {
		this.msgType = msgType;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}