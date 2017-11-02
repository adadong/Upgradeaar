package com.ada.versionupgrade.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ada.versionupgrade.R;
import com.ada.versionupgrade.interfaces.IUpgradeApp;
import com.ada.versionupgrade.managers.NetInfoParser;
import com.ada.versionupgrade.utils.UpgradeUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IUpgradeApp{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Welcome","进入程序，检查更新");
        //接入页面，检查更新
        UpgradeUtils.setVERSION("V1.0.1");
        NetInfoParser infoParser = new NetInfoParser(this, this);
        //设置服务器查询地址
        infoParser.setURL_APPUPGRADE("http://60.205.224.13:8080/HaiNanAPP/AppUpdate.xml");
        infoParser.Check();
    }

    @Override
    public void setUpgradeSettings() {
        //FIXME 指标名解析设置，示例如下：
        UpgradeUtils.AN_PRUDUCTVERCION = "version";
        UpgradeUtils.AN_PRUDUCTDESCRIBE = "description";
        UpgradeUtils.AN_PRUDUCTURL = "url";
        UpgradeUtils.AN_ITEM="item";
    }

    @Override
    public void setUpgradeDialog(String title,String currentVersion,String newVerision, List<String> Dialog_items) {
        UpgradeUtils.VIEW_DIALOG_UPGRADE = new View(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.dialog_update_layout, null);
        TextView item= (TextView) view.findViewById(R.id.txt_update_dialog_items);
        TextView cVersion= (TextView) view.findViewById(R.id.txt_current_version);
        TextView newVersion= (TextView) view.findViewById(R.id.txt_new_version);
        cVersion.setText(UpgradeUtils.VERSION);
        newVersion.setText(UpgradeUtils.NewVersion);
        String des="";
        for(int i=0;i<UpgradeUtils.Dialog_items.size();i++){
            des+=UpgradeUtils.Dialog_items.get(i)+"\n";
        }
        item.setText(des);
        UpgradeUtils. Update_Dialog_View=view;
//        UpdateUtils.VIEW_DIALOG_UPGRADE.setBackgroundResource(R.drawable.logo);
        UpgradeUtils.STYLE_DIALOG_UPGRADE = AlertDialog.THEME_TRADITIONAL;
    }

    @Override
    public void unUpgradeAPP(Context context) {
        Toast.makeText(this,"不更新",Toast.LENGTH_LONG).show();
    }

    @Override
    public void noNeedUpgradeAPP(Context context) {
        Toast.makeText(this,"不更新noNeedUpgradeAPP",Toast.LENGTH_LONG).show();
    }

    @Override
    public void unConnected(Context context) {
        Toast.makeText(this,"未连接",Toast.LENGTH_LONG).show();
    }
}
