package com.android.sichard.search.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.sichard.search.R;
import com.android.sichard.search.data.ISearchItem;
import com.android.sichard.search.data.contact.ContactInfo;

/**
 * <br>类描述:搜索联系人item
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-14</b>
 */
public class SearchContactItemView extends RelativeLayout implements View.OnClickListener{
    private ViewHolder mViewHolder;
    public SearchContactItemView(Context context) {
        super(context);
    }

    public SearchContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchContactItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
        mViewHolder = new ViewHolder();
        mViewHolder.mHeadIcon = (ImageView) findViewById(R.id.search_contact_item_head);
        mViewHolder.mName = (TextView) findViewById(R.id.search_contact_item_name);
        findViewById(R.id.search_contact_item_call).setOnClickListener(new OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                final Object tag = getTag();
                if (tag != null && tag instanceof ContactInfo) {
                    ContactInfo contactInfo = (ContactInfo) tag;
                    if (null != contactInfo.mDefaultNum) {
                        if (contactInfo.mPhoneNums != null && contactInfo.mPhoneNums.size() > 1) {
                            openContacts(contactInfo);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactInfo.mDefaultNum));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            try {
                                getContext().startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Uri uri = ContactsContract.Contacts.getLookupUri(contactInfo.mID, contactInfo.mLookupKey);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        try {
                            getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        findViewById(R.id.search_contact_item_message).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Object tag = getTag();
                if (tag != null && tag instanceof ContactInfo) {
                    ContactInfo contactInfo = (ContactInfo) tag;
                    if (null != contactInfo.mDefaultNum) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto://" + contactInfo.mDefaultNum));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        try {
                            getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Uri uri = ContactsContract.Contacts.getLookupUri(contactInfo.mID, contactInfo.mLookupKey);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        try {
                            getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    public void setHeadIcon(Drawable photo) {
        if (mViewHolder != null) {
            mViewHolder.mHeadIcon.setImageDrawable(photo);
        }
    }

    public void setName(ContactInfo contactInfo) {
        //文字。关键字高亮
        SpannableString string = new SpannableString(contactInfo.mName);
        if (contactInfo.mMatchResult.mMatchWords != 0) {
            string.setSpan(new ForegroundColorSpan(ISearchItem.HIGH_LIGHT_COLOR), contactInfo.mMatchResult.mStart, contactInfo.mMatchResult.mStart
                    + contactInfo.mMatchResult.mMatchWords, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mViewHolder.mName.setText(string);
    }

    @Override
    public void onClick(View v) {
        final Object tag = getTag();
        if (tag != null && tag instanceof ContactInfo) {
            openContacts((ContactInfo) tag);
        }
    }

    /**
     * 打开联系人
     * @param tag
     */
    private void openContacts(ContactInfo tag) {
        ContactInfo contactInfo = tag;
        Uri uri = ContactsContract.Contacts.getLookupUri(contactInfo.mID, contactInfo.mLookupKey);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getContext().startActivity(intent);
    }

    private static class ViewHolder {
        public ImageView mHeadIcon;
        public TextView mName;
    }
}
