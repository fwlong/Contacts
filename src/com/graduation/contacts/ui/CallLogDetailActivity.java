package com.graduation.contacts.ui;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graduation.contacts.R;
import com.graduation.contacts.bean.CallLogDetail;
import com.graduation.contacts.bean.CallLogDetail.DetailLog;
import com.graduation.contacts.utils.FormatUtils;
import com.graduation.contacts.utils.IntentActionAndExtra;

public class CallLogDetailActivity extends Activity implements OnClickListener {

	private final int MSG_REFRESH = 0;

	ImageView headView;
	TextView nameView;

	LinearLayout callLogLayout;
	LinearLayout telLayout;
	RelativeLayout telContentLayout;
	TextView callNumber;
	ImageView imgCall;
	ImageView imgSms;
	Button mAddContactBtn;
	CallLogDetail mDetail = new CallLogDetail();
	String number;

	Handler refreshHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == MSG_REFRESH) {
				refreshView();
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_calllog);
		headView = (ImageView) findViewById(R.id.headview);
		nameView = (TextView) findViewById(R.id.name);
		callLogLayout = (LinearLayout) findViewById(R.id.call_logs);
		telLayout = (LinearLayout) findViewById(R.id.tel_head_layout);
		telLayout.setVisibility(View.GONE);
		telContentLayout = (RelativeLayout) findViewById(R.id.detail_item);
		telContentLayout.setVisibility(View.GONE);
		callNumber = (TextView) findViewById(R.id.content);
		imgCall = (ImageView) findViewById(R.id.call);
		imgCall.setOnClickListener(this);
		imgSms = (ImageView) findViewById(R.id.sms);
		imgSms.setOnClickListener(this);
		mAddContactBtn = (Button) findViewById(R.id.add_to_contact);
		mAddContactBtn.setVisibility(View.GONE);
		mAddContactBtn.setOnClickListener(this);
		number = getIntent()
				.getStringExtra(IntentActionAndExtra.CONTACT_NUMBER);
		if (number == null) {
			this.finish();
			return;
		}
		CallLogQueryHandler mHandler = new CallLogQueryHandler(
				getContentResolver());
		mHandler.startQuery(0, null, CallLog.Calls.CONTENT_URI, null,
				CallLog.Calls.NUMBER + "=" + number, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

	}

	private void refreshView() {
		if (mDetail == null) {
			this.finish();
			return;
		}
		if (mDetail.name == null || "".equals(mDetail.name.trim())
				|| mDetail.name.startsWith("+")) {

			nameView.setText(mDetail.number);
			telLayout.setVisibility(View.GONE);
			telContentLayout.setVisibility(View.GONE);
			mAddContactBtn.setVisibility(View.VISIBLE);
		} else {
			try {
				Integer.valueOf(mDetail.name);

				nameView.setText(mDetail.number);
				telLayout.setVisibility(View.GONE);
				telContentLayout.setVisibility(View.GONE);
				mAddContactBtn.setVisibility(View.VISIBLE);

			} catch (Exception e) {

				nameView.setText(mDetail.name);
				mAddContactBtn.setVisibility(View.GONE);
				telLayout.setVisibility(View.VISIBLE);
				telContentLayout.setVisibility(View.VISIBLE);
				callNumber.setText(mDetail.number);
			}
		}
		if (mDetail.logs.size() > 0) {
			String mDay = "";
			for (DetailLog log : mDetail.logs) {
				if (!mDay.equals(log.date)) {
					TextView v = new TextView(CallLogDetailActivity.this);
					v.setText(log.date);
					v.setTextSize(16);
					LayoutParams params = new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.leftMargin = 5;
					params.topMargin = 15;
					params.bottomMargin = 15;
					callLogLayout.addView(v, params);
					mDay = log.date;
				}

				View v = LayoutInflater.from(CallLogDetailActivity.this)
						.inflate(R.layout.log_item, null);
				TextView time = (TextView) v.findViewById(R.id.time);
				TextView type = (TextView) v.findViewById(R.id.type);
				TextView duration = (TextView) v.findViewById(R.id.duration);
				time.setText(log.time);
				String typestr = "";
				if (log.type == CallLog.Calls.INCOMING_TYPE) {
					typestr = this.getString(R.string.incall);
				} else if (log.type == CallLog.Calls.OUTGOING_TYPE) {
					typestr = this.getString(R.string.outcall);
				} else if (log.type == CallLog.Calls.MISSED_TYPE) {
					typestr = this.getString(R.string.misscall);
				}
				type.setText(typestr);
				duration.setText(log.duration);
				callLogLayout.addView(v);
			}
		}
	}

	class CallLogQueryHandler extends AsyncQueryHandler {

		public CallLogQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			super.onDeleteComplete(token, cookie, result);
			if (result > 0) {
				CallLogDetailActivity.this.finish();
			}
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor == null || cursor.getCount() == 0) {
				CallLogDetailActivity.this.finish();
				return;
			}
			cursor.moveToFirst();
			do {
				mDetail.name = cursor.getString(cursor
						.getColumnIndex(CallLog.Calls.CACHED_NAME));
				mDetail.number = cursor.getString(cursor
						.getColumnIndex(CallLog.Calls.NUMBER));
				DetailLog l = new DetailLog();
				l.type = cursor.getInt(cursor
						.getColumnIndex(CallLog.Calls.TYPE));
				l.duration = FormatUtils.formatDuration(
						CallLogDetailActivity.this, cursor.getLong(cursor
								.getColumnIndex(CallLog.Calls.DURATION)));
				String allDate = FormatUtils.formatDate(
						CallLogDetailActivity.this, cursor.getLong(cursor
								.getColumnIndex(CallLog.Calls.DATE)));
				String[] dt = allDate.split("&");
				l.date = dt[0];
				l.time = dt[1];
				mDetail.logs.add(l);
			} while (cursor.moveToNext());
			refreshHandler.sendEmptyMessage(MSG_REFRESH);
			super.onQueryComplete(token, cookie, cursor);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.add_to_contact) {

		} else if (id == R.id.call) {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + number));
			startActivity(callIntent);
		} else if (id == R.id.sms) {
			Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
			smsIntent.setData(Uri.parse("smsto:" + number));
			startActivity(smsIntent);
		}

	}

}
