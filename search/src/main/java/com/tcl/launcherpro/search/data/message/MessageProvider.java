package com.tcl.launcherpro.search.data.message;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.android.sichard.common.permission.PermissionAssist;
import com.tcl.launcherpro.search.SearchSDK;
import com.tcl.launcherpro.search.common.MatchResult;
import com.tcl.launcherpro.search.common.TimeConstant;
import com.tcl.launcherpro.search.view.SearchUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <br>类描述:短信提供者
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-16</b>
 */
public class MessageProvider {

    public static final String SMS_URI_ALL = "content://sms/";

    private static MessageProvider sInstance;
    private Context mContext;
    private List<MessageInfo> mMessageInfoList = new ArrayList<>();
    private boolean mIsScanOver = false;
    public static MessageProvider getInstance() {
        if (sInstance == null) {
            sInstance = new MessageProvider();
        }
        return sInstance;
    }

    private MessageProvider() {
        this.mContext = SearchSDK.getContext();
    }

    public void scanMessage() {
        boolean permission = PermissionAssist.havePermission(mContext, Manifest.permission.READ_SMS) &&
                PermissionAssist.havePermission(mContext, Manifest.permission.READ_CONTACTS);
        if(!permission) {
            return;
        }
        List<MessageInfo> messageInfoList = new ArrayList<>();
        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[]{"_id", "address", "body", "date"};
            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, "date desc");
            if (cursor != null && cursor.moveToFirst()) {
                int index_Address = cursor.getColumnIndex("address");
                int index_Body = cursor.getColumnIndex("body");
                int index_Date = cursor.getColumnIndex("date");

                do {
                    String body = cursor.getString(index_Body);

                    String address = cursor.getString(index_Address);
                    String name = getPeopleNameFromPerson(address);

                    long date = cursor.getLong(index_Date);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(TimeConstant.DATE_TIME_FORMAT, Locale.getDefault());
                    Date d = new Date(date);
                    String strDate = dateFormat.format(d);

                    MessageInfo messageInfo = new MessageInfo(body, name, strDate, address);
                    messageInfoList.add(messageInfo);
                } while (cursor.moveToNext());

                if (!cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }

        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }

        synchronized (mMessageInfoList) {
            if (messageInfoList.size() > 0) {
                mMessageInfoList.clear();
                mMessageInfoList.addAll(messageInfoList);
            }
        }
        mIsScanOver = true;
    }

    /**
     * 通过address手机号关联Contacts联系人的显示名字
     * @param address 短信发送者的号码
     * @return
     */
    private String getPeopleNameFromPerson(String address) {

        String strPerson = null;
        Cursor cursor = null;
        try {
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

            Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤
            ContentResolver contentResolver = mContext.getContentResolver();
            if (contentResolver != null) {

                cursor = contentResolver.query(uri_Person, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int index_PeopleName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String strPeopleName = cursor.getString(index_PeopleName);
                    strPerson = strPeopleName;
                }
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // 如果未取到姓名，则返回原来的号码
        if (TextUtils.isEmpty(strPerson)) {
            return address;
        }
        return strPerson;
    }

    /**
     * 搜索短信
     * @param keys 搜索关键字
     * @return
     */
    public ArrayList<MessageInfo> searchMessage(String keys) {
        if (null == keys || keys.length() <= 0) {
            return null;
        }
        synchronized (mMessageInfoList) {
            ArrayList<MessageInfo> messageInfoList = new ArrayList<MessageInfo>();
            int sz = mMessageInfoList.size();
            for (int i = 0; i < sz; i++) {
                MessageInfo messageInfo = mMessageInfoList.get(i);
                if (null == messageInfo.mName) {
                    continue;
                }

                MatchResult item = SearchUtils.getInstance(mContext).match(keys, messageInfo.mContent);
                if (item != null && item.mMatchWords > 0) {
                    item.key = keys;
                    messageInfo.mMatchResult = item;
                    messageInfoList.add(messageInfo);
                }
            }
            return messageInfoList;
        }
    }

    public boolean scanMessageOver() {
        return mIsScanOver;
    }

    /**
     * 释放资源
     */
    public void release() {
        mContext = null;
        sInstance = null;
    }
}
