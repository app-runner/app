package io.github.apprunner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.date.StopWatch;
import io.github.apprunner.cli.AppRunnerCli;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.tools.AppRunnerContext;
import io.github.apprunner.tools.AppRunnerUtils;
import io.github.apprunner.tools.StopWatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.utils.LogUtilToSlf4j;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;

@Slf4j
@SolonMain
public class App {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch("AppRunner");
        AppRunnerContext.setStopWatch(stopWatch);
        stopWatch.start("AppRunner start");
        // 设置日志级别
        setLogLevel(args);

        // 启动应用
        Solon.start(App.class, args);
        stopWatch.stop();

        // apprunner cli
        stopWatch.start("AppRunnerCli init");
        AppRunnerCli appRunnerCli = Solon.context().getBean(AppRunnerCli.class);
        CommandLine commandLine = new CommandLine(appRunnerCli);
        List<AppRunnerSubCli> appRunnerSubClis = Solon.context().getBeansOfType(AppRunnerSubCli.class);
        for (AppRunnerSubCli appRunnerSubCli : appRunnerSubClis) {
            commandLine.addSubcommand(appRunnerSubCli.getCommandLine());
        }
        stopWatch.stop();
        int execute = commandLine.execute(args);
        if (AppRunnerUtils.isDebugMode()) {
            log.info("time：{} ms, {}", stopWatch.getTotalTimeMillis(), StopWatchUtil.prettyPrint(stopWatch));
        }
        if (!NativeDetector.isAotRuntime()) {
            Solon.stopBlock(true, -1, execute);
        }
    }

    private static void setLogLevel(String[] args) {
        for (String arg : args) {
            if (arg.contains("debug=1") && !arg.contains("apprunner.debug=1")) {
                return;
            }
        }
        LogUtil.globalSet(new LogUtilToSlf4j());
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("org.noear.solon.Solon");
        logger.setLevel(Level.valueOf("warn"));
    }
}