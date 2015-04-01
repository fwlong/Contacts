package com.graduation.contacts.bean;

public class CallLogNative {
	
	private int id;
	private String phoneNumber;
	private String contactName;
	private String time;
	private int callType;
	private boolean isSelected = false;
	
	public CallLogNative(){
		
	}
	
	public CallLogNative(String phoneNumber,String contactsName,String time,int callType){
		this.phoneNumber = phoneNumber;
		this.contactName = contactsName;
		this.time = time;
		this.callType = callType;
	}
	
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getCallType() {
		return callType;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}
	
	

}
