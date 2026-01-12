package com.showise.message.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

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
    
    @Column(name = "MSG_content", nullable = false)
    private String msgContent;

    @Column(name = "MSG_subject", nullable = false)
    private String msgSubject;
    
    @Column(name = "pre_hours", nullable = false)
    private Integer preHours;
    
    @Column(name = "MSG_type", nullable = false)
    private Short msgType;

	public Integer getMsgNo() {
		return msgNo;
	}

	public void setMsgNo(Integer msgNo) {
		this.msgNo = msgNo;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getMsgSubject() {
		return msgSubject;
	}

	public void setMsgSubject(String msgSubject) {
		this.msgSubject = msgSubject;
	}

	public Integer getPreHours() {
		return preHours;
	}

	public void setPreHours(Integer preHours) {
		this.preHours = preHours;
	}

	public Short getMsgType() {
		return msgType;
	}

	public void setMsgType(Short msgType) {
		this.msgType = msgType;
	}

	public MessageVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MessageVO(Integer msgNo) {
		super();
		this.msgNo = msgNo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(msgContent, msgNo, msgSubject, msgType, preHours);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageVO other = (MessageVO) obj;
		return Objects.equals(msgContent, other.msgContent) && Objects.equals(msgNo, other.msgNo)
				&& Objects.equals(msgSubject, other.msgSubject) && Objects.equals(msgType, other.msgType)
				&& Objects.equals(preHours, other.preHours);
	}





    
	
}