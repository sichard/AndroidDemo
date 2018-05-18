package com.tcl.launcherpro.search.data.message;

import com.tcl.launcherpro.search.data.ISearchItem;

/**
 * <br>类描述:展示更多的item
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-15</b>
 */
public class LessMessageItem extends IMessageIml {
    /** 未展示短信的项 */
    public int mCount = 0;

    public LessMessageItem() {
        this.mType = ISearchItem.LESS;
    }
}
