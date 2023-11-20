package io.github.apprunner.cli.sub;

import cn.hutool.core.util.StrUtil;
import io.github.apprunner.cli.AppRelatedCli;
import io.github.apprunner.persistence.AppPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import io.github.apprunner.tools.AppHome;
import io.github.apprunner.tools.JavaProcessExecutor;
import io.github.apprunner.tools.Util;
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
@CommandLine.Command(name = "run", description = "${bundle:run.description}")
public class RunCli extends AppRelatedCli {

    @Inject
    private AppPersistence appPersistence;

    private final AppHome appHome = new AppHome();

    @CommandLine.Parameters(index = "1..*", description = "${bundle:run.parameter.appParameters}")
    private String[] appParameters;

    @Override
    public Integer execute() {
        AppDO appDO = appPersistence.getUsed(name);

        switch (appDO.getAppType()) {
            case java -> {
                if (StrUtil.isBlank(appDO.getAppRuntimePath())) {
                    appDO.getJavaParams().setJavaHome(Util.getJavaHome(appDO.getRequiredAppTypeVersionNum()));
                }
                JavaProcessExecutor javaProcessExecutor = new JavaProcessExecutor(appDO, appParameters);
                return javaProcessExecutor.run(appHome.findDefaultHomeDir());
            }
            case shell -> {
                log.info("shell exe");
            }
            default ->
                throw new AppRunnerException("The program running of this type [%s] is currently not supported".formatted(appDO.getAppType()));
        }
        return 0;
    }

}
