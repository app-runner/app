package com.dudiao.stm.cli.sub;

import com.dudiao.stm.persistence.StmPlugin;
import com.dudiao.stm.persistence.StmPluginManager;
import com.dudiao.stm.plugin.StmSubCli;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.io.File;

/**
 * @author songyinyin
 * @since 2023/4/22 16:35
 */
@Component
@CommandLine.Command(name = "install", description = "安装工具")
public class InstallStmSubCli implements StmSubCli {

    @Inject
    private StmPluginManager stmPluginManager;

    @CommandLine.Parameters(index = "0", description = "工具名称")
    private String name;

    @CommandLine.Option(names = {"-p", "--path"}, description = "本地")
    private File path;

    @Override
    public void execute() {
        if (path != null) {
            StmPlugin stmPlugin = StmPlugin.builder().name(name).jar(path.getAbsolutePath()).build();
            stmPluginManager.add(stmPlugin);
        }
    }
}
