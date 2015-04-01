package com.graduation.contacts.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.graduation.contacts.R;
import com.graduation.contacts.utils.IntentActionAndExtra;

public class DialFragment extends Fragment implements OnClickListener,
		OnLongClickListener {

	EditText number;
	ImageView btnNum1;
	ImageView btnNum2;
	ImageView btnNum3;
	ImageView btnNum4;
	ImageView btnNum5;
	ImageView btnNum6;
	ImageView btnNum7;
	ImageView btnNum8;
	ImageView btnNum9;
	ImageView btnNum0;
	ImageView btnAsterisk;
	ImageView btnPound;
	ImageView btnAddPersion;
	ImageView btnDeleteNumber;
	ImageView btnCall;
	
	ActionBar mActionBar; 

	@Override
	public void onAttach(Activity activity) {
		int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		activity.getWindow().addFlags(flags);
		
		mActionBar = this.getActivity().getActionBar();
		mActionBar.setTitle(R.string.dial);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dial_fragment, null);
		number = (EditText) view.findViewById(R.id.numbers_shower);
		number.setOnClickListener(this);
		number.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable editable) {
				if(editable.toString().equals("")){
					number.setCursorVisible(false);
					if(btnDeleteNumber.getVisibility()!=View.INVISIBLE)
						btnDeleteNumber.setVisibility(View.INVISIBLE);
					if(btnAddPersion.getVisibility()!=View.INVISIBLE)
						btnAddPersion.setVisibility(View.INVISIBLE);
				}else{
					if(btnDeleteNumber.getVisibility()!=View.VISIBLE)
						btnDeleteNumber.setVisibility(View.VISIBLE);
					if(btnAddPersion.getVisibility()!=View.VISIBLE)
						btnAddPersion.setVisibility(View.VISIBLE);
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		number.setCursorVisible(false);
		btnNum1 = (ImageView) view.findViewById(R.id.num1);
		btnNum1.setOnClickListener(this);
		btnNum2 = (ImageView) view.findViewById(R.id.num2);
		btnNum2.setOnClickListener(this);
		btnNum3 = (ImageView) view.findViewById(R.id.num3);
		btnNum3.setOnClickListener(this);
		btnNum4 = (ImageView) view.findViewById(R.id.num4);
		btnNum4.setOnClickListener(this);
		btnNum5 = (ImageView) view.findViewById(R.id.num5);
		btnNum5.setOnClickListener(this);
		btnNum6 = (ImageView) view.findViewById(R.id.num6);
		btnNum6.setOnClickListener(this);
		btnNum7 = (ImageView) view.findViewById(R.id.num7);
		btnNum7.setOnClickListener(this);
		btnNum8 = (ImageView) view.findViewById(R.id.num8);
		btnNum8.setOnClickListener(this);
		btnNum9 = (ImageView) view.findViewById(R.id.num9);
		btnNum9.setOnClickListener(this);
		btnNum0 = (ImageView) view.findViewById(R.id.num0);
		btnNum0.setOnClickListener(this);
		btnAsterisk = (ImageView) view.findViewById(R.id.asterisk);
		btnAsterisk.setOnClickListener(this);
		btnPound = (ImageView) view.findViewById(R.id.pound);
		btnPound.setOnClickListener(this);
		btnAddPersion = (ImageView) view.findViewById(R.id.addpersion);
		btnAddPersion.setOnClickListener(this);
		btnDeleteNumber = (ImageView) view.findViewById(R.id.deletedial);
		btnDeleteNumber.setOnClickListener(this);
		btnDeleteNumber.setOnLongClickListener(this);
		btnCall = (ImageView) view.findViewById(R.id.call);
		btnCall.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onResume() {
		mActionBar.setTitle(R.string.dial);
		super.onResume();
	}

	@Override
	public void onClick(View view) {
		int index = number.getSelectionStart();
		int indexEnd = number.getSelectionEnd();
		int id = view.getId();
		switch (id) {
		case R.id.numbers_shower:
			number.setCursorVisible(true);
			break;
		case R.id.num1:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "1");
			break;
		case R.id.num2:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "2");
			break;
		case R.id.num3:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "3");
			break;
		case R.id.num4:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "4");
			break;
		case R.id.num5:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "5");
			break;
		case R.id.num6:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "6");
			break;
		case R.id.num7:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "7");
			break;
		case R.id.num8:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "8");
			break;
		case R.id.num9:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "9");
			break;
		case R.id.num0:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "0");
			break;
		case R.id.asterisk:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "*");
			break;
		case R.id.pound:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
			}
			number.getText().insert(index, "#");
			break;
		case R.id.addpersion:
			String phonenum = number.getText().toString();
			Intent i = new Intent();
			i.setClass(getActivity(),AddEditActivity.class);
			i.setAction(IntentActionAndExtra.ACTION_ADD_CONTACT);
			i.putExtra(IntentActionAndExtra.CONTACT_NUMBER, phonenum);
			getActivity().startActivity(i);
			break;
		case R.id.deletedial:
			if(indexEnd>index){
				number.getText().delete(index, indexEnd);
				return;
			}
				
			if (index == 0)
				return;
			number.getText().delete(index - 1, index);
			break;
		case R.id.call:
			if(number.getText()!=null&&!"".equals(number.getText().toString())){
				String num = number.getText().toString();
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+num));
				startActivity(intent);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onLongClick(View view) {
		int id = view.getId();
		if (id == R.id.deletedial) {
			number.setText("");
			number.setCursorVisible(false);
		}
		return false;
	}

}
