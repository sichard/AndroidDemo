package com.tcl.launcherpro.search.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.tcl.launcherpro.search.R;
import com.tcl.launcherpro.search.utils.DeviceUtil;


/**
 *<br>类描述：搜索WebView
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2016-8-22</b>
 */
public class SearchWebView extends FrameLayout {

	private Context mContext;
	private WebView mWebView;
	private ProgressBar mLoadProgressBar;
	/** 是否网络请求失败 */
	private boolean mIsRequestFailed = false;
	private FrameLayout mWebViewContent;

	public SearchWebView(Context context) {
		super(context);
		mContext = context;
	}

	public SearchWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public SearchWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected void init() {
		mLoadProgressBar = (ProgressBar) findViewById(R.id.load_progressbar);
		mWebViewContent = (FrameLayout) findViewById(R.id.web_view_content);
	}

	private void initWebView() {
		if (mWebView == null) {
			return;
		}
		WebSettings ws = mWebView.getSettings();
		// 设置可自动加载图片
		ws.setLoadsImagesAutomatically(true);
		// 可用JS
		ws.setJavaScriptEnabled(true);
		// 设置 Application Caches API 可用
		ws.setAppCacheEnabled(true);
		// 设置DOM storage API可用
		ws.setDomStorageEnabled(true);
		// 设置触摸放大/缩小
		ws.setBuiltInZoomControls(true);
		// 设置双击变大变小动作响应
		ws.setUseWideViewPort(true);
		// ws.setRenderPriority(RenderPriority.HIGH);
		// 设置最后加载图片，提升加载速度
		ws.setBlockNetworkImage(true);
		// 设置缓冲大小，此处设置的是8M
		// ws.setAppCacheMaxSize(1024 * 1024 * 8);
		String appCacheDir = mContext.getDir("cache", Context.MODE_PRIVATE).getPath();
		ws.setAppCachePath(appCacheDir);
		ws.setAllowFileAccess(true);
		ws.setCacheMode(WebSettings.LOAD_DEFAULT);
		// 设置WebView加载页面的模式(适应手机屏幕)
		ws.setLoadWithOverviewMode(true);
		mWebView.setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);
		// 设置超级链接
		mWebView.setWebViewClient(new WebViewClient() {
			// 设置可以链接网页中的超级链接
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				// 因为有的引擎有域名重定向，所以只要url中包含引擎字符，则认为是使用引擎搜索，WebView自己处理链接，不跳转浏览器
//				if (url.contains("www.google.com") || url.contains("www.baidu.com") || url.contains("bing.com") || url.contains("yandex.com")) {
//					// 暂时设置为不允许加载图片，等网页加载完之后，再对图片进行加载
//					view.getSettings().setBlockNetworkImage(true);
//					mLoadProgressBar.setVisibility(View.VISIBLE);
//					// 载入网页
//					view.loadUrl(url);
//					return false;
//				} else {
//					try {
//						Intent intent = new Intent(Intent.ACTION_VIEW);
//						intent.setData(Uri.parse(url));
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						getContext().startActivity(intent);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					return true;
//				}
//
				view.getSettings().setBlockNetworkImage(true);
				mLoadProgressBar.setVisibility(View.VISIBLE);
				// 载入网页
//				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mIsRequestFailed = false;
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//				 super.onReceivedError(view, request, error);
				mIsRequestFailed = true;
				onBackPressed();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// 网页加载结束，对图片进行加载
				view.getSettings().setBlockNetworkImage(false);
				super.onPageFinished(view, url);
			}

			@Override
			public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
				super.doUpdateVisitedHistory(view, url, isReload);
			}
		});

		// 设置网页的加载进度
		mWebView.setWebChromeClient(new WebChromeClient() {
			// 载入进度改变而触发
			public void onProgressChanged(WebView view, int progress) {
				mLoadProgressBar.setProgress(progress);
				if (progress == 100) {
					// 如果全部载入,隐藏进度条
					mLoadProgressBar.setVisibility(GONE);
				} else {
					mLoadProgressBar.setVisibility(VISIBLE);
				}

				super.onProgressChanged(view, progress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				boolean isNetworkOK = DeviceUtil.isNetworkOK(mContext);
				if (!isNetworkOK) {
					mIsRequestFailed = true;
					onBackPressed();
				}
			}

		});
		// 添加下载监听
		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				// 跳到浏览器下载
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 如果点击位置在margin之上，则不处理点击事件，交给SearchAppLayer处理
//		if (ev.getY() < mTopMargin) {
//			return false;
//		} else {
			return super.dispatchTouchEvent(ev);
//		}
	}

	/**
	 * <br>功能简述：加载网页
	 * @param url 加载网页的url
	 */
	public void loadUrl(String url) {
		setVisibility(VISIBLE);
		if (mWebView == null) {
			mWebView = new WebView(mContext);
			mWebViewContent.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			initWebView();
		}
		mWebView.loadUrl(checkUrl(url));
	}

	/**
	 * <br>功能简述：判定传入网页链接是否包含“http://”,如果不包含则加上。
	 * <br>注意:必须检验传入的url，如果不包含"http://",在WebView中将打不开网页
	 * @param url
	 * @return
	 */
	private String checkUrl(String url) {
		if (!url.contains("http")) {
			url = "http://" + url;
		}
		return url;
	}

	public boolean onBackPressed() {
		setVisibility(GONE);
		final ViewParent parent = getParent();
		final SearchView searchView = (SearchView) parent;
		searchView.updateSearchResult(mIsRequestFailed);
		return true;
	}

	public boolean webViewGoBack() {
		if(mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 销毁当前WebView
	 */
	public void removeWebView() {
		if (mWebView != null) {
			final WebView finalWebView = mWebView;
			postDelayed(new Runnable() {
				@Override
				public void run() {
					finalWebView.destroy();
				}
			}, 3000);

			mWebViewContent.removeView(mWebView);
			mWebView = null;
		}
	}
}
