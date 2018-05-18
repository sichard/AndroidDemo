package com.tcl.launcherpro.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcl.launcherpro.search.data.App.AppInfo;
import com.tcl.launcherpro.search.data.App.AppList;
import com.tcl.launcherpro.search.data.App.AppTitle;
import com.tcl.launcherpro.search.data.App.IApp;
import com.tcl.launcherpro.search.data.App.IAppIml;
import com.tcl.launcherpro.search.data.App.MoreAppItem;
import com.tcl.launcherpro.search.data.DivideInfo;
import com.tcl.launcherpro.search.data.ISearchItem;
import com.tcl.launcherpro.search.data.PreferencesManager;
import com.tcl.launcherpro.search.data.hotSite.HotSiteList;
import com.tcl.launcherpro.search.data.hotSite.HotSiteTitle;
import com.tcl.launcherpro.search.data.hotSite.IHotSiteIml;
import com.tcl.launcherpro.search.data.hotgame.HotGameList;
import com.tcl.launcherpro.search.data.hotgame.HotGameTitle;
import com.tcl.launcherpro.search.data.hotgame.IHotGameImpl;
import com.tcl.launcherpro.search.view.HotSiteView;
import com.tcl.launcherpro.search.view.SearchAppView;
import com.tcl.launcherpro.search.view.SearchWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:搜索列表的adapter
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-8</b>
 */
public class RecentAppAdapter extends BaseAdapter {
    public static final String SHOW_HOT_SITE = "showHotSite";
    public static final String SHOW_HOT_GAME = "showHotGame";
    private Context mContext;
    private List<Object> mList = new ArrayList<>();
    private List<AppInfo> mAppList = new ArrayList<>();
    private boolean isMoreMode = true;

    private LayoutInflater mInflater;
    private SearchWebView mSearchWebView;
    private HotSiteList mHotSiteList;
    private HotGameList mHotGameList;

    public RecentAppAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        isMoreMode = PreferencesManager.getInstance(context).getBoolean("isMoreMode", true);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = null;
        final Object item = mList.get(position);
        if (item instanceof IAppIml) {
            switch (((IAppIml) item).mType) {
                case ISearchItem.TYPE_TITLE:
                    itemView = mInflater.inflate(R.layout.search_title_and_more, parent, false);
                    itemView.setEnabled(false);
                    final TextView more = (TextView) itemView.findViewById(R.id.more);
                    final TextView less = (TextView) itemView.findViewById(R.id.less);

                    final AppTitle appTitle = (AppTitle) item;
                    if (appTitle.mState == ISearchItem.STATE_MORE) {
                        more.setVisibility(View.VISIBLE);
                    } else if (appTitle.mState == ISearchItem.STATE_LESS) {
                        less.setVisibility(View.VISIBLE);
                    }
                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moreSearchAppView();
                        }
                    });
                    less.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           lessSearchAppView(new ArrayList<AppInfo>(mAppList));
                        }
                    });
                    break;
                case ISearchItem.TYPE_ITEM:
                    SearchAppView searchAppView = (SearchAppView) mInflater.inflate(R.layout.search_app_view, parent, false);
                    final AppList appList = (AppList) item;
                    searchAppView.setChildList(appList.mAppList,true);
                    itemView = searchAppView;
                    break;
                case ISearchItem.MORE:
