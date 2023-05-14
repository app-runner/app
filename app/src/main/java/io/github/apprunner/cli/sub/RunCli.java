package io.github.apprunner.cli.sub;

import cn.hutool.core.util.StrUtil;
import io.github.apprunner.persistence.AppsPersistence;
import io.github.apprunner.persistence.AppDO;
import io.github.apprunner.tools.AppHome;
import io.github.apprunner.tools.JavaProcessExecutor;
import io.github.apprunner.tools.AppRunnerUtils;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.plugin.AppRunnerException;
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
@CommandLine.Command(name = "run", description = "Running Application")
public class RunCli implements AppRunnerSubCli {

    @Inject
    private AppsPersistence appsPersistence;

    private final AppHome appHome = new AppHome();

    @CommandLine.Parameters(index = "0", description = "application name")
    private String name;

    @CommandLine.Parameters(index = "1..*", description = "run parameters")
    private String[] appParameters;

    @Override
    public Integer execute() {
        AppDO appDO = appsPersistence.getUsed(name);
        if (appDO == null) {
            throw new AppRunnerException("The application [%s] does not exist".formatted(name));
        }

        switch (appDO.getAppType()) {
            case java -> {
                if (StrUtil.isBlank(appDO.getAppRuntimePath())) {
                    appDO.setAppRuntimePath(AppRunnerUtils.getJavaHome(appDO.getRequiredAppTypeVersionNum()));
                }
                JavaProcessExecutor javaProcessExecutor = new JavaProcessExecutor(appDO, appParameters);
                return javaProcessExecutor.run(appHome.findDefaultHomeDir());
            }
            case shell -> {
                log.info("shell exe");
            }
            default -> throw new AppRunnerException("The program running of this type [%s] is currently not supported".formatted(appDO.getAppType()));
        }
        return 0;
    }

}
