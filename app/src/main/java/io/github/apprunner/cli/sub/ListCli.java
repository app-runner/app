package io.github.apprunner.cli.sub;

import io.github.apprunner.tools.Util;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.persistence.AppDO;
import io.github.apprunner.persistence.AppsPersistence;
import io.github.apprunner.tools.ConsoleTable;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.util.List;

/**
 * @author songyinyin
 * @since 2023/4/22 09:58
 */
@Slf4j
@Component
@CommandLine.Command(name = "list", description = "List all supported applications")
public class ListCli implements AppRunnerSubCli {

    @CommandLine.Option(names = {"-l", "--local"}, description = "Is only local applications listed")
    private boolean local;

    @Inject
    private AppsPersistence appsPersistence;

    @Override
    public Integer execute() {

        ConsoleTable consoleTable = ConsoleTable.create();
        consoleTable.addHeader("name", "version", "appType", "requiredVersion");
        List<AppDO> localList = appsPersistence.list();


        List<String> existAppIds = localList.stream().map(AppDO::getId).toList();
        for (AppDO appDO : localList) {
            consoleTable.addBody(appDO.getName() + "(local)", appDO.getVersion(), appDO.getAppType().getType(), fieldToString(appDO.getRequiredAppTypeVersionNum()));
        }
        if (!local) {
            List<AppDO> appDOS = Util.apiList(null);
            for (AppDO appDO : appDOS) {
                if (existAppIds.contains(appDO.getId())) {
                    continue;
                }
                consoleTable.addBody(appDO.getName(), appDO.getVersion(), appDO.getAppType().getType(), fieldToString(appDO.getRequiredAppTypeVersionNum()));
            }

        }
        log.info("\n{}", consoleTable);
        return 0;
    }

    private String fieldToString(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }
}
