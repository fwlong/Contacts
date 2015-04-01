package com.graduation.contacts.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.graduation.contacts.R;
import com.graduation.contacts.bean.ContactNative;
import com.graduation.contacts.utils.ContactsOperation;
import com.graduation.contacts.utils.HeadViewUtils;
import com.graduation.contacts.utils.HeadViewUtils.onImageLoaderListener;
import com.graduation.contacts.utils.IntentActionAndExtra;
import com.graduation.contacts.utils.PinyinComparator;
import com.graduation.contacts.view.SideBar;
import com.graduation.contacts.view.SideBar.OnTouchingLetterChangedListener;

public class ContactsFragment extends Fragment implements OnItemClickListener,MultiChoiceModeListener{
	
	private final static int MSG_REFRESH_VIEW = 0x1;
	
	ListView mContactsView;
	TextView indexDialog;
	SideBar mSideBar;
	ActionBar mActionBar;
	ExecutorService mExecutorPool;
	ContactsOperation mContactsOperation;
	MyContactAdapter mAdapter;
	List<ContactNative> checked = new ArrayList<ContactNative>(); 
	
	List<ContactNative> contacts = new ArrayList<ContactNative>();
	
	PinyinComparator comparator =  new PinyinComparator();
	
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if(what==MSG_REFRESH_VIEW){
				if(mAdapter!=null&&contacts!=null){
					mAdapter.updateDataList(contacts);
				}
			}
			super.handleMessage(msg);
		}
		
	};

	@Override
	public void onAttach(Activity activity) {
		mActionBar = activity.getActionBar();
		mExecutorPool = Executors.newFixedThreadPool(2);
		mContactsOperation = ContactsOperation.getInstance(activity.getContentResolver());
		mExecutorPool.execute(new Runnable(){

			@Override
			public void run() {
				contacts = mContactsOperation.getAllContacts();
				if(contacts!=null&&mAdapter!=null){
					Collections.sort(contacts, comparator);
					mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
				}
				
			}
			
		});
		super.onAttach(activity);
	}

	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(IntentActionAndExtra.ACTION_REFRESH_DATA);
		getActivity().registerReceiver(mReciver, filter);
		super.onCreate(savedInstanceState);
	}




	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReciver);
		super.onDestroy();
	}




	@Override
	public void onResume() {
		mActionBar.setTitle(getResources().getString(R.string.contacts));
		
		super.onResume();
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contact_fragment, null);
		mContactsView = (ListView)view.findViewById(R.id.contacts);
		indexDialog = (TextView)view.findViewById(R.id.index_dialog);
		mSideBar = (SideBar)view.findViewById(R.id.sidebar);
		mSideBar.setTextView(indexDialog);
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mContactsView.setSelection(position);
				}

			}
		});
		mContactsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mContactsView.setOnItemClickListener(this);
		mContactsView.setMultiChoiceModeListener(this);
		mAdapter = new MyContactAdapter();
		mContactsView.setAdapter(mAdapter);
		mAdapter.updateDataList(contacts);
		return view;
	}

	
	class MyContactAdapter extends BaseAdapter implements SectionIndexer{
		
		private List<ContactNative> mcontacts = new ArrayList<ContactNative>();
		
		public void updateDataList(List<ContactNative> contacts){
			this.mcontacts = contacts;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mcontacts==null?0:mcontacts.size();
		}

		@Override
		public Object getItem(int postion) {
			// TODO Auto-generated method stub
			return mcontacts==null?null:mcontacts.get(postion);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup root) {
			final ViewHolder holder;
			if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			convertView = inflater.inflate(R.layout.contact_item, null);
			holder = new ViewHolder();
			holder.pyClassView = (TextView)convertView.findViewById(R.id.pyclass);
			holder.headView = (ImageView)convertView.findViewById(R.id.headview);
			holder.nameView = (TextView)convertView.findViewById(R.id.contact_name);
			holder.numberView=(TextView)convertView.findViewById(R.id.contact_number);
			holder.infoLayout = (RelativeLayout)convertView.findViewById(R.id.info_layout);
			convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			ContactNative contact = mcontacts.get(position);
			int section = getSectionForPosition(position);

			if (position == getPositionForSection(section)) {
				holder.pyClassView.setVisibility(View.VISIBLE);
				holder.pyClassView.setText(contact.getSortLetters());
			} else {
				holder.pyClassView.setVisibility(View.GONE);
			}
			holder.id=contact.getId();
			if(contact.getPhotoId()!=null){
				HeadViewUtils headViewUtil = new HeadViewUtils(getActivity());
				headViewUtil.downloadImage(contact.getPhotoId(), new onImageLoaderListener() {
					
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						holder.headView.setImageBitmap(bitmap);
						
					}
				});
			}
			holder.nameView.setText(contact.getContactName());
			List<String> numbers = new ArrayList<String>();
			if(contact.getMobileNumbers()!=null&&contact.getMobileNumbers().size()>0){
				numbers.addAll(contact.getMobileNumbers());
			}
			if(contact.getHomeNumbers()!=null&&contact.getHomeNumbers().size()>0){
				numbers.addAll(contact.getHomeNumbers());
			}
			if(contact.getWorkNumbers()!=null&&contact.getWorkNumbers().size()>0){
				numbers.addAll(contact.getWorkNumbers());
			}
			if(contact.getOtherNumbers()!=null&&contact.getOtherNumbers().size()>0){
				numbers.addAll(contact.getOtherNumbers());
			}
			if(numbers.size()>0)
				holder.numberView.setText(numbers.get(0));
			
			if(contact.isSelected()){
				holder.infoLayout.setBackgroundColor(getResources().getColor(R.color.list_selected_color));
			}else{
				holder.infoLayout.setBackgroundColor(getResources().getColor(R.color.white_with_alpha));
			}
			
			return convertView;
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = mcontacts.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return mcontacts.get(position).getSortLetters().charAt(0);
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}

		class ViewHolder{
			TextView pyClassView;
			ImageView headView;
			TextView nameView;
			TextView numberView;
			RelativeLayout infoLayout;
			//List<String> numbers;
			int id;
		}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder holder = (ViewHolder) view.getTag();
		int contactid = holder.id;
		Intent i = new Intent();
		i.setClass(getActivity(), DetailActivity.class);
		i.putExtra(IntentActionAndExtra.CONTACT_ID, contactid);
		startActivity(i);
		
	}



	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.add){
			Intent i = new Intent(IntentActionAndExtra.ACTION_ADD_CONTACT);
			i.setClass(getActivity(), AddEditActivity.class);
			getActivity().startActivity(i);
		}else if(id==R.id.delete){
			mExecutorPool.execute(new Runnable(){
				@Override
				public void run() {
					ContactsOperation operation = ContactsOperation
							.getInstance(getActivity().getContentResolver());
					for(ContactNative contact:checked){
						int result = operation.deleteContacts(contact.getId());
						if(result>0){
							contacts.remove(contact);
						}
					}
					mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
				}
			});
		}
		mode.finish();
		return true;
	}



	@Override
	public boolean onCreateActionMode(ActionMode arg0, Menu menu) {
		getActivity().getMenuInflater().inflate(R.menu.contacts_menu, menu);
		if(checked!=null) checked.clear();
		return true;
	}



	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		for(ContactNative contact:contacts){
			contact.setSelected(false);
		}
		if(mAdapter!=null)mAdapter.notifyDataSetChanged();
		
	}



	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu menu) {
		if(checked.size()==0){
			menu.findItem(R.id.delete).setEnabled(false);
		}else{
			menu.findItem(R.id.delete).setEnabled(true);
		}
		return true;
	}



	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
			boolean isChecked) {
		contacts.get(position).setSelected(isChecked);
		checked.add(contacts.get(position));
		if(checked.size()==0){
			mode.getMenu().findItem(R.id.delete).setEnabled(false);
		}else{
			mode.getMenu().findItem(R.id.delete).setEnabled(true);
		}
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
	
	BroadcastReceiver mReciver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action==IntentActionAndExtra.ACTION_REFRESH_DATA){
				if(mExecutorPool!=null){
					mExecutorPool.execute(new Runnable(){

						@Override
						public void run() {
							contacts = mContactsOperation.getAllContacts();
							if(contacts!=null&&mAdapter!=null){
								Collections.sort(contacts, comparator);
								mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
							}
							
						}
						
					});
				}
			}
			
		}
		
	};
	
}
