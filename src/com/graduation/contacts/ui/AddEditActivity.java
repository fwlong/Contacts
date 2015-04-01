package com.graduation.contacts.ui;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.graduation.contacts.R;
import com.graduation.contacts.bean.ContactNative;
import com.graduation.contacts.utils.ContactsOperation;
import com.graduation.contacts.utils.IntentActionAndExtra;

public class AddEditActivity extends Activity implements OnClickListener{
	
	private static final int MSG_REFRESH_VIEW = 0x1;
	private static final int MSG_ADD_SUCCESS = 0x2;
	private static final int MSG_EDIT_SUCCESS = 0x3;
	
	EditText nameView;
	LinearLayout telContent;
	LinearLayout emailContent;
	EditText mEmailView;
	ImageView addBtn;
	ActionBar mActionBar;
	
	ContactNative contact;
	int contactId;
	String action;
	AlertDialog dialog;
	
	ContactsOperation contactOperation;
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == MSG_REFRESH_VIEW) {
				refreshView();
			} else if(what==MSG_ADD_SUCCESS){
				showMsg(R.string.add_suc);
				Intent i = new Intent(IntentActionAndExtra.ACTION_REFRESH_DATA);
				AddEditActivity.this.sendBroadcast(i);
				AddEditActivity.this.finish();
			}else if(what == MSG_EDIT_SUCCESS){
				showMsg(R.string.edit_suc);
				Intent i = new Intent(IntentActionAndExtra.ACTION_REFRESH_DATA);
				AddEditActivity.this.sendBroadcast(i);
				AddEditActivity.this.finish();
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_page);
		mActionBar = this.getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setTitle(R.string.edit_contact);
		
		nameView = (EditText)findViewById(R.id.name);
		mEmailView = (EditText)findViewById(R.id.email_content);
		addBtn = (ImageView)findViewById(R.id.add_phone);
		addBtn.setOnClickListener(this);
		telContent = (LinearLayout)findViewById(R.id.tels);
		
		contactOperation = ContactsOperation.getInstance(getContentResolver());
		action = getIntent().getAction();
		contactId = getIntent().getIntExtra(IntentActionAndExtra.CONTACT_ID, -1);
		if(action.equals(IntentActionAndExtra.ACTION_ADD_CONTACT)||
				contactId==-1){
			addPhoneView();
		}else if(action.equals(IntentActionAndExtra.ACTION_EDIT_CONTACT)){
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					contact = contactOperation.getContactById(contactId);
					if(contact!=null){
						mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
					}
				}
				
			}).start();
		}
		
		
	}
	
	private View addPhoneView(){
		final View view = LayoutInflater.from(this).inflate(R.layout.edit_item, null);
		ImageView deleteview = (ImageView)view.findViewById(R.id.delete_phone);
		deleteview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				telContent.removeView(view);
				
			}
		});
		telContent.addView(view);
		return view;
	}
	 
	

	private void refreshView(){
		if(contact!=null){
			nameView.setText(contact.getContactName());
			
			List<String> mobileNumbers = contact.getMobileNumbers();
			if (mobileNumbers != null) {
				for (String number : mobileNumbers) {
					View view = addPhoneView();
					EditText t = (EditText)view.findViewById(R.id.phnumber);
					t.setText(number);
				}
			}
			List<String> homeNumbers = contact.getHomeNumbers();
			if (homeNumbers != null) {
				for (String number : homeNumbers) {
					View view = addPhoneView();
					EditText t = (EditText)view.findViewById(R.id.phnumber);
					t.setText(number);
				}
			}

			List<String> workNumbers = contact.getWorkNumbers();
			if (workNumbers != null) {
				for (String number : workNumbers) {
					View v = addPhoneView();
					EditText t = (EditText)v.findViewById(R.id.phnumber);
					t.setText(number);
				}
			}

			List<String> otherNumbers = contact.getWorkNumbers();
			if (otherNumbers != null) {
				for (String number : otherNumbers) {
					View view = addPhoneView();
					EditText t = (EditText)view.findViewById(R.id.phnumber);
					t.setText(number);
				}
			}
			String emailContent = contact.getContactEmail();
			if (emailContent != null && !emailContent.trim().equals("")) {
				mEmailView.setText(emailContent);
			}
			
		}else{
			addPhoneView();
		}
	}

	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.save, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
		int id = item.getItemId();
		final ContactNative mContact = new ContactNative();
		mContact.setContactName(nameView.getText().toString());
		int count = telContent.getChildCount();
		if(count>0){
			for(int i=0;i<count;i++){
				String number=null;
				try{
					number = ((EditText)telContent.getChildAt(i).findViewById(R.id.phnumber)).getText().toString();
				}catch(Exception e){
					
				}
				if(number!=null&&!number.trim().equals("")){
					mContact.addOtherNumbers(number);
				}
			}
		}
		mContact.setContactEmail(mEmailView.getText().toString());
		if(id==R.id.save){
			
			if(mContact.getContactName()==null&&mContact.getOtherNumbers()==null&&mContact.getContactEmail()==null){
				return false;
			}
			
			if(action.equals(IntentActionAndExtra.ACTION_ADD_CONTACT)){
				new Thread(new Runnable(){

					@Override
					public void run() {
						int result = contactOperation.addContacts(mContact);
						if(result>0){
							mHandler.sendEmptyMessage(MSG_ADD_SUCCESS);
						}
						
					}
					
				}).start();
			}else if(action.equals(IntentActionAndExtra.ACTION_EDIT_CONTACT)){
				if(mContact.getContactName()==null&&mContact.getOtherNumbers()==null&&mContact.getContactEmail()==null){
					return false;
				}
					new Thread(new Runnable(){

						@Override
						public void run() {
							int result = contactOperation.deleteContacts(contactId);
							if(result>0){
								int addresult = contactOperation.addContacts(mContact);
								if(addresult>0){
									mHandler.sendEmptyMessage(MSG_EDIT_SUCCESS);
									
								}
							}
							
						}
						
					}).start();
				}
			
		}else if(id==android.R.id.home){
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if(id==R.id.add_phone){
			addPhoneView();
		}
		
	}
	
	Toast mToast;
	
	private void showMsg(int id){
		if(mToast!=null){
			mToast.cancel();
			mToast.cancel();
		}
		mToast = Toast.makeText(this, id, Toast.LENGTH_LONG);
		mToast.show();
	}

	
}
