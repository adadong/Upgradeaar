package com.ada.versionupgrade.interfaces;

import android.content.Context;

import java.util.List;

/**
 * Created by Ada on 2017/11/1.
 */

public interface IUpgradeApp {
    /**
     * TODO 设置升级APP的相关参数；
     *      重点要设置UTIL.URLPRODUCTUPGRADE
     *       AN_PRUDUCTVERCION = "version";
     *       N_PRUDUCTDESCRIBE = "description";
     *       AN_PRUDUCTURL = "url";
     *      VIEW_DIALOG_UPGRADE
     *      若不进行设置则使用默认值
     */
    void setUpgradeSettings();
    
    /**
     * TODO 设置升级弹窗的样式及文字，若不设置，则使用默认信息
     * 通过  UTIL.VIEW_DIALOG_UPGRADE
     * @param title
     * @param currentVersion 当前版本
     * @param newVerision  最新版本
     * @param Dialog_items 版本更改信息
     */
    void setUpgradeDialog(String title,String currentVersion,String newVerision, List<String> Dialog_items);

    /**
     * TODO 用户选择不升级APP时，app要执行的事情
     * @param context
     */
    void unUpgradeAPP(Context context);


    /**TODO 不需要升级APP时，app要执行的事情
     * @param context
     */
    void noNeedUpgradeAPP(Context context);

    /**TODO 无网络时，app要执行的事情
     * @param context
     */
    void unConnected(Context context);
}
