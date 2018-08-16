package com.android.sichard.search.data.contact;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;

/**
 *<br>类描述：联系人监听监控
 * 注意：
 * Android只能监听整个数据库的改变，当你监听联系人数据库时，通话记录改变，也就是打一下电话什么的，也会触发前面的监听器！
 * 具体原因，主要是联系人数据库中有“联系次数"、”最后通话时间“等字段，所以每次打电话都会更新联系人数据库。
 * 这里解决办法：主要思路是同时监听联系人数据库和通话记录数据库，如果两个监听器都触发了，则认为这仅仅是打了一次电话，如果只有联系人数据库的监听器被触发了，则认为是真正的联系人被更改了，从而做出相应的处理
 *<br><b>Author sichard</b>
 *<br><b>Date   2016-8-11</b>
 */
public class ContactObserver {
	
	// Handle消息ID
	private final static int MESSAGE_CONTACT_CHANGED = 10000;
	// Handle延时时间
    private final static int ELAPSE_TIME = 10000;
	private Handler mHandler;
	
	// 联系人数据库监听
	private ContentObserver mContactObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            //去掉多余的消息
            mHandler.removeMessages(MESSAGE_CONTACT_CHANGED);

            //延时ELAPSE_TIME(10秒）发送同步信号“0”
            mHandler.sendEmptyMessageDelayed(MESSAGE_CONTACT_CHANGED, ELAPSE_TIME);
        }
	};
	// 通话记录数据库监听
	private ContentObserver mCallLogObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
        	//如果延时期间发现通话记录数据库也改变了，即判断为联系人未被改变
            mHandler.removeMessages(MESSAGE_CONTACT_CHANGED);
		}
	};
	
	private Context mContext;
	private boolean mIsListen = false;
	
	public ContactObserver(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
	}
	
	public synchronized void startListen() {
		if (!mIsListen) {
			// 注册监听通话记录数据库
			mContext.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mCallLogObserver);
			// 注册监听联系人数据库
			mContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mContactObserver);
			
			mIsListen = true;
		}
	}
	
	public synchronized void stopListen() {
		if (mIsListen) {
			// 注册监听通话记录数据库
			mContext.getContentResolver().unregisterContentObserver(mCallLogObserver);
			// 注册监听联系人数据库
			mContext.getContentResolver().unregisterContentObserver(mContactObserver);
			
			mIsListen = false;
		}
	}
}
