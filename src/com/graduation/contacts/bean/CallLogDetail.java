package com.graduation.contacts.bean;

import java.util.ArrayList;
import java.util.List;

public class CallLogDetail {

	public String name;
	public String photoId;
	public String number;
	public List<DetailLog> logs = new ArrayList<DetailLog>();
	
	
	public static class DetailLog{
		public String date;
		public String time;
		public int type;
		public String duration;
		
	}
}
