package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import io.github.apprunner.tools.StmUtils;
import io.github.apprunner.cli.StmSubCli;
import io.github.apprunner.persistence.StmAppDO;
import io.github.apprunner.persistence.AppsPersistence;
import io.github.apprunner.plugin.StmException;
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
@CommandLine.Command(name = "uninstall", description = "Uninstalling apps")
public class UninstallCli implements StmSubCli {

    @CommandLine.Parameters(index = "0", description = "application name")
    private String name;

    @Inject
    private AppsPersistence appsPersistence;

    @Override
    public Integer execute() {
        StmAppDO stmAppDO = appsPersistence.getUsed(name);
        if (stmAppDO == null) {
            throw new StmException("The application [%s] does not exist".formatted(name));
        }
        String appPath = StmUtils.getAppPath(stmAppDO);
        appsPersistence.remove(name);
        FileUtil.del(appPath);
        log.info("delete file: {}", appPath);
        log.info("Application [{}] uninstalled successfully", name);
        return null;
    }
}
