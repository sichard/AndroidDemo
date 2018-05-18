package com.android.sichard.search.data.contact;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;

import com.android.sichard.search.R;
import com.android.sichard.search.common.MatchResult;
import com.android.sichard.search.data.ISearchItem;
import com.android.sichard.search.utils.BitmapUtil;

import java.io.InputStream;
import java.util.List;

/**
 *<br>类描述：联系人数据结构
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date   2016-8-11</b>
 */
public class ContactInfo extends IContactIml {
	
	public long mID;
	public long mPhotoID;
	public String mName;
	public String mLookupKey;
	public String mDefaultNum;
	public List<String> mPhoneNums;

	
	public MatchResult mMatchResult;
	
	public ContactInfo(long id, long photoid, String name, String lookupkey, String phonenum, List<String> phonenums) {
		mID = id;
		mPhotoID = photoid;
		mName = name;
		mLookupKey = lookupkey;
		mDefaultNum = phonenum;
		mPhoneNums = phonenums;
		mType = ISearchItem.TYPE_ITEM;
	}
	
	public boolean hasPhoto() {
		return mPhotoID > 0;
	}
	
	public BitmapDrawable loadPhoto(Context context) {
		BitmapDrawable ret = null;
		ContentResolver cr = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, mID);
		InputStream input = null;
		try {
			input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
			Bitmap photo = BitmapFactory.decodeStream(input);
			Bitmap mask = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_search_contact_cover)).getBitmap();
			// 将头像缩放到和遮罩一样大
			Bitmap scaledBitmap = BitmapUtil.createScaledBitmap(photo, mask.getWidth(), mask.getHeight());
			Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);
            //将遮罩层的图片放到画布中
            Canvas mCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //设置两张图片相交时的模式 
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mCanvas.drawBitmap(scaledBitmap, 0, 0, null);
            mCanvas.drawBitmap(mask, 0, 0, paint);
            paint.setXfermode(null);
			ret = new BitmapDrawable(context.getResources(), result);
			scaledBitmap.recycle();
			photo.recycle();
		} catch (Throwable e) {

		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (Exception e2) {
					
				}
			}
		}
		return ret;
	}
}
