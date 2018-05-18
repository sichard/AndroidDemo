package com.tcl.launcherpro.search.data.message;

import com.tcl.launcherpro.search.data.ISearchItem;
import com.tcl.launcherpro.search.data.contact.IContactIml;

/**
 * <br>类描述:展示更多的item
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-15</b>
 */
public class MoreMessageItem extends IMessageIml {
    /** 未展示短信的项 */
    public int mCount = 0;

    public MoreMessageItem() {
        this.mType = ISearchItem.MORE;
    }
}
