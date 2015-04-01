package com.graduation.contacts.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.graduation.contacts.R;
import com.graduation.contacts.bean.CallLogNative;
import com.graduation.contacts.ui.CalllogFragment.CallLogAdapter.ViewHolder;
import com.graduation.contacts.utils.FormatUtils;
import com.graduation.contacts.utils.IntentActionAndExtra;




public class CalllogFragment extends Fragment implements OnItemClickListener,
		MultiChoiceModeListener {

	ListView mCallList;
	List<CallLogNative> mCallLogs = new ArrayList<CallLogNative>();
	List<CallLogNative> mCheckedCallLogs = new ArrayList<CallLogNative>();
	CallLogAdapter mAdapter;
	ActionBar mActionBar;
	CallLogQueryHandler mHandler;
	
	private final String[] projection = new String[]{CallLog.Calls._ID,CallLog.Calls.CACHED_NAME,CallLog.Calls.NUMBER,
			CallLog.Calls.TYPE,CallLog.Calls.DATE};
	

	@Override
	public void onAttach(Activity activity) {
		mActionBar = this.getActivity().getActionBar();
		mActionBar.setTitle(R.string.calllog);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calllog_fragment,null);
		mCallList = (ListView)view.findViewById(R.id.calllogs);
		mAdapter = new CallLogAdapter();
		mAdapter.setLists(mCallLogs);
		mCallList.setAdapter(mAdapter);
		mCallList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mCallList.setOnItemClickListener(this);
		mCallList.setMultiChoiceModeListener(this);
		mHandler = new CallLogQueryHandler(getActivity().getContentResolver());
		mHandler.startQuery(0, null, CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		return view;
	}
	
	

	@Override
	public void onResume() {
		mActionBar.setTitle(R.string.calllog);
		super.onResume();
	}



	class CallLogAdapter extends BaseAdapter {

		private List<CallLogNative> logs = new ArrayList<CallLogNative>();

		public void setLists(List<CallLogNative> logs) {
			this.logs = logs;
		}

		@Override
		public int getCount() {
			return logs.size();
		}

		@Override
		public Object getItem(int position) {
			return logs.size() == 0 ? null : logs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return logs.size() == 0 ? 0 : position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup root) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.calllog_item, null);
				holder = new ViewHolder();
				holder.tagView = (ImageView) convertView.findViewById(R.id.tag);
				holder.mNameView = (TextView) convertView
						.findViewById(R.id.contact_name);
				holder.mNumberView = (TextView) convertView
						.findViewById(R.id.contact_number);
				holder.mTime = (TextView) convertView
						.findViewById(R.id.call_time);
				holder.ditailView = (ImageView)convertView.findViewById(R.id.toditail);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final CallLogNative log = logs.get(position);
			if (log.getCallType() == CallLog.Calls.INCOMING_TYPE) {
				holder.tagView.setImageResource(R.drawable.history_call_in);
				holder.tagView.setVisibility(View.VISIBLE);
			} else if (log.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
				holder.tagView.setImageResource(R.drawable.history_call_out);
				holder.tagView.setVisibility(View.VISIBLE);
			} else {
				holder.tagView.setVisibility(View.INVISIBLE);
			}
			if (log.getContactName() == null || "".equals(log.getContactName())||log.getContactName().startsWith("+86")) {
				holder.mNameView.setText(log.getPhoneNumber());
				holder.mNumberView.setText(getResources().getString(
						R.string.unsave));
			} else {
				holder.mNameView.setText(log.getContactName());
				holder.mNumberView.setText(log.getPhoneNumber());
			}
			holder.number = log.getPhoneNumber();
			holder.mTime.setText(log.getTime());
			holder.ditailView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent();
					i.setClass(getActivity(), CallLogDetailActivity.class);
					i.putExtra(IntentActionAndExtra.CONTACT_NUMBER, log.getPhoneNumber());
					startActivity(i);
				}
				
			});
			holder.id = log.getId();
			if(log.isSelected()){
				convertView.setBackgroundColor(getResources().getColor(R.color.list_selected_color));
			}else{
				convertView.setBackgroundColor(getResources().getColor(R.color.white_with_alpha));
			}
			
			return convertView;

		}

		class ViewHolder {
			ImageView tagView;
			TextView mNumberView;
			TextView mNameView;
			TextView mTime;
			ImageView ditailView;
			String number;
			int id;
		}

	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.delete){
			if(mCheckedCallLogs.size()==0) return false;
			int length = mCheckedCallLogs.size();
			String[] IDs = new String[length];
			for(int i=0;i<length;i++){
				IDs[i]=String.valueOf(mCheckedCallLogs.get(i).getId());
			}
			String selection = CallLog.Calls._ID+"=?";
			for(String calllog:IDs){
				mHandler.startDelete(0, null, CallLog.Calls.CONTENT_URI, selection, new String[]{calllog});
			}
		}
		mode.finish();
		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode arg0, Menu menu) {
		getActivity().getMenuInflater().inflate(R.menu.calllog_menu, menu);
		if(mCheckedCallLogs!=null) mCheckedCallLogs.clear();
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		for(CallLogNative log:mCallLogs){
			log.setSelected(false);
		}
		if(mAdapter!=null)mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu menu) {
		
		if(mCheckedCallLogs.size()==0){
			menu.findItem(R.id.delete).setEnabled(false);
		}else{
			menu.findItem(R.id.delete).setEnabled(true);
		}
		return true;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
			boolean isChecked) {
		mCallLogs.get(position).setSelected(isChecked);
		mCheckedCallLogs.add(mCallLogs.get(position));
		if(mCheckedCallLogs.size()==0){
			mode.getMenu().findItem(R.id.delete).setEnabled(false);
		}else{
			mode.getMenu().findItem(R.id.delete).setEnabled(true);
		}
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> adapterview, View convertView, int postion, long id) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		String number = holder.number;
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:"+number));
		startActivity(callIntent);
		
	}
	
	class CallLogQueryHandler extends AsyncQueryHandler{

		public CallLogQueryHandler(ContentResolver cr) {
			super(cr);
		}


		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			if(result>0){
				for(CallLogNative log:mCheckedCallLogs){
					mCallLogs.remove(log);
				}
				if(mAdapter!=null)mAdapter.notifyDataSetChanged();
			}
			super.onDeleteComplete(token, cookie, result);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if(cursor==null||cursor.getCount()==0) return;
			cursor.moveToFirst();
			do{
				CallLogNative mLog = new CallLogNative();
				mLog.setId(cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID)));
				mLog.setContactName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
				mLog.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
				mLog.setCallType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
				mLog.setTime(FormatUtils.getDateLong2String(getActivity(),cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))));
				mCallLogs.add(mLog);
			}while(cursor.moveToNext());
			if(mAdapter!=null) mAdapter.notifyDataSetChanged();
			super.onQueryComplete(token, cookie, cursor);
		}
		
		
		
	}

}
