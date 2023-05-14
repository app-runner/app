package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.cli.StmSubCli;
import io.github.apprunner.persistence.AppsPersistence;
import io.github.apprunner.persistence.StmAppDO;
import io.github.apprunner.plugin.StmException;
import io.github.apprunner.tools.DownloadStreamProgress;
import io.github.apprunner.tools.StmUtils;
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
public class UpgradeCli implements StmSubCli {


    @CommandLine.Parameters(index = "0", description = "application name")
    private String name;


    @CommandLine.Option(names = {"-v", "--version"}, description = "Specify the version number for the upgrade, if not specified, the latest version is used")
    private String version;

    @Inject
    private AppsPersistence appsPersistence;

    @Override
    public Integer execute() {
        StmAppDO currApp = appsPersistence.getUsed(name);
        if (currApp == null) {
            throw new StmException("The application [%s] does not exist".formatted(name));
        }
        StmAppDO stmAppDO = StmUtils.apiLatestVersion(name, version);
        if (StrUtil.isBlank(version)) {
            if (StrUtil.equals(stmAppDO.getAppLatestVersion().getVersion(), currApp.getVersion())) {
                log.info("The application [{}] is already the latest version and does not need to be upgraded", name);
                return 0;
            }
            log.info("Latest version of application [{}]: {}, current version: {}", name, stmAppDO.getAppLatestVersion().getVersion(), currApp.getVersion());
        }

        File downloadFile = HttpUtil.downloadFileFromUrl(stmAppDO.getAppLatestVersion().getGithubDownloadUrl(), FileUtil.mkdir(StmUtils.getAppPath(stmAppDO)), new DownloadStreamProgress());
        stmAppDO.setToolAppPath(downloadFile.getAbsolutePath());
        appsPersistence.add(stmAppDO);
        log.info("Application [{}] upgraded successfully", name);
        return 0;
    }

}
