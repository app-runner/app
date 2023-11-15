package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import io.github.apprunner.cli.AppRelatedCli;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.cli.support.AppNameCandidates;
import io.github.apprunner.persistence.AppPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.tools.Util;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/30 20:00
 */
@Slf4j
@Component
@CommandLine.Command(name = "uninstall", description = "${bundle:uninstall.description}")
public class UninstallCli extends AppRelatedCli {

    @Inject
    private AppPersistence appPersistence;

    @Override
    public Integer execute() {
        AppDO appDO = appPersistence.getUsed(name);

        String appPath = Util.getAppPath(appDO);
        appPersistence.remove(name);
        FileUtil.del(appPath);
        log.info(getMessages("uninstall.log.delete", appPath));
        log.info(getMessages("uninstall.log.success", name));
        return null;
    }
}
