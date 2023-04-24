package com.dudiao.stm.cli.sub;

import com.dudiao.stm.cli.StmSubCli;
import com.dudiao.stm.hotplugin.PluginManager;
import com.dudiao.stm.hotplugin.PluginPackage;
import com.dudiao.stm.persistence.PluginDO;
import com.dudiao.stm.persistence.PluginPersistence;
import com.dudiao.stm.plugin.StmException;
import com.dudiao.stm.plugin.StmPluginCli;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AopContext;
import picocli.CommandLine;

import java.io.File;

/**
 * @author songyinyin
 * @since 2023/4/23 10:05
 */
@Component
@CommandLine.Command(name = "run", description = "运行工具")
public class RunCli implements StmSubCli {

    @Inject
    private PluginPersistence pluginPersistence;

    @CommandLine.Parameters(index = "0", description = "工具名称")
    private String name;

    @CommandLine.Parameters(index = "1..*", description = "工具名称")
    private String[] pluginParameters;

    @Override
    public Integer execute() {
        PluginDO pluginDO = pluginPersistence.get(name);
        if (pluginDO == null) {
            throw new StmException("工具不存在");
        }

        PluginPackage pluginPackage = PluginManager.loadJar(new File(pluginDO.getJar())).start();
        AopContext context = pluginPackage.getContext();
        StmPluginCli stmPluginCli = context.getBean(StmPluginCli.class);

        if (pluginParameters == null) {
            pluginParameters = new String[0];
        }
        return new CommandLine(stmPluginCli).execute(pluginParameters);
    }

    @Override
    public CommandLine getCommandLine() {
        CommandLine commandLine = new CommandLine(this);
        // 未匹配的参数作为位置参数
        commandLine.setUnmatchedOptionsArePositionalParams(true);
        return commandLine;
    }
}
