package com.graduation.contacts.ui;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.graduation.contacts.R;
import com.graduation.contacts.bean.ContactNative;
import com.graduation.contacts.utils.ContactsOperation;
import com.graduation.contacts.utils.HeadViewUtils;
import com.graduation.contacts.utils.IntentActionAndExtra;
import com.graduation.contacts.utils.HeadViewUtils.onImageLoaderListener;

public class DetailActivity extends Activity {

	private static final int MSG_REFRESH_VIEW = 0x1;
	private static final int MSG_DELETE_SUCCESS = 0x2;

	ImageView headView;
	TextView nameView;
	LinearLayout telLayout;
	LinearLayout telContentLayout;
	LinearLayout emailLayout;
	LinearLayout emailContentLayout;
	ActionBar mActionBar;
	int contactId;
	ContactNative contacts;
	ExecutorService mExecutorPool;
	Bitmap mBitmap;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == MSG_REFRESH_VIEW) {
				refreshView();
			} else if (what == MSG_DELETE_SUCCESS) {
				Intent i = new Intent(IntentActionAndExtra.ACTION_REFRESH_DATA);
				DetailActivity.this.sendBroadcast(i);
				DetailActivity.this.finish();
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		mActionBar = this.getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setTitle(R.string.detail_contact);

		headView = (ImageView) findViewById(R.id.headView);
		nameView = (TextView) findViewById(R.id.name);

		telLayout = (LinearLayout) findViewById(R.id.tel_head_layout);
		telContentLayout = (LinearLayout) findViewById(R.id.tels);

		emailLayout = (LinearLayout) findViewById(R.id.email_head_layout);
		emailLayout = (LinearLayout) findViewById(R.id.emails);

		contactId = getIntent()
				.getIntExtra(IntentActionAndExtra.CONTACT_ID, -1);
		if (contactId == -1) {
			finish();
			return;
		}

		mExecutorPool = Executors.newFixedThreadPool(2);
		mExecutorPool.execute(new Runnable() {

			@Override
			public void run() {
				contacts = ContactsOperation.getInstance(getContentResolver())
						.getContactById(contactId);
				if (contacts != null) {
					mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
				}
			}
		});
	}

	private void refreshView() {
		if (contacts == null)
			return;

		String name = contacts.getContactName();
		nameView.setText(name);

		if (contacts.getPhotoId() != null) {
			HeadViewUtils headUtil = new HeadViewUtils(this);
			headUtil.downloadImage(contacts.getPhotoId(),
					new onImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							mBitmap = bitmap;
							DetailActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									headView.setImageBitmap(mBitmap);
								}

							});
						}
					});
		}

		List<String> mobileNumbers = contacts.getMobileNumbers();
		if (mobileNumbers != null) {
			telLayout.setVisibility(View.VISIBLE);
			for (String number : mobileNumbers) {
				View view = LayoutInflater.from(this).inflate(
						R.layout.detail_item, null);
				TextView content = (TextView) view.findViewById(R.id.content);
				TextView subcontent = (TextView) view
						.findViewById(R.id.subcontent);
				View operaTelLayout = view.findViewById(R.id.tel_opera);
				operaTelLayout.setVisibility(View.VISIBLE);
				ImageView callBtn = (ImageView) view.findViewById(R.id.call);
				callBtn.setTag(number);
				callBtn.setOnClickListener(BtnClickListener);
				ImageView smsBtn = (ImageView) view.findViewById(R.id.sms);
				smsBtn.setTag(number);
				smsBtn.setOnClickListener(BtnClickListener);
				content.setText(number);
				subcontent.setText(R.string.mobile);
				telLayout.addView(view);
			}
		}
		List<String> homeNumbers = contacts.getHomeNumbers();
		if (homeNumbers != null) {
			telLayout.setVisibility(View.VISIBLE);
			for (String number : homeNumbers) {
				View view = LayoutInflater.from(this).inflate(
						R.layout.detail_item, null);
				TextView content = (TextView) view.findViewById(R.id.content);
				TextView subcontent = (TextView) view
						.findViewById(R.id.subcontent);
				View operaTelLayout = view.findViewById(R.id.tel_opera);
				operaTelLayout.setVisibility(View.VISIBLE);
				ImageView callBtn = (ImageView) view.findViewById(R.id.call);
				callBtn.setTag(number);
				callBtn.setOnClickListener(BtnClickListener);
				ImageView smsBtn = (ImageView) view.findViewById(R.id.sms);
				smsBtn.setTag(number);
				smsBtn.setOnClickListener(BtnClickListener);
				content.setText(number);
				subcontent.setText(R.string.home);
				telLayout.addView(view);
			}
		}

		List<String> workNumbers = contacts.getWorkNumbers();
		if (workNumbers != null) {
			telLayout.setVisibility(View.VISIBLE);
			for (String number : workNumbers) {
				View view = LayoutInflater.from(this).inflate(
						R.layout.detail_item, null);
				TextView content = (TextView) view.findViewById(R.id.content);
				TextView subcontent = (TextView) view
						.findViewById(R.id.subcontent);
				View operaTelLayout = view.findViewById(R.id.tel_opera);
				operaTelLayout.setVisibility(View.VISIBLE);
				ImageView callBtn = (ImageView) view.findViewById(R.id.call);
				callBtn.setTag(number);
				callBtn.setOnClickListener(BtnClickListener);
				ImageView smsBtn = (ImageView) view.findViewById(R.id.sms);
				smsBtn.setTag(number);
				smsBtn.setOnClickListener(BtnClickListener);
				content.setText(number);
				subcontent.setText(R.string.work);
				telLayout.addView(view);
			}
		}

		List<String> otherNumbers = contacts.getWorkNumbers();
		if (otherNumbers != null) {
			telLayout.setVisibility(View.VISIBLE);
			for (String number : otherNumbers) {
				View view = LayoutInflater.from(this).inflate(
						R.layout.detail_item, null);
				TextView content = (TextView) view.findViewById(R.id.content);
				TextView subcontent = (TextView) view
						.findViewById(R.id.subcontent);
				View operaTelLayout = view.findViewById(R.id.tel_opera);
				operaTelLayout.setVisibility(View.VISIBLE);
				ImageView callBtn = (ImageView) view.findViewById(R.id.call);
				callBtn.setTag(number);
				callBtn.setOnClickListener(BtnClickListener);
				ImageView smsBtn = (ImageView) view.findViewById(R.id.sms);
				smsBtn.setTag(number);
				smsBtn.setOnClickListener(BtnClickListener);
				content.setText(number);
				subcontent.setText(R.string.mobile);
				telLayout.addView(view);
			}
		}

		final String emailContent = contacts.getContactEmail();
		if (emailContent != null && !emailContent.trim().equals("")) {
			emailLayout.setVisibility(View.VISIBLE);
			View view = LayoutInflater.from(this).inflate(R.layout.detail_item,
					null);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("plain/text");
					intent.putExtra(Intent.EXTRA_EMAIL,
							new String[] { emailContent });
					startActivity(intent);
				}
			});
			TextView content = (TextView) view.findViewById(R.id.content);
			TextView subcontent = (TextView) view.findViewById(R.id.subcontent);
			subcontent.setVisibility(View.GONE);
			View operaTelLayout = view.findViewById(R.id.tel_opera);
			operaTelLayout.setVisibility(View.GONE);
			content.setText(emailContent);
			emailLayout.addView(view);
		}
	}

	@Override
	protected void onStart() {
		
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (mExecutorPool != null) {
			mExecutorPool.shutdown();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.detail_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.detail_delete) {
			mExecutorPool.execute(new Runnable() {

				@Override
				public void run() {
					int result = ContactsOperation.getInstance(
							getContentResolver()).deleteContacts(contactId);
					if (result > 0) {
						mHandler.sendEmptyMessage(MSG_DELETE_SUCCESS);
					}

				}

			});
		} else if (id == R.id.detail_edit) {
			Intent i = new Intent(IntentActionAndExtra.ACTION_EDIT_CONTACT);
			i.setClass(this, AddEditActivity.class);
			i.putExtra(IntentActionAndExtra.CONTACT_ID, contactId);
			startActivity(i);
			this.finish();
		}else if(id==android.R.id.home){
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	OnClickListener BtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			String number = (String) view.getTag();
			if (view.getId() == R.id.call) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + number));
				startActivity(callIntent);
			} else if (view.getId() == R.id.sms) {
				Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
				smsIntent.setData(Uri.parse("smsto:" + number));
				startActivity(smsIntent);
			}

		}

	};

	
	
	

}
