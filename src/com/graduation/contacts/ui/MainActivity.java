package com.graduation.contacts.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.graduation.contacts.R;
import com.graduation.contacts.view.TabButton;

public class MainActivity extends Activity implements OnClickListener {
	
	
	private final static int DIAL_TAB = 0;
	private final static int CALLLOG_TAB = 1;
	private final static int CONTACTS_TAB = 2;
	
	TabButton dialBtn;
	TabButton logBtn;
	TabButton contactsBtn;
	
	DialFragment mDialFragment;
	CalllogFragment mCalllogFragment;
	ContactsFragment mContactsFragment;
	
	FragmentTransaction mTransaction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setSelectedTab(DIAL_TAB);

		
	}
	
	private void initView(){
		Resources res = getResources();
		dialBtn = (TabButton)findViewById(R.id.dailpan);
		dialBtn.setIcon(R.drawable.icon_tab_dialer);
		dialBtn.setText(res.getString(R.string.dial));
		dialBtn.setOnClickListener(this);
		logBtn = (TabButton)findViewById(R.id.calllog);
		logBtn.setIcon(R.drawable.icon_tab_calllog);
		logBtn.setText(res.getString(R.string.calllog));
		logBtn.setOnClickListener(this);
		contactsBtn = (TabButton)findViewById(R.id.contacts);
		contactsBtn.setIcon(R.drawable.icon_tab_contacts);
		contactsBtn.setText(res.getString(R.string.contacts));
		contactsBtn.setOnClickListener(this);
		
		
	}
	
	private void setSelectedTab(int tabId){
		hideFragment();
		mTransaction = this.getFragmentManager().beginTransaction();
		if(tabId==DIAL_TAB){
			setTabBackGround(true, false, false);
			if(mDialFragment==null){
				mDialFragment = new DialFragment();
				mTransaction.add(R.id.content, mDialFragment);
				
			}else{
				mTransaction.show(mDialFragment);
			}
		}else if(tabId==CALLLOG_TAB){
			setTabBackGround(false, true, false);
			if(mCalllogFragment==null){
				mCalllogFragment = new CalllogFragment();
				mTransaction.add(R.id.content, mCalllogFragment);
			}else{
				mTransaction.show(mCalllogFragment);
			}
			
		}else if(tabId == CONTACTS_TAB){
			setTabBackGround(false, false, true);
			if(mContactsFragment==null){
				mContactsFragment = new ContactsFragment();
				mTransaction.add(R.id.content, mContactsFragment);
			}else{
				mTransaction.show(mContactsFragment);
			}
		}
		mTransaction.commit();
	}
	
	private void setTabBackGround(boolean isFirst,boolean isSecond,boolean isThird){
		if(isFirst)
			dialBtn.setIcon(R.drawable.icon_tab_dialer_selected);
		else
			dialBtn.setIcon(R.drawable.icon_tab_dialer);
		
		if(isSecond)
			logBtn.setIcon(R.drawable.icon_tab_calllog_selected);
		else
			logBtn.setIcon(R.drawable.icon_tab_calllog);
		
		if(isThird)
			contactsBtn.setIcon(R.drawable.icon_tab_contacts_selected);
		else
			contactsBtn.setIcon(R.drawable.icon_tab_contacts);
	}
	
	private void hideFragment(){
		mTransaction = this.getFragmentManager().beginTransaction();
		if(mDialFragment!=null){
			this.getFragmentManager().beginTransaction().hide(mDialFragment).commit();
		}
		if(mCalllogFragment!=null){
			this.getFragmentManager().beginTransaction().hide(mCalllogFragment).commit();
		}
		if(mContactsFragment!=null){
			this.getFragmentManager().beginTransaction().hide(mContactsFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if(id==R.id.calllog){
			setSelectedTab(CALLLOG_TAB);
		}else if(id==R.id.dailpan){
			setSelectedTab(DIAL_TAB);
		}else if(id==R.id.contacts){
			setSelectedTab(CONTACTS_TAB);
		}
		
	}
}
