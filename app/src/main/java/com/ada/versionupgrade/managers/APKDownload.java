package com.ada.versionupgrade.managers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.ada.versionupgrade.interfaces.IUpgradeApp;
import com.ada.versionupgrade.utils.UpgradeUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 版本更新APK下载
 *
 * @author zhoudan
 * @date 2015年11月27日
 */
public class APKDownload {
    private Context activity;
    private IUpgradeApp mUpgradeAPP;

    public APKDownload(Context activity, IUpgradeApp mUpgradeAPP) {
        this.activity = activity;
        this.mUpgradeAPP = mUpgradeAPP;
    }

    /**
     * 下载APK
     *
     * @param path
     * @param pd
     * @return
     * @throws Exception
     */
    private File getFileFromServer(String path, ProgressDialog pd) throws IOException {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小 
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();

            String fileName = path.substring(path.lastIndexOf("/"));
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * 下载弹窗
     * @param view
     */
    public void downLoadApk(View view) {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(activity);
        if (view != null) {
            pd.setView(view);
        }
        pd.setProgressStyle(UpgradeUtils.STYLE_DIALOG_UPGRADE);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                File file = null;
                try {
                    file = getFileFromServer(UpgradeUtils.URLPRODUCTUPGRADEAPP, pd);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("dch", "下载APK出错：" + e.getMessage());
                    pd.dismiss();
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    return;
                }
                installApk(file);
                pd.dismiss();
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("msg:" + msg.what);
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    //下载apk失败
                    UpgradeUtils.Toast("下载新版本失败", activity);
                    mUpgradeAPP.noNeedUpgradeAPP(activity);
                    break;
            }
        }
    };

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作  
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //执行的数据类型  
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        activity.startActivity(intent);

        mUpgradeAPP.unUpgradeAPP(activity);
        //
    }

}
