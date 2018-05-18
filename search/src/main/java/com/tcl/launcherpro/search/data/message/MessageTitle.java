package com.tcl.launcherpro.search.data.message;

import com.tcl.launcherpro.search.data.ISearchItem;

/**
 * <br>类描述:短信标题
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-16</b>
 */
public class MessageTitle extends IMessageIml {
    public int mState = ISearchItem.STATE_NONE;

    public MessageTitle() {
        mType = ISearchItem.TYPE_TITLE;
    }
}
