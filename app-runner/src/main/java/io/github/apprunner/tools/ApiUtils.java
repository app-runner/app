package io.github.apprunner.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songyinyin
 * @since 2023/11/1 17:28
 */
@Slf4j
public class ApiUtils {

    public static List<AppDO> apiList(String appName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", appName);
        ONode oNode = apiRequest(Util.API_LIST, paramMap);
        return oNode.toObjectList(AppDO.class);
    }


    public static AppDO apiLatestVersion(String appName, String version) {
        if (StrUtil.isBlank(appName)) {
            throw new AppRunnerException("appName not null");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appName", appName);
        paramMap.put("version", version);
        ONode oNode = apiRequest(Util.API_LATEST_VERSION, paramMap);
        AppDO appDO = oNode.toObject(AppDO.class);
        if (appDO == null) {
            throw new AppRunnerException("App not found: %s".formatted(appName));
        }
        appDO.setVersion(appDO.getAppLatestVersion().getVersion());
        return appDO;
    }

    public static List<String> apiGetAppRuntimeSdkUrls(String appType, Long requiredVersion) {
        if (StrUtil.isBlank(appType)) {
            throw new AppRunnerException("appType not null");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appType", appType);
        paramMap.put("requiredAppTypeVersionNum", requiredVersion);
        paramMap.put("osName", Util.getOsName());
        paramMap.put("osArch", Util.getOsArch());
        ONode oNode = apiRequest(Util.API_GET_APP_RUNTIME_SDK_URLS, paramMap);
        return oNode.toObjectList(String.class);
    }

    /**
     * 下载文件
     *
     * @param downloadUrl 文件下载地址
     * @param destFile    目标文件
     * @return 下载的文件
     */
    public static File downloadFile(String downloadUrl, File destFile) {
        return HttpUtil.downloadFileFromUrl(downloadUrl, destFile, new DownloadStreamProgress());
    }

    /**
     * 请求 app admin 接口
     *
     * @param path     接口路径
     * @param paramMap 参数
     * @return 返回结果
     */
    private static ONode apiRequest(String path, Map<String, Object> paramMap) {
        String apiUrl = Util.apiUrl();
        String url = apiUrl + path;

        ONode oNode = getRequest(url, paramMap);
        int status = oNode.get("status").getInt();
        if (status != 0) {
            throw new AppRunnerException("request was aborted(%s)：%s".formatted(status, oNode.get("msg").getString()));
        }
        return oNode.get("data");
    }

    public static ONode getRequest(String url, Map<String, Object> paramMap) {
        if (Util.isDebugMode()) {
            log.info("{} request: {}", url, paramMap);
        }
        HttpResponse httpResponse = HttpRequest.get(url).contentType("application/json;charset=UTF-8").form(paramMap).timeout(Util.timeout()).execute();
        String responseBody = httpResponse.body();
        if (httpResponse.getStatus() != 200) {
            throw new AppRunnerException("request was failed(%s)：%s".formatted(httpResponse.getStatus(), responseBody));
        }
        if (Util.isDebugMode()) {
            log.info("{} response: {}", url, responseBody);
        }
        return ONode.loadStr(responseBody);
    }
}
