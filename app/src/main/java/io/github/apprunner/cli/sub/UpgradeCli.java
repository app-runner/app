package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.github.apprunner.cli.AppRelatedCli;
import io.github.apprunner.persistence.AppPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.tools.ApiUtils;
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
@CommandLine.Command(name = "upgrade", description = "${bundle:upgrade.description}")
public class UpgradeCli extends AppRelatedCli {

    @CommandLine.Option(names = {"-v", "--version"}, description = "${bundle:upgrade.parameter.version}")
    private String version;

    @Inject
    private AppPersistence appPersistence;

    @Override
    public Integer execute() {
        AppDO currApp = appPersistence.getUsed(name);

        AppDO appDO = ApiUtils.apiLatestVersion(name, version);
        if (StrUtil.isBlank(version)) {
            if (StrUtil.equals(appDO.getAppLatestVersion().getVersion(), currApp.getVersion())) {
                log.info(getMessages("upgrade.log.noUpgradeRequired", name));
                return 0;
            }
            log.info(getMessages("upgrade.log.version", name, appDO.getAppLatestVersion().getVersion(), currApp.getVersion()));
        }

        String url = StrUtil.isNotBlank(appDO.getAppLatestVersion().getGithubDownloadUrl()) ? appDO.getAppLatestVersion().getGithubDownloadUrl() : appDO.getAppLatestVersion().getGiteeDownloadUrl();

        File downloadFile = ApiUtils.downloadFile(url, FileUtil.mkdir(Util.getAppPath(appDO)));
        appDO.setAppPath(downloadFile.getAbsolutePath());
        appDO.setUsed(true);
        appPersistence.add(appDO);
        log.info(getMessages("upgrade.log.success", name));
        return 0;
    }

}
