package com.ada.versionupgrade.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ada.versionupgrade.R;
import com.ada.versionupgrade.beans.ServiceInfo;
import com.ada.versionupgrade.interfaces.IUpgradeApp;
import com.ada.versionupgrade.utils.UpgradeUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络信息解析类
 */

public class NetInfoParser {

    private Context context = null;
    private Handler mHandler = null;
    private IUpgradeApp upgradeAPP;

    private final String SURVER_CONN = "SUCCESS";
    private final String SURVER_STOP = "FAIL";

    public NetInfoParser(Context context, IUpgradeApp upgradeAPP) {
        this.context = context;
        this.upgradeAPP = upgradeAPP;
    }

    /**
     *
     * @param URL 服务器更新信息地址
     */
    public void setURL_APPUPGRADE(String URL){
        UpgradeUtils.URL_APPUPGRADE=URL;
    }

    /**
     *
     * @param URL APK下载地址
     */
    public void setURLPRODUCTUPGRADEAPP(String URL){
        UpgradeUtils.URLPRODUCTUPGRADEAPP=URL;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UpgradeUtils.UPDATA_NONEED:
                    Log.i("Welcome","UPDATA_NONEED");
                    UpgradeUtils.needUpdate = false;
                    break;
                case UpgradeUtils.UPDATA_CLIENT:
                    Log.i("Welcome","UPDATA_CLIENT");
                    UpgradeUtils.needUpdate = true;
                    break;
                case UpgradeUtils.GET_UNDATAINFO_ERROR:
                    Log.i("Welcome","GET_UNDATAINFO_ERROR");
                    UpgradeUtils.needUpdate = false;
                    break;
                case 111:
                    String result = (String) msg.obj;
                    if (result.equals(SURVER_CONN)) {
                        try {
                            Log.i("Welcome","SURVER_CONN");
//                            UTIL.VERSION = getVersionName();
                            CheckVersionTask cv = new CheckVersionTask();
                            new Thread(cv).start();
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        upgradeAPP.noNeedUpgradeAPP(context);
                    }
                    return;
            }
            updateVersion();
        }
    };

    //----------------------- 欢迎页APP版本更新 ------------------------

    public void Check() {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checked();
            }
        }, 200);
    }

    private void checked() {
        //版本检查
        try {
            // 网络已连接
            if (UpgradeUtils.isNetworkAvailable(context)) {

                // 判断服务器如果正常则提示更新
                isServerOK();
                /*UTIL.VERSION = getVersionName();
                CheckVersionTask cv = new CheckVersionTask();

                new Thread(cv).start();
                Thread.sleep(500);*/
            } else {
                upgradeAPP.noNeedUpgradeAPP(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断网络连接是否可用
    private void isServerOK() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = Message.obtain();
                msg.what = 111;
                URL url;
                HttpURLConnection conn = null;
                try {
                    url = new URL(UpgradeUtils.URL_APPUPGRADE);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(1000 * 5);
                    if (conn.getResponseCode() == 200) {
                        msg.obj = SURVER_CONN;
                        Log.i("Welcome"," URL测试通过");
                        handler.sendMessage(msg);
                    }
                } catch (MalformedURLException e) {
                    msg.obj = SURVER_STOP;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.obj = SURVER_STOP;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }
            }
        }.start();
    }

    /**
     * 获取服务器上版本
     * @author zhoudan
     * @date 2016年4月18日
     */
    public class CheckVersionTask implements Runnable {
        InputStream is;
        public void run() {
            try {
                URL url = new URL(UpgradeUtils.URL_APPUPGRADE);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("contentType", "utf-8");
                conn.setRequestProperty("Accept-Charset", "utf-8");
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // 从服务器获得一个输入流
                    is = conn.getInputStream();
                }
                UpgradeUtils.INFO = getUpdataInfo(is);
                UpgradeUtils.NewVersion=UpgradeUtils.INFO.getVersion();
                System.out.println("Welcome 服务器版本号：" + UpgradeUtils.INFO.getVersion());
                System.out.println("Welcome 本地版本号：" + UpgradeUtils.VERSION);
                if (UpgradeUtils.INFO.getVersion().equals(UpgradeUtils.VERSION)) {
                    System.out.println(" Welcome 版本相同");
                    Message msg = new Message();
                    msg.what = UpgradeUtils.UPDATA_NONEED;
                    handler.sendMessage(msg);
                } else {
                    System.out.println("版本不同");
                    Message msg = new Message();
                    msg.what = UpgradeUtils.UPDATA_CLIENT;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = UpgradeUtils.GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
            }
        }
    }

    /**
     * 获取服务器中的版本更新信息
     * @param is 数据流
     * @return
     * @throws Exception
     */
    public ServiceInfo getUpdataInfo(InputStream is) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");
        int type = parser.getEventType();
        ServiceInfo info = new ServiceInfo();
        List<String> listItem=new ArrayList<String>();
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if (UpgradeUtils.NODE_NAME.equalsIgnoreCase(parser.getName())) {
                    }
                    if (UpgradeUtils.AN_ITEM.equalsIgnoreCase(parser.getName())) {
                        String a=parser.nextText();
                        Log.i("XmlPullParser",a);
                        listItem.add(a);
                    }
                    if (UpgradeUtils.AN_PRUDUCTVERCION.equals(parser.getName())) {
                        info.setVersion(parser.nextText());
                    }
                    if (UpgradeUtils.AN_PRUDUCTURL.equals(parser.getName())) {
                        UpgradeUtils.URLPRODUCTUPGRADEAPP = parser.nextText();
                        info.setUrl(UpgradeUtils.URLPRODUCTUPGRADEAPP);
                    }
                    if (UpgradeUtils.AN_PRUDUCTDESCRIBE.equals(parser.getName())) {
                        info.setDescription(parser.nextText());
                    }
                    Log.i("XmlPullParser", "Namespace:" + parser.getNamespace() + ";Name:" + parser.getName());
                    break;
            }
            type = parser.next();
        }
        info.setListUpdates(listItem);
        UpgradeUtils.Dialog_items=listItem;
        return info;
    }

    /**
     * 获取当前版本
     */
    private String getVersionName() throws Exception {
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),
                0);
        return packInfo.versionName;
    }

    /**
     * 执行版本更新检查
     */
    private void updateVersion() {
        if (UpgradeUtils.INFO != null && UpgradeUtils.needUpdate) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    upgradeAPP.setUpgradeDialog("",UpgradeUtils.VERSION,UpgradeUtils.NewVersion,UpgradeUtils.Dialog_items);
                    showUpdataDialog(UpgradeUtils.VIEW_DIALOG_UPGRADE,UpgradeUtils.Update_Dialog_View);
                }
            }, 20);

        } else if (UpgradeUtils.INFO != null && !UpgradeUtils.needUpdate) {
            UpgradeUtils.Toast("当前已是最新版本，无需更新", context);
            upgradeAPP.noNeedUpgradeAPP(context);
        }
    }

    /**
     * 设置升级弹窗
     * 若不设置，则采用默认样式
     *
     * @param title           标题
     * @param description     说明文字
     * @param positiveBtn     确定按钮的文字
     * @param cancelBtn       取消按钮的文字
     * @param tipsCantConnect 无法连接时的提示文字
     */
    public static void setUpdataDialog(String title, String description, String positiveBtn, String cancelBtn, final String tipsCantConnect) {
        UpgradeUtils.DU_TITLE = title;
        UpgradeUtils.DU_DESCRIPTION = description;
        UpgradeUtils.DU_BTN_POSITIVE = positiveBtn;
        UpgradeUtils.DU_BTN_CANCEL = cancelBtn;
        UpgradeUtils.DU_TIP_UNCONNNECTED = tipsCantConnect;
    }

    /**
     * 显示APP升级弹窗
     */
    private void showUpdataDialog(final View view, View dialogView) {
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle(UpgradeUtils.DU_TITLE);

//        builer.setMessage(UpdateUtils.DU_DESCRIPTION);
        builer.setView(dialogView);
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton(UpgradeUtils.DU_BTN_POSITIVE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (UpgradeUtils.isNetworkAvailable(context)) {
                    new APKDownload(context, upgradeAPP).downLoadApk(view);
                } else {
                    UpgradeUtils.Toast(UpgradeUtils.DU_TIP_UNCONNNECTED, context);
                    upgradeAPP.unConnected(context);
                }
            }
        });
        builer.setNegativeButton(UpgradeUtils.DU_BTN_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                upgradeAPP.unUpgradeAPP(context);
            }
        });
        builer.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    Log.i("XmlPullParser", "按下了back键    onKeyDown()");
                    System.out.println("按下了back键   onKeyDown()");
                    upgradeAPP.noNeedUpgradeAPP(context);
                    return false;
                }
                return false;
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }
}