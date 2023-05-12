package io.github.apprunner.cli.sub;

import io.github.apprunner.tools.StmUtils;
import io.github.apprunner.cli.StmSubCli;
import io.github.apprunner.persistence.StmAppDO;
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
@CommandLine.Command(name = "list", description = "列出所有的支持的应用")
public class ListCli implements StmSubCli {

    @CommandLine.Option(names = {"-l", "--local"}, description = "是否只列出本地的应用")
    private boolean local;

    @Inject
    private AppsPersistence appsPersistence;

    @Override
    public Integer execute() {

        ConsoleTable consoleTable = ConsoleTable.create();
        consoleTable.addHeader("name", "version", "appType", "requiredVersion");
        List<StmAppDO> localList = appsPersistence.list();


        List<String> existAppIds = localList.stream().map(StmAppDO::getId).toList();
        for (StmAppDO stmAppDO : localList) {
            consoleTable.addBody(stmAppDO.getName() + "(local)", stmAppDO.getVersion(), stmAppDO.getAppType().getType(), fieldToString(stmAppDO.getRequiredAppTypeVersionNum()));
        }
        if (!local) {
            List<StmAppDO> stmAppDOS = StmUtils.apiList(null);
            for (StmAppDO stmAppDO : stmAppDOS) {
                if (existAppIds.contains(stmAppDO.getId())) {
                    continue;
                }
                consoleTable.addBody(stmAppDO.getName(), stmAppDO.getVersion(), stmAppDO.getAppType().getType(), fieldToString(stmAppDO.getRequiredAppTypeVersionNum()));
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
