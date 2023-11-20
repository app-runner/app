package io.github.apprunner.tools;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.core.util.ZipUtil;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.persistence.entity.ApplicationType;
import io.github.apprunner.plugin.AppRunnerException;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author songyinyin
 * @since 2023/4/30 20:04
 */
@Slf4j
public class Util {

    public static final String API_LIST = "/list";
    public static final String API_LATEST_VERSION = "/latestVersion";
    public static final String API_GET_APP_RUNTIME_SDK_URLS = "/getAppRuntimeSdkUrls";

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    public static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");

    private static boolean isDebugMode = false;

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

    /**
     * 应用路径，形如：~/.apprunner/app/{appName}/{version}
     */
    public static String getAppPath(AppDO appDO) {
        String appPackage = getAppPackage();
        return "%s/%s/%s".formatted(appPackage, appDO.getName(), appDO.getVersion());
    }

    /**
     * 应用运行时环境路径，形如：~/.apprunner/runtime/{appType}/{requiredVersion}
     *
     * @param appType         应用类型
     * @param requiredVersion 版本
     * @return 路径
     */
    public static String getAppRuntimePath(String appType, Long requiredVersion) {
        String appRuntimeHome = getAppRuntimeHome();
        return "%s/%s/%s".formatted(appRuntimeHome, appType.toLowerCase(), requiredVersion);
    }

    /**
     * app runner 路径
     */
    public static String getAppRunnerPath() {
        return getAppHome() + "/bin";
    }

    public static void setDebugMode(boolean debugMode) {
        Util.isDebugMode = debugMode;
    }

    public static boolean isDebugMode() {
        return Util.isDebugMode;
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

    /**
     * 解压文件
     *
     * @param zipFile    压缩的文件
     * @param outFileDir 解压到的目录
     */
    public static void unzip(File zipFile, String outFileDir) {
        String downloadFileName = zipFile.getName();
        if (downloadFileName.endsWith(".zip")) {
            ZipUtil.unzip(zipFile, FileUtil.mkdir(outFileDir), StandardCharsets.UTF_8);
            log.info("unzip {} to {}", zipFile, outFileDir);
        } else if (downloadFileName.endsWith(".tar.gz")) {
            GzipUtil.extractTarGZ(zipFile, outFileDir);
            log.info("gzip {} to {}", zipFile, outFileDir);
        } else {
            throw new AppRunnerException("Unsupported file type: %s".formatted(downloadFileName));
        }
    }

    private static String useDownloadJavaHome(Long requiredJreVersion) {
        String appRuntimePath = getAppRuntimePath(ApplicationType.java.getType(), requiredJreVersion);
        File file = new File(appRuntimePath);
        // 目录不存在，下载对应版本的 jre
        if (!file.exists()) {
            FileUtil.mkdir(file);

            List<String> urls = ApiUtils.apiGetAppRuntimeSdkUrls(ApplicationType.java.getType(), requiredJreVersion);
            String url = urls.get(0).split(",")[0];

            File downloadFile = ApiUtils.downloadFile(url, FileUtil.mkdir(getAppTmp()));
            unzip(downloadFile, appRuntimePath);
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

}
