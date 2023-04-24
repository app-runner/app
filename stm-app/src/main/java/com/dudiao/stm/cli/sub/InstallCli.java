package com.dudiao.stm.cli.sub;

import com.dudiao.stm.cli.StmSubCli;
import com.dudiao.stm.persistence.PluginDO;
import com.dudiao.stm.persistence.PluginPersistence;
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
public class InstallCli implements StmSubCli {

    @Inject
    private PluginPersistence pluginPersistence;

    @CommandLine.Parameters(index = "0", description = "工具名称")
    private String name;

    @CommandLine.Option(names = {"-p", "--path"}, description = "本地")
    private File path;

    @Override
    public Integer execute() {
        if (path != null) {
            PluginDO pluginDO = new PluginDO();
            pluginDO.setName(name);
            pluginDO.setJar(path.getAbsolutePath());
            pluginPersistence.add(pluginDO);
        }
        return 0;
    }
}
