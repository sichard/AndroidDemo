package com.tcl.launcherpro.search.data.contact;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

import com.tcl.launcherpro.search.R;
import com.tcl.launcherpro.search.common.TaskManager;

/**
 *<br>类描述：联系人头像缓存
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2016-12-10</b>
 */
public class ContactPhotoCache {
	
	/**
	 * 联系人头像缓存（利用LRU原则）
	 * 
	 * @author sichard
	 *
	 */
	class PhotoLruCache extends LruCache<ContactInfo, Drawable> {
		
		public PhotoLruCache(int maxsize) {
			super(maxsize);
		}
		
		@Override
		protected void entryRemoved(boolean evicted, ContactInfo key,
				Drawable oldValue, Drawable newValue) {
			super.entryRemoved(evicted, key, oldValue, newValue);
		}
		
		@Override
		protected int sizeOf(ContactInfo key, Drawable value) {
			return 1;
		}
	}
	
	/**
	 * 联系人头像加载回调
	 * 
	 * @author masanbing
	 *
	 */
	public static interface ContactPhotoLoadListener {
		public void onLoadFinished(ContactInfo contact);
	}
	
	private Context mcContext;
	private PhotoLruCache mPhotoCache;
	
	private Drawable mDefaultPhoto;
	private ContactPhotoLoadListener mListener;
	
	public ContactPhotoCache(Context context, int cachesz) {
		mcContext = context;
		mPhotoCache = new PhotoLruCache(cachesz);
		
		mDefaultPhoto = mcContext.getResources().getDrawable(R.drawable.ic_default_avatar);
	}
	
	public void setContactPhotoLoadListener(ContactPhotoLoadListener listener) {
		mListener = listener;
	}
	
	public Drawable getPhoto(final ContactInfo contact) {
		Drawable ret = mPhotoCache.get(contact);
		if (null != ret) {
			return ret;
		}
		
		if (contact.hasPhoto()) {
			TaskManager.execWorkTask(new Runnable() {
				@Override
				public void run() {
					Drawable photo = contact.loadPhoto(mcContext);
					if (null != photo) {
						mPhotoCache.put(contact, photo);
						
						mListener.onLoadFinished(contact);
					}
				}
			});
		}
		
		return mDefaultPhoto;
	}
	
	public void clear() {
		mPhotoCache.evictAll();
	}
}
