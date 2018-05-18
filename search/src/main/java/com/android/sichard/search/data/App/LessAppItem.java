package com.android.sichard.search.data.App;

import com.android.sichard.search.data.ISearchItem;

/**
 * <br>类描述:展示更多的item
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-15</b>
 */
public class LessAppItem extends IAppIml {
    /** 未展示信息的数量 */
    public int mCount = 0;
    public LessAppItem() {
        this.mType = ISearchItem.MORE;
    }
}
