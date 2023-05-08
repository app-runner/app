package com.github.dudiao.stm.cli.sub;

import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.persistence.StmAppDO;
import com.github.dudiao.stm.persistence.AppsPersistence;
import com.github.dudiao.stm.tools.ConsoleTable;
import com.github.dudiao.stm.tools.StmUtils;
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
                String name = existAppIds.contains(stmAppDO.getId()) ? stmAppDO.getName() + "(local)" : stmAppDO.getName();
                consoleTable.addBody(name, stmAppDO.getVersion(), stmAppDO.getAppType().getType(), fieldToString(stmAppDO.getRequiredAppTypeVersionNum()));
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
