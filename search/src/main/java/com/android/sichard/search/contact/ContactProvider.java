package com.android.sichard.search.contact;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.android.sichard.common.permission.PermissionAssist;
import com.android.sichard.search.R;
import com.android.sichard.search.SearchSDK;
import com.android.sichard.search.common.MatchResult;
import com.android.sichard.search.common.TaskManager;
import com.android.sichard.search.data.contact.ContactInfo;
import com.android.sichard.search.utils.ToastUtil;
import com.android.sichard.search.view.SearchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *<br>类描述：联系人数据提供者
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date   2016-8-11</b>
 */
public class ContactProvider {
	
	private Context mContext;
	private ArrayList<ContactInfo> mContactInfos = new ArrayList<ContactInfo>();

	private boolean mScanContactOver = false;
	
	/*
	 * 为快速启动联系人搜索，缓存联系人数据为全局变量，因此用到单例模式
	 */
	private static ContactProvider sInstance;
	public static synchronized ContactProvider getInstance() {
		if (null == sInstance) {
			sInstance = new ContactProvider();
		}
		return sInstance;
	}

	private ContactProvider() {
		mContext = SearchSDK.getContext();
		
//		mHasContactModule = isIntentAvalid(new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI));
	}
	
	public void scanContact() {
		if (mContext == null) {
			mContext = SearchSDK.getContext();
		}
		if (mContext == null) {
			return;
		}
		if (!PermissionAssist.havePermission(mContext, Manifest.permission.READ_CONTACTS)) {
			return;
		}
		ArrayList<ContactInfo> phones = scanPhoneContact();
		if (phones == null) {
			return;
		}
		// 20160923 gaojt fix anr bug begin
		synchronized (mContactInfos) {
			mContactInfos.clear();
			mContactInfos.addAll(phones);
		}
		// 20160923 gaojt fix anr bug end
	}

	public boolean scanContactOver() {
		return mScanContactOver;
	}

	public void updateContact() {
		// 简单处理，重新扫描
		// 20160923 gaojt fix anr bug begin
		synchronized (mContactInfos) {
			mContactInfos.clear();
		}
		// 20160923 gaojt fix anr bug end
		scanContact();
	}

	private ArrayList<ContactInfo> scanPhoneContact() {
		final String columns[] = {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.PHOTO_ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.LOOKUP_KEY,
				ContactsContract.Contacts.HAS_PHONE_NUMBER
		};
		ArrayList<ContactInfo> contacts = new ArrayList<ContactInfo>();
		Cursor cur = null;
		// 如果mContext为空，表示activity重启，mContext被回收，直接返回即可。
		if (mContext == null) {
			return null;
		}
		try {
			cur = mContext.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					columns,
					null,
					null,
					ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		} catch (Exception e) {
			TaskManager.execTaskOnUIThread(new Runnable() {
				@Override
				public void run() {
					ToastUtil.show(mContext, R.string.search_permission_hint, Toast.LENGTH_SHORT);
				}
			});
		}
		if (cur != null) {
            if (cur.moveToFirst()) {  
                do {  
                    long id = cur.getLong(0);
                    long photoid = cur.getLong(1);
                    String name = cur.getString(2);
                    String lookupkey = cur.getString(3);
                    int phonenum = cur.getInt(4);
                    String phonenumstr = null;
					List<String> phoneNums = new ArrayList<>();
                    if (phonenum > 0) {
                    	ArrayList<String> phones = getContactPhoneNum(id);
                    	if (phones != null && phones.size() > 0) {
							phoneNums.addAll(phones);
                    		phonenumstr = phones.get(0);
                    	}
                    }
                    
                    ContactInfo contact = new ContactInfo(id, photoid, name, lookupkey, phonenumstr, phoneNums);
                    contacts.add(contact);
                } while(cur.moveToNext());
				// 已经获取联系人
				mScanContactOver = true;
            }
            cur.close();
		}
		return contacts;
	}
	
	private ArrayList<String> getContactPhoneNum(long id) {
		// 如果mContext为空，表示activity重启，mContext被回收，直接返回即可。
		if (mContext == null) {
			return null;
		}
		final String columns[] = {
				ContactsContract.CommonDataKinds.Phone.NUMBER
		};

		ArrayList<String> phones = new ArrayList<String>();

		try {
			Cursor cur = mContext.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					columns,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
					null,
					null);

			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						String phone = cur.getString(0);
						if (null != phone && !phone.equals("")) {
							phones.add(phone);
						}
					} while(cur.moveToNext());
				}
				cur.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


        return phones;
	}
	
	public ArrayList<ContactInfo> getContacts(String keys) {
		if (null == keys || keys.length() <= 0) {
			return null;
		}
		// 20160923 gaojt fix anr bug begin
		synchronized (mContactInfos) {
			ArrayList<ContactInfo> contacts = new ArrayList<ContactInfo>();
			int sz = mContactInfos.size();
			for (int i = 0; i < sz; i++) {
				ContactInfo contact = mContactInfos.get(i);
				if (null == contact.mName) {
					continue;
				}

				MatchResult item = SearchUtils.getInstance(mContext).match(keys, contact.mName);
				if (item != null && item.mMatchWords > 0) {
					item.key = keys;
					contact.mMatchResult = item;
					contacts.add(contact);
				}
			}
			return contacts;
		}
		// 20160923 gaojt fix anr bug end
	}
	
	private boolean isIntentAvalid(Intent intent) {
		return mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
	}

	/**
	 * 重置扫描标志位
	 */
	public void resetScanState() {
		mScanContactOver = false;
	}

	public void release() {
		mContext = null;
		sInstance = null;
		mContactInfos.clear();
	}
}
