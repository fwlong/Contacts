package com.graduation.contacts.bean;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.graduation.contacts.utils.FormatUtils;

public class ContactNative {
    
    private int id;
    
    private String contactName;
    
    private List<String> contactMobileNumbers;
    
    private List<String> contactHomeNumbers;
    
    private List<String> contactWorkNumbers;
    
    private List<String> contactOtherNumbers;
    
    private String contactEmail;
    
    private String photoId;
    
    private String address;
    
    private byte[] photoBytes;
    
    private String SortLetters;
    
    private boolean isSelected;
    
    
    
    public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getSortLetters() {
		return SortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.SortLetters = sortLetters;
	}


    public int getId() {
        return id;
    }

    public byte[] getPhotoBytes() {
        return photoBytes;
    }

    public void setPhotoBytes(byte[] photoBytes) {
        this.photoBytes = photoBytes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
    	if(contactName==null||"".equals(contactName)){
    		contactName="#";
    	}
        this.contactName = contactName;
        this.setSortLetters(FormatUtils.getSortString(contactName));
    }


    public void addMobileNumbers(String number){
        if(contactMobileNumbers==null) 
            contactMobileNumbers = new ArrayList<String>();
        contactMobileNumbers.add(number);
    }
    
    public void addHomeNumbers(String number){
        if(contactHomeNumbers==null) 
            contactHomeNumbers = new ArrayList<String>();
        contactHomeNumbers.add(number);
    }
    
    public void addWorkNumbers(String number){
        if(contactWorkNumbers==null) 
            contactWorkNumbers = new ArrayList<String>();
        contactWorkNumbers.add(number);
    }
    
    public void addOtherNumbers(String number){
        if(contactOtherNumbers==null) 
            contactOtherNumbers = new ArrayList<String>();
        contactOtherNumbers.add(number);
    }
    
    public List<String> getMobileNumbers(){
        return contactMobileNumbers;
    }
    
    public List<String> getHomeNumbers(){
        return contactHomeNumbers;
    }
    
    public List<String> getWorkNumbers(){
        return contactWorkNumbers;
    }
    
    public List<String> getOtherNumbers(){
        return contactOtherNumbers;
    }
    
    
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    

}
