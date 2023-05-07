package com.github.dudiao.stm.cli.sub;

import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.persistence.ToolDO;
import com.github.dudiao.stm.persistence.ToolsPersistence;
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
    private ToolsPersistence toolsPersistence;

    @Override
    public Integer execute() {

        ConsoleTable consoleTable = ConsoleTable.create();
        consoleTable.addHeader("name", "version", "appType", "requiredVersion");
        List<ToolDO> localList = toolsPersistence.list();


        List<String> existAppIds = localList.stream().map(ToolDO::getId).toList();
        for (ToolDO toolDO : localList) {
            consoleTable.addBody(toolDO.getName() + "(local)", toolDO.getVersion(), toolDO.getAppType().getType(), fieldToString(toolDO.getRequiredAppTypeVersionNum()));
        }
        if (!local) {
            List<ToolDO> toolDOS = StmUtils.apiList(null);
            for (ToolDO toolDO : toolDOS) {
                String name = existAppIds.contains(toolDO.getId()) ? toolDO.getName() + "(local)" : toolDO.getName();
                consoleTable.addBody(name, toolDO.getVersion(), toolDO.getAppType().getType(), fieldToString(toolDO.getRequiredAppTypeVersionNum()));
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
