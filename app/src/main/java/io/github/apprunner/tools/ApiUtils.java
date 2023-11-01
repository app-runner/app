package io.github.apprunner.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;

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

    private static ONode apiRequest(String path, Map<String, Object> paramMap) {
        String apiUrl = Util.apiUrl();
        String url = apiUrl + path;
        if (Util.isDebugMode()) {
            log.info("{} request: {}", url, paramMap);
        }
        String response = HttpUtil.get(url, paramMap, Util.timeout());
        if (Util.isDebugMode()) {
            log.info("{} response: {}", url, response);
        }
        ONode oNode = ONode.loadStr(response);
        int status = oNode.get("status").getInt();
        if (status != 0) {
            throw new AppRunnerException("request was aborted(%s)：%s".formatted(status, oNode.get("msg").getString()));
        }
        return oNode.get("data");
    }
}
