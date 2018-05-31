package com.android.sichard.search;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.LayoutInflater;

import com.android.sichard.common.permission.PermissionAssist;
import com.android.sichard.common.permission.PermissionConstant;
import com.android.sichard.common.utils.DrawUtils;
import com.android.sichard.search.common.SearchAppManager;
import com.android.sichard.search.common.TaskManager;
import com.android.sichard.search.contact.ContactProvider;
import com.android.sichard.search.data.App.AppInfo;
import com.android.sichard.search.data.contact.ContactObserver;
import com.android.sichard.search.data.message.MessageProvider;
import com.android.sichard.search.recent.IRecentTask;
import com.android.sichard.search.theme.ThemeTools;
import com.android.sichard.search.view.SearchView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:搜索SDK集成类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-7</b>
 */
public class SearchSDK {
    private static SearchSDK sInstance;
    private static Context mContext;
    private SearchAppManager mSearchManager;
    /** 联系人变化监听 */
    private ContactObserver mContactObserver;
    private ContentObserver mMessageObserver;
    // 主题工具
    private ThemeTools mThemeTools;
    // 搜索过滤
    private AppsFilter mAppsFilter;
    // 最近使用
    private IRecentTask mRecentTask;
    // 应用搜索时间处理
    private ISearchAction mAciton;

    private SearchView mSearchView;

    private Runnable mContactRunnable;
    private Runnable mMessageRunnable;

    private SearchSDK() {
        if (mContext == null) {
            throw new RuntimeException("init() must be called first!!!");
        }
        TaskManager.execWorkTask(new Runnable() {
            @Override
            public void run() {
                // SearchAppManager需要读取应用信息时耗时操作，所以放在线程中加载
                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                mSearchManager = new SearchAppManager(mContext);
            }
        });
        initProvider();
    }

    /**
     * 确保调用init()方法后，再调用该方法。{@link #init(Context)}
     * @return
     */
    public static SearchSDK getInstance() {
        if (sInstance == null) {
            sInstance = new SearchSDK();
        }
        return sInstance;
    }

    /**
     * 搜索sdk初始化,
     */
    public static void init(Context context) {
        if (mContext != null) {
            return;
        }
        Constants.init(context.getPackageName());
        mContext = context.getApplicationContext();
        DrawUtils.resetDensity(mContext);
        getInstance();
    }

    public static void init(Context context, boolean isLoadApps) {
        SearchAppManager.setIsLoadApp(isLoadApps);
        init(context);
    }

    public static Context getContext() {
        return mContext;
    }

    private void initProvider() {
        final ContactProvider provider = ContactProvider.getInstance();
        mContactObserver = new ContactObserver(mContext, new Handler(TaskManager.getWorkThreadLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                provider.updateContact();
            }
        });
        mContactObserver.startListen();

        mMessageObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                TaskManager.execWorkTask(new Runnable() {
                    @Override
                    public void run() {
                        MessageProvider.getInstance().scanMessage();
                    }
                });
            }
        };
        mContext.getContentResolver().registerContentObserver(Uri.parse(MessageProvider.SMS_URI_ALL), true, mMessageObserver);
    }

    private void initWebViewProvider() {
        try {
            Class<?> webViewFactoryClass = Class.forName("android.webkit.WebViewFactory");
            Method getProviderClassMethod = webViewFactoryClass.getDeclaredMethod("getProviderClass");
            getProviderClassMethod.setAccessible(true);
            Class<?> webViewFactoryProviderClass = (Class<?>) getProviderClassMethod.invoke(null);
            getProviderClassMethod.setAccessible(false);

            Class<?> webViewDelegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> webViewDelegateConstructor = webViewDelegateClass.getDeclaredConstructor();
            webViewDelegateConstructor.setAccessible(true);
            Object webViewDelegate = webViewDelegateConstructor.newInstance();
            webViewDelegateConstructor.setAccessible(false);

            Object webViewFactoryProvider = webViewFactoryProviderClass.getConstructor(webViewDelegateClass).newInstance(webViewDelegate);

            Field providerInstanceField = webViewFactoryClass.getDeclaredField("sProviderInstance");
            providerInstanceField.setAccessible(true);
            providerInstanceField.set(null, webViewFactoryProvider);
            providerInstanceField.setAccessible(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 确定是否有读取联系人权限
     * @param activity
     */
    public void checkPermission(final Activity activity) {
        boolean isHavePermission = PermissionAssist.newInstance(PermissionConstant.PERMISSION_ALL).requestPermissionDelay(activity, new PermissionAssist.PermissionListener() {
            @Override
            public void onSuccess(int requestCode, String[] permissions) {
                for (String permission : permissions) {
                    if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                        scanContactDelayed();
                    }
                    if (permission.equals(Manifest.permission.READ_SMS)) {
                        scanMessageDelayed();
                    }
                }
            }

            @Override
            public void finish(int requestCode, String[] permissions) {

            }

            @Override
            public void onFailure(int requestCode, String[] permission) {

            }
        }, 1 * 1000, android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS);
        if (isHavePermission) {
            scanContactDelayed();
            scanMessageDelayed();
            return;
        }
        // 如果有联系人权限就扫描准备数据
        if (PermissionAssist.havePermission(activity, Manifest.permission.READ_CONTACTS)) {
            scanContactDelayed();
        }

        // 如果有读取短信权限就扫描准备数据
        if (PermissionAssist.havePermission(activity, Manifest.permission.READ_SMS) &&
                PermissionAssist.havePermission(activity, Manifest.permission.READ_CONTACTS)) {
            scanMessageDelayed();
        }
    }

    public void scanContactDelayed() {
        final ContactProvider provider = ContactProvider.getInstance();
        mContactRunnable = new Runnable() {
            @Override
            public void run() {
                provider.scanContact();
            }
        };
        if (!provider.scanContactOver()) {
            TaskManager.execWorkTaskDelay(mContactRunnable, 10 * 1000);
        }
    }

    public void scanMessageDelayed() {
        final MessageProvider provider = MessageProvider.getInstance();
        mMessageRunnable = new Runnable() {
            @Override
            public void run() {
                provider.scanMessage();
            }
        };
        if (!provider.scanMessageOver()) {
            TaskManager.execWorkTaskDelay(mMessageRunnable, 10 * 1000);
        }
    }

    public SearchView getSearchView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (mSearchView == null) {
            mSearchView = (SearchView) inflater.inflate(R.layout.search_view, null);
        }
        return mSearchView;
    }

    public void updateRecent() {
        if (mRecentTask != null) {
            List<ComponentName> recentList = mRecentTask.getRecentList();
            if (recentList != null) {
                List<AppInfo> recentAppList = new ArrayList<>();
                List<AppInfo> history = new ArrayList<AppInfo>();
                for (ComponentName componentName : recentList) {
                    SearchAppManager searchManager = SearchSDK.getInstance().getSearchManager();
                    if (searchManager != null && componentName != null) {
                        AppInfo app = searchManager.getAppInfo(componentName.toShortString());
                        if (app != null) {
                            history.add(app);
                        }
                    }
                }

                int i = 0;
                for (AppInfo info : history) {
                    if (i >= 10) {
                        break;
                    } else {
                        recentAppList.add(info);
                    }
                    i++;
                }
                SearchView searchView = getSearchView();
                if (searchView != null) {
                    searchView.setRecentAppList(recentAppList);
                }
            }
        }
    }

    public void releaseSearchView(){
        mSearchView = null;
    }

    public SearchAppManager getSearchManager() {
        return mSearchManager;
    }

    /**
     * 设置主题工具
     * @param themeTools
     */
    public void setThemeTools(ThemeTools themeTools) {
        mThemeTools = themeTools;
    }

    /**
     * 获取主题工具
     * @return
     */
    public ThemeTools getThemeTools() {
        return mThemeTools;
    }

    public void setAppsFilter(AppsFilter appsFilter) {
        mAppsFilter = appsFilter;
    }

    public AppsFilter getAppsFilter() {
        return mAppsFilter;
    }

    public void destroy() {
        if (mContactObserver != null) {
            mContactObserver.stopListen();
            mContactObserver = null;
        }
        if (mMessageObserver != null) {
            // 注册监听通话记录数据库
            mContext.getContentResolver().unregisterContentObserver(mMessageObserver);
            mMessageObserver = null;
        }
        if (mContactRunnable != null) {
            TaskManager.removeWorkTask(mContactRunnable);
        }
        if (mMessageRunnable != null) {
            TaskManager.removeWorkTask(mMessageRunnable);
        }
        ContactProvider.getInstance().release();
        MessageProvider.getInstance().release();
        sInstance = null;
    }

    public IRecentTask getRecentTask() {
        return mRecentTask;
    }

    public void setRecentTask(IRecentTask recentTask) {
        this.mRecentTask = recentTask;
    }

    public ISearchAction getSearchAction() {
        return mAciton;
    }

    public void setSearchAction(ISearchAction aciton) {
        this.mAciton = aciton;
    }
}
