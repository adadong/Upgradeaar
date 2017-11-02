package com.ada.versionupgrade.beans;

import java.util.List;

/**
 * Created by Ada on 2017/11/1.
 */

public class ServiceInfo {
    /**
     *  版本更新xml文件   服务器上版本
     *  <Packages>
     *      <Package version="V1.0.0" url="http://60.205.224.13:8080/HaiNanAPP/APK/V1.0.0/app-release.apk" description="海南生态红线 ">
     *          <version>V1.0.0</version>
     *          <updates>
     *              <item>更新说明1 。</item>
     *              <item>更新说明2 。</item>
     *              <item>。。。。。。</item>
     *          </updates>
     *          <url>http://60.205.224.13:8080/HaiNanAPP/APK/V1.0.0/app-release.apk</url>
     *          <description>海南生态红线</description>
     *      </Package>
     *  </Packages>
     */

    /**
     * 服务器版本号
     */
    private String version;

    /**
     * APK下载地址
     */
    private String url;

    /**
     * 版本描述
     */
    private String description;

    /**
     * 版本跟新信息
     */
    private List<String> listUpdates;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getListUpdates() {
        return listUpdates;
    }

    public void setListUpdates(List<String> listUpdates) {
        this.listUpdates = listUpdates;
    }
}
