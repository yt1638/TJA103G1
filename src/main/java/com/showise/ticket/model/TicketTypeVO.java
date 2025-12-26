package com.showise.ticket.model;


public class TicketTypeVO implements java.io.Serializable{
	private Integer TicketID;
	private String  TicketName;
	private Integer TicketPrice;
	private String  TicketDescription;
	private byte[]  TicketTypeImage;
	
	public Integer getTicketID() {
		return TicketID;
	}
	public void setTicketID(Integer TicketID) {
		this.TicketID = TicketID;
	}
	public String getTicketName() {
		return TicketName;
	}
	public void setTicketName(String TicketName) {
		this.TicketName = TicketName;
	}
	public Integer getTicketPrice() {
		return TicketPrice;
	}
	public void setTicketPrice(Integer TicketPrice) {
		this.TicketPrice = TicketPrice;
	}
	public String getTicketDescription() {
		return TicketDescription;
	}
	public void setTicketDescription(String TicketDescription) {
		this.TicketDescription = TicketDescription;
	}
	public byte[] getTicketImage() {
		return TicketTypeImage;
	}
	public void setTicketImage(byte[] TicketTypeImage) {
		this.TicketTypeImage = TicketTypeImage;
	}

}
