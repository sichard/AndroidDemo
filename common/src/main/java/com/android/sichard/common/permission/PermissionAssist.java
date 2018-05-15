package com.android.sichard.common.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author htoall
 * @Description: 权限申请助手类
 * 申请权限的Activity类需要重载onRequestPermissionsResult方法，并调用permissonResult方法,其中requestCode参数作为newInstance参数
 * @date 2016/10/19 下午1:38
 * @copyright TCL-MIE
 */
@TargetApi(Build.VERSION_CODES.M)
public class PermissionAssist {
    public static final boolean IS_MARSHMALLOW = Build.VERSION.SDK_INT >= 23;
    private PermissionListener mPermissionListener;
    private int idCode;
    private static Map<Integer, PermissionAssist> permissionListenerMap = new HashMap();

    /**
     * 获取实例
     *
     * @param idCode 权限申请的识别码，不同申请要区分
     * @return
     */
    public static PermissionAssist newInstance(int idCode) {
        PermissionAssist permissionAssist = permissionListenerMap.get(idCode);
        if (permissionAssist == null) {
            permissionAssist = new PermissionAssist(idCode);
            permissionListenerMap.put(idCode, permissionAssist);
        }
        return permissionAssist;
    }

    public PermissionAssist(int idCode) {
        this.idCode = idCode;
    }

    /**
     * 查询是否有权限
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean havePermission(Context activity, String permission) {
        if (!IS_MARSHMALLOW) {
            return true;
        }
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean requestPermission(Activity activity, PermissionListener permissionListener, String... permissions) {
        if (!IS_MARSHMALLOW) {
            return true;
        }
        mPermissionListener = permissionListener;
        String[] unPermissions = checkPermission(activity, false, permissions);
        // 申请权限
        if (unPermissions != null && unPermissions.length != 0) {
            try {
                // 部分手机调用该系统方法去获取权限时会报错,故此处需要捕获异常
                activity.requestPermissions(unPermissions, idCode);
            } catch (Exception e) {
                Toast.makeText(activity, "Request permission failed!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return false;
        }
        permissionListenerMap.remove(idCode);
        return true;
    }

    /**
     * 延迟进行权限申请
     *
     * @param activity
     * @param permissionListener
     * @param intervalTime       间隔时间
     * @param permissions
     * @return
     */
    public boolean requestPermissionDelay(final Activity activity, final PermissionListener permissionListener, int intervalTime, final String... permissions) {
        if (!IS_MARSHMALLOW) {
            return true;
        }
        String[] unPermissions = checkPermission(activity, false, permissions);
        if (unPermissions != null && unPermissions.length != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestPermission(activity, permissionListener, permissions);
                }
            }, intervalTime);
            return false;
        }
        permissionListenerMap.remove(idCode);
        return true;
    }

    /**
     * 查询已经有的权限
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static String[] queryPermission(Activity activity, String... permissions) {
        return checkPermission(activity, true, permissions);
    }

    /**
     * 检测哪些权限未授权
     *
     * @param activity
     * @param permissions
     * @param have        true表示获取已有权限，false表示获取未获取权限
     * @return 未授权的权限数组
     */
    private static String[] checkPermission(Activity activity, boolean have, String... permissions) {
        if (!IS_MARSHMALLOW) {
            return null;
        }
        List<String> permissionList = new ArrayList<>(permissions.length);
        for (String permission : permissions) {
            if (!(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED)) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            return permissionList.toArray(new String[]{});
        }
        return null;
    }

    public void permissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mPermissionListener == null) {
            return;
        }
        List<String> permissionSuccessList = new ArrayList<>(permissions.length);
        List<String> permissionFailueList = new ArrayList<>(permissions.length);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                permissionSuccessList.add(permissions[i]);
            } else {
                permissionFailueList.add(permissions[i]);
            }
        }
        if (!permissionSuccessList.isEmpty()) {
            mPermissionListener.onSuccess(requestCode, permissionSuccessList.toArray(new String[]{}));
        }
        if (!permissionFailueList.isEmpty()) {
            mPermissionListener.onFailure(requestCode, permissionFailueList.toArray(new String[]{}));
        }
        mPermissionListener.finish(requestCode, permissions);
        permissionListenerMap.remove(idCode);
    }

    // 移除idCode对应的实例
    public static void removeIdCode(int idCode) {
        permissionListenerMap.remove(idCode);
    }

    /**
     * 权限返回结果监听
     */
    public interface PermissionListener {
        void onSuccess(int requestCode, String[] permission);

        void finish(int requestCode, String[] permissions);

        void onFailure(int requestCode, String[] permission);
    }
}
