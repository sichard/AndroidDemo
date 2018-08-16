package com.android.sichard.search.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sichard.search.R;
import com.android.sichard.search.data.ISearchItem;
import com.android.sichard.search.data.message.MessageInfo;

/**
 * <br>类描述:
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-16</b>
 */
public class SearchMessageItemView extends LinearLayout implements View.OnClickListener {
    private MessageInfo mMessageInfo;
    public ViewHolder mViewHolder;

    public SearchMessageItemView(Context context) {
        super(context);
    }

    public SearchMessageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchMessageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
        mViewHolder = new ViewHolder();
        mViewHolder.mContent = (TextView) findViewById(R.id.search_message_item_content);
        mViewHolder.mName = (TextView) findViewById(R.id.search_message_item_name);
        mViewHolder.mDate = (TextView) findViewById(R.id.search_message_item_date);
    }

    public void setMessageInfo(MessageInfo messageInfo) {
        mMessageInfo = messageInfo;
        //文字。关键字高亮
        SpannableString string = new SpannableString(messageInfo.mContent);
        if (messageInfo.mMatchResult.mMatchWords != 0) {
            string.setSpan(new ForegroundColorSpan(ISearchItem.HIGH_LIGHT_COLOR), messageInfo.mMatchResult.mStart, messageInfo.mMatchResult.mStart
                    + messageInfo.mMatchResult.mMatchWords, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (mViewHolder != null) {
            mViewHolder.mContent.setText(string);
        }
        if (mViewHolder != null) {
            mViewHolder.mName.setText(messageInfo.mName);
        }
        if (mViewHolder != null) {
            mViewHolder.mDate.setText(messageInfo.mDate);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + mMessageInfo.mPhoneNum));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        getContext().startActivity(intent);
//        com.android.mms/.ui.ComposeMessageActivity; //短信界面的activity
    }

    static class ViewHolder {
        public TextView mContent;
        public TextView mName;
        public TextView mDate;
    }
}
