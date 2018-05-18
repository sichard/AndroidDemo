package com.android.sichard.search.data.contact;

import com.android.sichard.search.data.ISearchItem;

/**
 * <br>类描述:联系人标题
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-10</b>
 */
public class ContactTitle extends IContactIml {
    public int mState = ISearchItem.STATE_NONE;

    public ContactTitle() {
        this.mType = ISearchItem.TYPE_TITLE;
    }
}
