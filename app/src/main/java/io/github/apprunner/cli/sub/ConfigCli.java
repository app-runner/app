package io.github.apprunner.cli.sub;

import cn.hutool.core.util.StrUtil;
import io.github.apprunner.cli.AppRelatedCli;
import io.github.apprunner.persistence.AppPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import io.github.apprunner.tools.JavaExecutable;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.io.IOException;

/**
 * 配置应用
 *
 * @author songyinyin
 * @since 2023/11/2 17:12
 */
@Slf4j
@Component
@CommandLine.Command(name = "config", description = "${bundle:config.description}")
public class ConfigCli extends AppRelatedCli {

    @Inject
    private AppPersistence appPersistence;
    @Inject
    private InfoCli infoCli;

    @CommandLine.Option(names = {"-r", "--runtime"}, description = "${bundle:config.parameter.runtime}")
    private String runtimePath;

    @CommandLine.Option(names = {"-p", "--programArguments"}, description = "${bundle:config.parameter.programArguments}")
    private String programArguments;

    @CommandLine.Option(names = {"-j", "--jvmArguments"}, description = "${bundle:config.parameter.jvmArguments}")
    private String jvmArguments;

    @CommandLine.Parameters(index = "1..*", description = "${bundle:config.parameter.appConfigurations}")
    private String[] appConfigurations;

    @Override
    public Integer execute() throws IOException {
        AppDO used = appPersistence.getUsed(name);
        AppDO.JavaDO java = used.getJavaParams();
        if (StrUtil.isNotBlank(runtimePath)) {
            java.setJavaHome(runtimePath);
            validateRuntimePath(used);
        }
        if (StrUtil.isNotBlank(programArguments)) {
            java.setProgramArguments(programArguments);
        }
        if (StrUtil.isNotBlank(jvmArguments)) {
            java.setJvmArguments(jvmArguments);
        }

        appPersistence.update(used);
        log.info(getMessages("config.log.success", name));

        infoCli.printAppInfo(name);

        return 0;
    }

    private void validateRuntimePath(AppDO app) {
        switch (app.getAppType()) {
            case java -> {
                JavaExecutable javaExecutable = new JavaExecutable(app.getAppRuntimePath());
                if (!javaExecutable.canExecute()) {
                    throw new AppRunnerException("java can't execute in " + javaExecutable);
                }
            }
        }
    }
}
