package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.cli.AppRunnerVersionProvider;
import io.github.apprunner.cli.support.LatestAppVersion;
import io.github.apprunner.tools.ApiUtils;
import io.github.apprunner.tools.RunProcess;
import io.github.apprunner.tools.Util;
import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.runtime.NativeDetector;
import picocli.CommandLine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自我更新
 *
 * @author songyinyin
 * @since 2023/11/15 11:42
 */
@Slf4j
@Component
@CommandLine.Command(name = "selfupdate", description = "${bundle:selfupdate.description}")
public class SelfupdateCli extends AppRunnerSubCli {

    @CommandLine.Option(names = {"-c", "--channel"}, defaultValue = "gitee", description = "${bundle:selfupdate.parameter.channel}")
    private String channel;

    @Override
    protected Integer execute() throws Exception {
        LatestAppVersion latestAppVersion = getLatestAppVersion(channel);
        if (latestAppVersion == null) {
            log.info(getMessages("selfupdate.log.notFoundLastVersion"));
            return 0;
        }
        log.info(getMessages("selfupdate.log.upgrade", latestAppVersion.getVersion(), AppRunnerVersionProvider.getAppRunnerVersion()));

        if (StrUtil.compareVersion(latestAppVersion.getVersion(), AppRunnerVersionProvider.getAppRunnerVersion()) <= 0) {
            log.info(getMessages("selfupdate.log.noUpgradeRequired"));
            return 0;
        }
        File downloadFile = ApiUtils.downloadFile(latestAppVersion.getDownloadUrl(), FileUtil.mkdir(Util.getAppTmp()));
        String downloadFileName = downloadFile.getName();
        String appRunnerPath = Util.getAppRunnerPath();
        if (FileUtil.exist(appRunnerPath)) {
            // 备份之前的版本
            File[] files = FileUtil.ls(appRunnerPath);
            for (File file : files) {
                if (!file.isDirectory()) {
                    FileUtil.copy(file, new File(appRunnerPath + ".bak"), true);
                }
            }
            // 删除老版本
            FileUtil.del(appRunnerPath);
        }
        FileUtil.mkdir(appRunnerPath);

        if (downloadFileName.endsWith(".jar")) {
            FileUtil.copy(downloadFile, new File(appRunnerPath), true);
        } else if (downloadFileName.endsWith(".zip")) {
            Util.unzip(downloadFile, appRunnerPath);
            File appFile = new File(appRunnerPath + "/app");
            if (!appFile.canExecute()) {
                appFile.setExecutable(true);
            }
        }
        FileUtil.del(downloadFile.getParentFile());
        log.info(getMessages("selfupdate.log.upgradeSuccess", appRunnerPath));
        return 0;
    }

    public LatestAppVersion getLatestAppVersion(String channel) {
        if (StrUtil.isBlank(channel) || "gitee".equalsIgnoreCase(channel)) {
            Map<String, Object> request = new HashMap<>();
            request.put("page", 1);
            request.put("per_page", 20);
            request.put("direction", "desc");
            ONode response = ApiUtils.getRequest("https://gitee.com/api/v5/repos/app-runner/app/releases", request);
            List<Map> list = response.toObjectList(Map.class);
            // 过滤预发布版本
            Map map = list.stream().filter(e -> {
                Object o = e.get("prerelease");
                return Boolean.FALSE.equals(o);
            }).findFirst().orElse(null);
            if (map == null) {
                return null;
            }
            LatestAppVersion latestAppVersion = new LatestAppVersion();
            String tagName = (String) map.get("tag_name");
            if (tagName.startsWith("v")) {
                tagName = tagName.substring(1);
            }
            latestAppVersion.setVersion(tagName);
            String createdAt = (String) map.get("created_at");
            latestAppVersion.setReleaseDate(createdAt);
            latestAppVersion.setReleaseNotes((String) map.get("body"));

            Object assetsObj = map.get("assets");
            if (assetsObj instanceof List) {
                List<Map<String, Object>> assets = (List) assetsObj;
                Map<String, Object> downloadMap = assets.stream().filter(e -> {
                    String browserDownloadUrl = (String) e.get("browser_download_url");
                    boolean isDownloadUrl = StrUtil.isNotBlank(browserDownloadUrl) && browserDownloadUrl.contains("download");
                    if (NativeDetector.inNativeImage()) {
                        if (Util.isWindows()) {
                            return isDownloadUrl && browserDownloadUrl.contains("win") && browserDownloadUrl.endsWith("zip");
                        }
                        if (Util.isMac()) {
                            return isDownloadUrl && browserDownloadUrl.contains("mac") && browserDownloadUrl.endsWith("zip");
                        }
                    }
                    // 其他系统，暂时只支持 Jar 包运行
                    return isDownloadUrl && browserDownloadUrl.endsWith("jar");
                }).findFirst().orElse(null);
                if (downloadMap == null) {
                    return null;
                }
                latestAppVersion.setDownloadUrl((String) downloadMap.get("browser_download_url"));
            }

            return latestAppVersion;
        } else if ("github".equalsIgnoreCase(channel)) {
            // TODO github
        }
        return null;
    }
}
