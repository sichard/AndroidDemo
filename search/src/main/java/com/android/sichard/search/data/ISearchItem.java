package com.android.sichard.search.data;

/**
 * <br>类描述:搜索项需要继承的接口
 * <br>详细描述:此处的项指的是搜索的类型比如：应用、联系人、短信等等
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-14</b>
 */
public interface ISearchItem {
    /** 搜索项高亮的颜色值 */
    int HIGH_LIGHT_COLOR = 0xFF03a9f4;
    /** 搜索无效项 */
    int TYPE_NONE = -1;
    /** list列表的item类型为title */
    int TYPE_TITLE = 1;
    /** list列表的item类型为item */
    int TYPE_ITEM = 2;
    /** list列表的item类型为"更多" */
    int MORE = 3;
    /** list列表的item类型为"收纳" */
    int LESS = 4;

    public final int STATE_NONE = 1;
    public final int STATE_MORE = 2;
    public final int STATE_LESS = 3;
}
