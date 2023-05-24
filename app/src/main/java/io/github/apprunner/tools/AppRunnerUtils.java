package io.github.apprunner.tools;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.persistence.ApplicationType;
import io.github.apprunner.persistence.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;
import org.noear.solon.Solon;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songyinyin
 * @since 2023/4/30 20:04
 */
@Slf4j
public class AppRunnerUtils {

    public static final String API_LIST = "/list";
    public static final String API_LATEST_VERSION = "/latestVersion";
    public static final String API_GET_APP_RUNTIME_SDK_URLS = "/getAppRuntimeSdkUrls";

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    public static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean isMac() {
        return IS_MAC;
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public static String getOsArch() {
        return System.getProperty("os.arch");
    }

    /**
     * 超时时间，单位毫秒
     */
    public static int timeout() {
        return Solon.cfg().getInt("apprunner.api.timeout", 5) * 1000;
    }

    public static String apiUrl() {
        return Solon.cfg().get("apprunner.api.url");
    }

    /**
     * AppRunner 应用数据目录，默认为：~/.apprunner
     */
    public static String getAppHome() {
        String appHome = Solon.cfg().get("apprunner.appHome");
        if (appHome == null) {
            appHome = SystemPropsUtil.get("user.home") + "/.apprunner";
        }
        return appHome;
    }

    /**
     * 应用软件包位置
     */
    public static String getAppPackage() {
        return getAppHome() + "/app";
    }

    /**
     * 应用运行时环境位置
     */
    public static String getAppRuntimeHome() {
        return getAppHome() + "/runtime";
    }

    /**
     * 临时目录，用于存放下载的文件
     */
    public static String getAppTmp() {
        return getAppHome() + "/tmp/" + System.currentTimeMillis();
    }

    public static String getAppPath(AppDO appDO) {
        String appPackage = getAppPackage();
        return "%s/%s/%s".formatted(appPackage, appDO.getName(), appDO.getVersion());
    }

    public static String getAppRuntimePath(String appType, Long requiredVersion) {
        String appRuntimeHome = getAppRuntimeHome();
        return "%s/%s/%s".formatted(appRuntimeHome, appType.toLowerCase(), requiredVersion);
    }

    public static boolean isDebugMode() {
        String stmDebug = Solon.cfg().get("apprunner.debug");
        return Solon.cfg().isDebugMode() || "1".equals(stmDebug);
    }

    public static String getJavaHome(Long requiredJreVersion) {
        String javaHome = Solon.cfg().get("apprunner.javaHome");
        if (StrUtil.isBlank(javaHome)) {
            javaHome = System.getProperty("java.home");
        }
        if (StrUtil.isBlank(javaHome)) {
            javaHome = System.getenv("JAVA_HOME");
        }
        if (StrUtil.isBlank(javaHome)) {
            javaHome = useDownloadJavaHome(requiredJreVersion);
        }
        return javaHome;
    }

    private static String useDownloadJavaHome(Long requiredJreVersion) {
        String appRuntimePath = getAppRuntimePath(ApplicationType.java.getType(), requiredJreVersion);
        File file = new File(appRuntimePath);
        // 目录不存在，下载对应版本的 jre
        if (!file.exists()) {
            FileUtil.mkdir(file);

            List<String> urls = apiGetAppRuntimeSdkUrls(ApplicationType.java.getType(), requiredJreVersion);
            String url = urls.get(0).split(",")[0];

            File downloadFile = HttpUtil.downloadFileFromUrl(url, FileUtil.mkdir(getAppTmp()), new DownloadStreamProgress());
            String downloadFileName = downloadFile.getName();
            if (downloadFileName.endsWith(".zip")) {
                log.info("unzip {} to {}", downloadFile, file);
                ZipUtil.unzip(downloadFile, file);
            } else if (downloadFileName.endsWith("tar.gz")) {
                log.info("gzip {} to {}", downloadFile, file);
                GzipUtil.extractTarGZ(downloadFile, appRuntimePath);
            } else {
                throw new AppRunnerException("Unsupported file type: %s".formatted(downloadFileName));
            }
        }
        String[] list = file.list();
        if (list != null) {
            List<String> subFiles = CollUtil.newArrayList(list);
            if (isMac()) {
                subFiles.remove(".DS_Store");
                appRuntimePath = "%s/%s/Contents/Home".formatted(appRuntimePath, subFiles.get(0));
            } else if (isWindows()) {
                String subFileName = subFiles.get(subFiles.size() - 1);
                appRuntimePath = "%s/%s".formatted(appRuntimePath, subFileName);
            }
        }
        if (isDebugMode()) {
            log.info("javaHome: {}", appRuntimePath);
        }
        return appRuntimePath;
    }


    ///////////////////////// API /////////////////////////

    public static List<AppDO> apiList(String appName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", appName);
        ONode oNode = apiRequest(API_LIST, paramMap);
        return oNode.toObjectList(AppDO.class);
    }


    public static AppDO apiLatestVersion(String appName, String version) {
        if (StrUtil.isBlank(appName)) {
            throw new AppRunnerException("appName not null");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appName", appName);
        paramMap.put("version", version);
        ONode oNode = apiRequest(API_LATEST_VERSION, paramMap);
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
        paramMap.put("osName", getOsName());
        paramMap.put("osArch", getOsArch());
        ONode oNode = apiRequest(API_GET_APP_RUNTIME_SDK_URLS, paramMap);
        return oNode.toObjectList(String.class);
    }

    private static ONode apiRequest(String path, Map<String, Object> paramMap) {
        String apiUrl = apiUrl();
        String url = apiUrl + path;
        if (isDebugMode()) {
            log.info("{} request: {}", url, paramMap);
        }
        String response = HttpUtil.get(url, paramMap, timeout());
        if (isDebugMode()) {
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
