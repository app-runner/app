package com.github.dudiao.stm.cli.sub;

import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.persistence.ToolDO;
import com.github.dudiao.stm.persistence.ToolsPersistence;
import com.github.dudiao.stm.plugin.StmException;
import com.github.dudiao.stm.tools.AppHome;
import com.github.dudiao.stm.tools.JavaProcessExecutor;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/23 10:05
 */
@Slf4j
@Component
@CommandLine.Command(name = "run", description = "运行应用")
public class RunCli implements StmSubCli {

    @Inject
    private ToolsPersistence toolsPersistence;

    private final AppHome appHome = new AppHome();

    @CommandLine.Parameters(index = "0", description = "应用名称")
    private String name;

    @CommandLine.Parameters(index = "1..*", description = "运行参数")
    private String[] appParameters;

    @Override
    public Integer execute() {
        ToolDO toolDO = toolsPersistence.get(name);
        if (toolDO == null) {
            throw new StmException("应用不存在");
        }

        switch (toolDO.getAppType()) {
            case java -> {
                JavaProcessExecutor javaProcessExecutor = new JavaProcessExecutor(toolDO, appParameters);
                return javaProcessExecutor.run(appHome.getDir());
            }
            case shell -> {
                log.info("shell exe");
            }
            default -> throw new StmException("暂不支持该类型[%s]的程序运行".formatted(toolDO.getAppType()));
        }
        return 0;
    }

    @Override
    public CommandLine getCommandLine() {
        CommandLine commandLine = new CommandLine(this);
        // 未匹配的参数作为位置参数
        commandLine.setUnmatchedOptionsArePositionalParams(true);
        return commandLine;
    }
}