//                    itemView = mInflater.inflate(R.layout.search_more_result, parent, false);
//                    final TextView moreResult = (TextView) itemView.findViewById(R.id.search_more_result);
//                    moreResult.setText(mContext.getString(R.string.search_more_result, ((MoreAppItem) item).mCount));
//                    itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            moreSearchAppView();
//                        }
//                    });
                    break;
            }
        } else if (item instanceof IHotSiteIml) {
            switch (((IHotSiteIml) item).mType) {
                case ISearchItem.TYPE_TITLE:
                    itemView = mInflater.inflate(R.layout.search_title_and_more, parent, false);
                    itemView.setEnabled(false);
                    final TextView title = (TextView) itemView.findViewById(R.id.title);
                    title.setText(R.string.hot_site_title);
                    break;
                case ISearchItem.TYPE_ITEM:
                    HotSiteView hotAppView = (HotSiteView) mInflater.inflate(R.layout.hot_site_view, parent, false);
                    final HotSiteList appList = (HotSiteList) item;
                    hotAppView.setHotSiteList(appList.mHotSiteInfoList);
                    hotAppView.setSearchWebView(mSearchWebView);
                    itemView = hotAppView;
                    break;
            }
        }else if (item instanceof IHotGameImpl) {
            switch (((IHotGameImpl) item).mType) {
                case ISearchItem.TYPE_TITLE:
                    itemView = mInflater.inflate(R.layout.search_title_and_more, parent, false);
                    itemView.setEnabled(false);
                    final TextView title = (TextView) itemView.findViewById(R.id.title);
                    title.setText(R.string.hot_game_title);
                    break;
                case ISearchItem.TYPE_ITEM:
                    HotSiteView hotAppView = (HotSiteView) mInflater.inflate(R.layout.hot_site_view, parent, false);
                    final HotGameList appList = (HotGameList) item;
                    hotAppView.setGameList(appList.mHotGameInfoList);
                    hotAppView.setSearchWebView(mSearchWebView);
                    itemView = hotAppView;
                    break;
            }
        }
        else if (item instanceof DivideInfo) {
            itemView = mInflater.inflate(R.layout.search_divide, parent, false);
        }
        return itemView;
    }

    /**
     * 设置搜索App的结果列表
     *
     * @param list
     */
    public void setAppList(List<AppInfo> list) {
        mList.clear();
        if (list != null && list.size() > 0) {
            mAppList.clear();
            mAppList.addAll(list);
            if (isMoreMode) {
                lessSearchAppView(list);
            } else {
                moreSearchAppView();
            }
        }
        notifyDataSetChanged();
    }

    private void lessSearchAppView(List<AppInfo> list) {
        mList.clear();
        if (list != null && list.size() > 0) {
            // 添加title
            final AppTitle appTitle = new AppTitle();
            appTitle.mType = IApp.TYPE_TITLE;
            mList.add(0, appTitle);

            if (list.size() > 5) {
                MoreAppItem moreAppItem = new MoreAppItem();
                moreAppItem.mCount = list.size() - 5;

                // 添加item
                final List<AppInfo> appInfos = list.subList(0, 5);
                AppList appList = new AppList();
                appList.mAppList = new ArrayList<>(appInfos);
                appList.mType = IApp.TYPE_ITEM;
                appList.mIsMore = true;
                mList.add(appList);

                // 添加more
                // mList.add(moreAppItem);
                appTitle.mState = ISearchItem.STATE_MORE;
            } else {
                AppList appList = new AppList();
                appList.mAppList = list;
                mList.add(appList);
                appTitle.mState = ISearchItem.STATE_NONE;
            }
        }
        // 添加分隔符
        mList.add(new DivideInfo());

        notifyDataSetChanged();
        // 保持状态
        PreferencesManager.getInstance(mContext).putBoolean("isMoreMode", true);
        isMoreMode = true;

        if (mHotGameList != null) {
            addHotGameList(mHotGameList);
        }

        if (mHotSiteList != null) {
            addHotSiteList(mHotSiteList);
        }
    }

    private void moreSearchAppView() {
        mList.clear();

        // 添加title
        final AppTitle appTitle = new AppTitle();
        appTitle.mType = IApp.TYPE_TITLE;
        mList.add(0, appTitle);

        // 添加item
        AppList appList = new AppList();
        appList.mAppList = mAppList;
        appList.mIsMore = false;
        mList.add(1, appList);

//        mList.add(new LessAppItem());
        appTitle.mState = ISearchItem.STATE_LESS;
        // 添加分隔符
        mList.add(new DivideInfo());

        notifyDataSetChanged();
        // 保持状态
        PreferencesManager.getInstance(mContext).putBoolean("isMoreMode", false);
        isMoreMode = false;

        if (mHotGameList != null) {
            addHotGameList(mHotGameList);
        }

        if (mHotSiteList != null) {
            addHotSiteList(mHotSiteList);
        }
    }

    /**
     * 设置热点网站列表
     * @param hotSiteList
     */
    public void setHotSiteList(HotSiteList hotSiteList) {
        mHotSiteList = hotSiteList;
        // 如果倒数第二个列表不是热点网站则加入
        boolean containHotSite = false;
        for(Object item :mList) {
            if(item instanceof HotSiteList){
                containHotSite = true;
                break;
            }
        }
        if (!containHotSite) {
            addHotSiteList(mHotSiteList);
        }
    }

    /**
     * 设置热点游戏列表
     * @param hotGameList
     */
    public void setGameSiteList(HotGameList hotGameList) {
        if(PreferencesManager.getInstance(mContext).getBoolean(SHOW_HOT_GAME,true) == false) {
            return;
        }
        mHotGameList = hotGameList;
        boolean containHotGame = false;
        for(Object item : mList) {
            if(item instanceof HotGameList){
                containHotGame = true;
                break;
            }
        }
        if (!containHotGame) {
            addHotGameList(mHotGameList);
        }
    }

    private void addHotSiteList(HotSiteList hotSiteList) {
        // 添加title
        if(PreferencesManager.getInstance(mContext).getBoolean(SHOW_HOT_SITE,true) == false) {
            return;
        }
        final HotSiteTitle hotSiteTitle = new HotSiteTitle();
        hotSiteTitle.mType = ISearchItem.TYPE_TITLE;
        mList.add(hotSiteTitle);

        // 添加item
        mList.add(hotSiteList);

        // 添加分隔符
        mList.add(new DivideInfo());

        notifyDataSetChanged();
    }

    private void addHotGameList(HotGameList hotGameList) {
        // 添加title
        final HotGameTitle hotGameTitle = new HotGameTitle();
        hotGameTitle.mType = ISearchItem.TYPE_TITLE;
        mList.add(hotGameTitle);

        // 添加item
        mList.add(hotGameList);

        // 添加分隔符
        mList.add(new DivideInfo());

        notifyDataSetChanged();
    }

    public void setSearchWebView(SearchWebView searchWebView) {
        this.mSearchWebView = searchWebView;
    }
}
