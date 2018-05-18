package com.tcl.launcherpro.search.data.message;

import com.tcl.launcherpro.search.common.MatchResult;
import com.tcl.launcherpro.search.data.ISearchItem;

/**
 * <br>类描述:短信信息类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-16</b>
 */
public class MessageInfo extends IMessageIml {

    public String mContent;
    public String mName;
    public String mDate;
    public String mPhoneNum;
    public MatchResult mMatchResult;

    public MessageInfo(String mContent, String mName, String mDate, String address) {
        this.mContent = mContent;
        this.mName = mName;
        this.mDate = mDate;
        this.mPhoneNum = address;
        mType = ISearchItem.TYPE_ITEM;
    }
}
