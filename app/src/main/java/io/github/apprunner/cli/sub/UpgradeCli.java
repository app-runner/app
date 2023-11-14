package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.cli.support.AppNameCandidates;
import io.github.apprunner.persistence.AppPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.tools.ApiUtils;
import io.github.apprunner.tools.DownloadStreamProgress;
import io.github.apprunner.tools.Util;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.io.File;

/**
 * 升级应用到最新版本
 *
 * @author songyinyin
 * @since 2023/4/22 16:35
 */
@Slf4j
@Component
@CommandLine.Command(name = "upgrade", description = "Upgrade application")
public class UpgradeCli extends AppRunnerSubCli {


    @CommandLine.Parameters(index = "0", description = "application name", completionCandidates = AppNameCandidates.class)
    private String name;


    @CommandLine.Option(names = {"-v", "--version"}, description = "Specify the version number for the upgrade, if not specified, the latest version is used")
    private String version;

    @Inject
    private AppPersistence appPersistence;

    @Override
    public Integer execute() {
        AppDO currApp = appPersistence.getUsed(name);

        AppDO appDO = ApiUtils.apiLatestVersion(name, version);
        if (StrUtil.isBlank(version)) {
            if (StrUtil.equals(appDO.getAppLatestVersion().getVersion(), currApp.getVersion())) {
                log.info("The application [{}] is already the latest version and does not need to be upgraded", name);
                return 0;
            }
            log.info("Latest version of application [{}]: {}, current version: {}", name, appDO.getAppLatestVersion().getVersion(), currApp.getVersion());
        }

        String url = StrUtil.isNotBlank(appDO.getAppLatestVersion().getGithubDownloadUrl()) ? appDO.getAppLatestVersion().getGithubDownloadUrl() : appDO.getAppLatestVersion().getGiteeDownloadUrl();

        File downloadFile = HttpUtil.downloadFileFromUrl(url, FileUtil.mkdir(Util.getAppPath(appDO)), new DownloadStreamProgress());
        appDO.setAppPath(downloadFile.getAbsolutePath());
        appDO.setUsed(true);
        appPersistence.add(appDO);
        log.info("Application [{}] upgraded successfully", name);
        return 0;
    }

}
