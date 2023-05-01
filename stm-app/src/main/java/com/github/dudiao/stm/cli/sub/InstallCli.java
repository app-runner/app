package com.github.dudiao.stm.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.persistence.ApplicationType;
import com.github.dudiao.stm.persistence.ToolDO;
import com.github.dudiao.stm.persistence.ToolsPersistence;
import com.github.dudiao.stm.plugin.StmException;
import com.github.dudiao.stm.tools.StmUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.io.File;

/**
 * @author songyinyin
 * @since 2023/4/22 16:35
 */
@Slf4j
@Component
@CommandLine.Command(name = "install", description = "安装应用")
public class InstallCli implements StmSubCli {


    @CommandLine.Parameters(index = "0", description = "应用名称")
    private String name;

    @CommandLine.Option(names = {"-p", "--path"}, description = "本地应用文件路径")
    private File path;

    @CommandLine.Option(names = {"-v", "--version"}, defaultValue = "local_version", description = "版本号")
    private String version;

    @Inject
    private ToolsPersistence toolsPersistence;

    @Override
    public Integer execute() {
        if (path != null) {
            ToolDO toolDO = new ToolDO();
            toolDO.setName(name);
            toolDO.setAppType(getAppType(path));
            toolDO.setVersion(version);
            String installedAppPath = StmUtils.getAppPath(toolDO) + "/" + path.getName();
            File copy = FileUtil.copy(path, new File(installedAppPath), true);
            log.info("将应用[{}]复制到：{}", name, copy.getAbsolutePath());
            toolDO.setToolAppPath(copy.getAbsolutePath());
            toolsPersistence.add(toolDO);
            log.info("应用安装[{}]成功", name);
        }
        return 0;
    }

    private ApplicationType getAppType(File file) {
        String suffix = FileUtil.getSuffix(file);
        ApplicationType[] values = ApplicationType.values();
        for (ApplicationType applicationType : values) {
            for (String typeSuffix : applicationType.getSuffix().split(",")) {
                if (StrUtil.equalsIgnoreCase(suffix, typeSuffix)) {
                    return applicationType;
                }
            }
        }
        throw new StmException("不支持的文件后缀");
    }
}
