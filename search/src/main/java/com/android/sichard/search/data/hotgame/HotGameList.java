package com.android.sichard.search.data.hotgame;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.android.sichard.search.R;
import com.android.sichard.search.data.ISearchItem;

/**
 * <br>类描述:热点网站列表封装类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/7</b>
 */

public class HotGameList extends IHotGameImpl {
    // 热点网站初始链接
    // 三消
    private final String JEWEL_JUNGLE = "https://play.famobi.com/jewel-jungle/A-QTK8D";
    // 泡泡龙
    private final String BUBBLE_SPIRIT = "https://play.famobi.com/bubble-spirit/A-QTK8D";
    // 纸牌 空当接龙
    private final String SOLITAIRE_CLASSIC = "https://play.famobi.com/solitaire-classic/A-QTK8D";
    // 神庙逃亡兔子版本
    private final String HOP_DONT_STOP = "https://play.famobi.com/hop-dont-stop/A-QTK8D";
    // 弹球 打砖块
    private final String SHARDS = "https://play.famobi.com/shards/A-QTK8D";
    /** 要展示热点网站的列表 */
    public List<HotGameInfo> mHotGameInfoList = new ArrayList<>();

    public HotGameList(Context context) {
        mType = ISearchItem.TYPE_ITEM;
        createHotGameList(context);
    }

    /**
     * 获取热点网站列表数据
     * @return
     */
    private List<HotGameInfo> createHotGameList(Context context) {

        HotGameInfo hotGameJewelJungle = new HotGameInfo(context);
        hotGameJewelJungle.setIcon(R.drawable.ic_game_jewel_jungle_teaser);
        hotGameJewelJungle.setTitle(R.string.hot_game_jewel_jungle);
        hotGameJewelJungle.mUrl = JEWEL_JUNGLE;
        mHotGameInfoList.add(hotGameJewelJungle);

        HotGameInfo hotGameBubbleSpirit = new HotGameInfo(context);
        hotGameBubbleSpirit.setIcon(R.drawable.ic_game_bubble_spirit_teaser);
        hotGameBubbleSpirit.setTitle(R.string.hot_game_bubble_spirit);
        hotGameBubbleSpirit.mUrl = BUBBLE_SPIRIT;
        mHotGameInfoList.add(hotGameBubbleSpirit);

        HotGameInfo hotGameSolitaireClassic = new HotGameInfo(context);
        hotGameSolitaireClassic.setIcon(R.drawable.ic_game_solitaire_classic_teaser);
        hotGameSolitaireClassic.setTitle(R.string.hot_game_solitaire_classic);
        hotGameSolitaireClassic.mUrl = SOLITAIRE_CLASSIC;
        mHotGameInfoList.add(hotGameSolitaireClassic);

        HotGameInfo hotGameHopDontStop = new HotGameInfo(context);
        hotGameHopDontStop.setIcon(R.drawable.ic_game_hop_dont_stop_teaser);
        hotGameHopDontStop.setTitle(R.string.hot_game_hop_dont_stop);
        hotGameHopDontStop.mUrl = HOP_DONT_STOP;
        mHotGameInfoList.add(hotGameHopDontStop);

        HotGameInfo hotGameShards = new HotGameInfo(context);
        hotGameShards.setIcon(R.drawable.ic_game_shards_teaser);
        hotGameShards.setTitle(R.string.hot_game_shards);
        hotGameShards.mUrl = SHARDS;
        mHotGameInfoList.add(hotGameShards);

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

        return mHotGameInfoList;
    }
}
