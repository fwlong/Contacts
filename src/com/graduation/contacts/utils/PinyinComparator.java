package com.graduation.contacts.utils;

import java.util.Comparator;

import com.graduation.contacts.bean.ContactNative;


public class PinyinComparator implements Comparator<ContactNative> {

	public int compare(ContactNative o1, ContactNative o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
