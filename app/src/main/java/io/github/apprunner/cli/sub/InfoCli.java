package io.github.apprunner.cli.sub;

import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.cli.support.AppNameCandidates;
import io.github.apprunner.persistence.AppPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.persistence.entity.ApplicationType;
import io.github.apprunner.tools.ConsoleTable;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author songyinyin
 * @since 2023/11/2 17:12
 */
@Slf4j
@Component
@CommandLine.Command(name = "info", description = "View current used application information")
public class InfoCli extends AppRunnerSubCli {

    @Inject
    private AppPersistence appPersistence;

    @CommandLine.Parameters(index = "0", description = "application name", completionCandidates = AppNameCandidates.class)
    private String name;

    @Override
    public Integer execute() throws IOException {
        printAppInfo(name);
        return 0;
    }

    public void printAppInfo(String appName) {
        AppDO used = appPersistence.getUsed(appName);
        Map<String, String> info = new LinkedHashMap<>();
        info.put("name", used.getName());
        info.put("type", used.getAppType().getType());
        info.put("version", used.getVersion());
        info.put("description", used.getDescription());
        info.put("appPath", used.getAppPath());
        if (ApplicationType.java.equals(used.getAppType())) {
            info.put("JAVA_HOME", used.getJavaParams().getJavaHome());
            info.put("programArguments", used.getJavaParams().getProgramArguments());
            info.put("jvmArguments", used.getJavaParams().getJvmArguments());
        }

        log.info("\n" + ConsoleTable.toDetailInfo(info));
    }
}
