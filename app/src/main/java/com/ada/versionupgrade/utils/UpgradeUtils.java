package com.ada.versionupgrade.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.ada.versionupgrade.beans.ServiceInfo;

import java.util.List;

/**
 * Created by Ada on 2017/11/1.
 */

public class UpgradeUtils {

    /**
     * 检查更新信息
     */
    public static final int UPDATA_NONEED = 0;
    public static final int UPDATA_CLIENT = 1;
    public static final int GET_UNDATAINFO_ERROR = 2;

    /**
     * 产品更新服务地址
     */
    public static String URL_APPUPGRADE = null;

    /**
     * app安装包apk下载地址
     */
    public static String URLPRODUCTUPGRADEAPP = null;

    /**
     * 系统升级弹窗的View，不包含可操作的控件，仅仅是背景布局
     */
    public static View VIEW_DIALOG_UPGRADE = null;
    public static View Update_Dialog_View=null;
    public static int STYLE_DIALOG_UPGRADE = AlertDialog.THEME_HOLO_DARK;


    /**
     * 服务器版本信息
     */
    public static ServiceInfo INFO = null;

    /**
     * 本地apk版本
     */
    public static String VERSION = "V1.0.1";
    public static String  NewVersion=null;
    /**
     * 升级窗口设置内容
     */
    public static String DU_TITLE = "版本升级";
    public static String DU_DESCRIPTION = "检测到最新版本，请及时更新！";
    public static List<String> Dialog_items=null;
    public static String DU_BTN_POSITIVE = "确定";
    public static String DU_BTN_CANCEL = "取消";
    public static String DU_TIP_UNCONNNECTED = "网络不可用，无法下载更新";


    /**
     * 服务器的节点信息
     */
    public static final String NODE_NAME = "Package";
    public static String AN_PRUDUCTVERCION = "version";
    public static String AN_PRUDUCTDESCRIBE = "description";
    public static String AN_PRUDUCTURL = "url";
    public static String AN_ITEM="item";

    /**
     * 版本是否需要更新
     */
    public static boolean needUpdate = false;


    /**
     * 判断当前网络连接状态
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            // 如果仅仅是用来判断网络连接　　　　　　
            // 则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED
                            || info[i].getState() == NetworkInfo.State.CONNECTING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }






    /**
     * 前台显示
     *
     * @param text    提示信息
     * @param context 上下文文件
     */
    public static void Toast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }







    public static boolean isListEmpty(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    public static void setVERSION(String CurrentVersion){
        VERSION=CurrentVersion;
    }
}
