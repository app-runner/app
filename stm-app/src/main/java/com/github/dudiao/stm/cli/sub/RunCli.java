package com.github.dudiao.stm.cli.sub;

import cn.hutool.core.util.StrUtil;
import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.persistence.StmAppDO;
import com.github.dudiao.stm.persistence.AppsPersistence;
import com.github.dudiao.stm.plugin.StmException;
import com.github.dudiao.stm.tools.AppHome;
import com.github.dudiao.stm.tools.JavaProcessExecutor;
import com.github.dudiao.stm.tools.StmUtils;
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
    private AppsPersistence appsPersistence;

    private final AppHome appHome = new AppHome();

    @CommandLine.Parameters(index = "0", description = "应用名称")
    private String name;

    @CommandLine.Parameters(index = "1..*", description = "运行参数")
    private String[] appParameters;

    @Override
    public Integer execute() {
        StmAppDO stmAppDO = appsPersistence.getUsed(name);
        if (stmAppDO == null) {
            throw new StmException("应用不存在");
        }

        switch (stmAppDO.getAppType()) {
            case java -> {
                if (StrUtil.isBlank(stmAppDO.getAppRuntimePath())) {
                    stmAppDO.setAppRuntimePath(StmUtils.getJavaHome(stmAppDO.getRequiredAppTypeVersionNum()));
                }
                JavaProcessExecutor javaProcessExecutor = new JavaProcessExecutor(stmAppDO, appParameters);
                return javaProcessExecutor.run(appHome.getDir());
            }
            case shell -> {
                log.info("shell exe");
            }
            default -> throw new StmException("暂不支持该类型[%s]的程序运行".formatted(stmAppDO.getAppType()));
        }
        return 0;
    }

}
