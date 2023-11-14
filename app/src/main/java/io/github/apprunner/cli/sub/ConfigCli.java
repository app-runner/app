package io.github.apprunner.cli.sub;

import cn.hutool.core.util.StrUtil;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.cli.support.AppNameCandidates;
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
@CommandLine.Command(name = "config", description = "config app runtime path and self configuration")
public class ConfigCli extends AppRunnerSubCli {

    @Inject
    private AppPersistence appPersistence;
    @Inject
    private InfoCli infoCli;

    @CommandLine.Parameters(index = "0", description = "application name", completionCandidates = AppNameCandidates.class)
    private String name;

    @CommandLine.Option(names = {"-r", "--runtime"}, description = "app runtime path. java app is java home path, shell app is shell path")
    private String runtimePath;

    @CommandLine.Option(names = {"-p", "--programArguments"}, description = "java app program arguments")
    private String programArguments;

    @CommandLine.Option(names = {"-j", "--jvmArguments"}, description = "java app jvm arguments")
    private String jvmArguments;

    @CommandLine.Parameters(index = "1..*", description = "app configuration")
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
        log.info("Application [{}] config successfully", name);

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
