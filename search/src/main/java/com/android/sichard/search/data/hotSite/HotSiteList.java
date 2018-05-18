package com.android.sichard.search.data.hotSite;

import android.content.Context;

import com.android.sichard.search.R;
import com.android.sichard.search.data.ISearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:热点网站列表封装类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/7</b>
 */

public class HotSiteList extends IHotSiteIml {
    // 热点网站初始链接
    private final String FACE_BOOK = "http://api.airfind.com/link/v1?id=58902687a103e15a6390b5e5&clientId=50065";
    private final String AMAZON = "https://www.amazon.com/";
    private final String YOU_TUBE = "https://m.youtube.com/";
    private final String EBAY = "http://m.ebay.com/";
    private final String GMAIL = "https://www.google.com/gmail/";
    /** 要展示热点网站的列表 */
    public List<HotSiteInfo> mHotSiteInfoList = new ArrayList<>();

    public HotSiteList(Context context) {
        mType = ISearchItem.TYPE_ITEM;
        createHotSiteList(context);
    }

    /**
     * 获取热点网站列表数据
     * @return
     */
    private List<HotSiteInfo> createHotSiteList(Context context) {

        HotSiteInfo hotSiteFacebook = new HotSiteInfo(context);
        hotSiteFacebook.setIcon(R.drawable.hot_site_facebook);
        hotSiteFacebook.setTitle(R.string.hot_site_facebook);
        hotSiteFacebook.mUrl = FACE_BOOK;
        mHotSiteInfoList.add(hotSiteFacebook);

        HotSiteInfo hotSiteAmazon = new HotSiteInfo(context);
        hotSiteAmazon.setIcon(R.drawable.hot_site_amazon);
        hotSiteAmazon.setTitle(R.string.hot_site_amazon);
        hotSiteAmazon.mUrl = AMAZON;
        mHotSiteInfoList.add(hotSiteAmazon);

        HotSiteInfo hotSiteYouTube = new HotSiteInfo(context);
        hotSiteYouTube.setIcon(R.drawable.hot_site_youtube);
        hotSiteYouTube.setTitle(R.string.hot_site_youtube);
        hotSiteYouTube.mUrl = YOU_TUBE;
        mHotSiteInfoList.add(hotSiteYouTube);

        HotSiteInfo hotSiteEbay = new HotSiteInfo(context);
        hotSiteEbay.setIcon(R.drawable.hot_site_ebay);
        hotSiteEbay.setTitle(R.string.hot_site_ebay);
        hotSiteEbay.mUrl = EBAY;
        mHotSiteInfoList.add(hotSiteEbay);

        HotSiteInfo hotSiteGmail = new HotSiteInfo(context);
        hotSiteGmail.setIcon(R.drawable.hot_site_gmail);
        hotSiteGmail.setTitle(R.string.hot_site_gmail);
        hotSiteGmail.mUrl = GMAIL;
        mHotSiteInfoList.add(hotSiteGmail);

//        HotGameInfo hotSiteTwitter = new HotGameInfo(context);
//        hotSiteTwitter.setIcon(R.drawable.hot_site_twitter);
//        hotSiteTwitter.setTitle(R.string.hot_site_twitter);
//        hotSiteTwitter.mUrl = "https://twitter.com/";
//        mHotGameInfoList.add(hotSiteTwitter);
//
//        HotGameInfo hotSiteWiki = new HotGameInfo(context);
//        hotSiteWiki.setIcon(R.drawable.hot_site_wiki);
//        hotSiteWiki.setTitle(R.string.hot_site_wiki);
//        hotSiteWiki.mUrl = "https://www.wikipedia.org/";
//        mHotGameInfoList.add(hotSiteWiki);
//
//        HotGameInfo hotSiteBBC = new HotGameInfo(context);
//        hotSiteBBC.setIcon(R.drawable.hot_site_bbc);
//        hotSiteBBC.setTitle(R.string.hot_site_bbc);
//        hotSiteBBC.mUrl = "http://www.bbc.com/news";
//        mHotGameInfoList.add(hotSiteBBC);
//
//        HotGameInfo hotSiteYahoo = new HotGameInfo(context);
//        hotSiteYahoo.setIcon(R.drawable.hot_site_yahoo);
//        hotSiteYahoo.setTitle(R.string.hot_site_yahoo);
//        hotSiteYahoo.mUrl = "https://www.yahoo.com/";
//        mHotGameInfoList.add(hotSiteYahoo);
//
//        HotGameInfo hotSiteEspn = new HotGameInfo(context);
//        hotSiteEspn.setIcon(R.drawable.hot_site_espn);
//        hotSiteEspn.setTitle(R.string.hot_site_espn);
//        hotSiteEspn.mUrl = "https://www.espn.com/";
//        mHotGameInfoList.add(hotSiteEspn);

        return mHotSiteInfoList;
    }
}
