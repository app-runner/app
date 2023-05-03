package com.github.dudiao.stm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.date.StopWatch;
import com.github.dudiao.stm.cli.StmCli;
import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.tools.StmContext;
import com.github.dudiao.stm.tools.StmUtils;
import com.github.dudiao.stm.tools.StopWatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.core.NativeDetector;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.utils.LogUtilToSlf4j;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;

@Slf4j
@SolonMain
public class App {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch("STM");
        StmContext.setStopWatch(stopWatch);
        stopWatch.start("StmApp start");
        // 设置日志级别
        setLogLevel(args);

        // 启动应用
        Solon.start(App.class, args);
        stopWatch.stop();

        // stm cli
        stopWatch.start("StmCli init");
        StmCli stmCli = Solon.context().getBean(StmCli.class);
        CommandLine commandLine = new CommandLine(stmCli);
        List<StmSubCli> stmSubClis = Solon.context().getBeansOfType(StmSubCli.class);
        for (StmSubCli stmSubCli : stmSubClis) {
            commandLine.addSubcommand(stmSubCli.getCommandLine());
        }
        stopWatch.stop();
        int execute = commandLine.execute(args);
        if (StmUtils.isDebugMode()) {
            log.info("执行耗时：{} ms, {}", stopWatch.getTotalTimeMillis(), StopWatchUtil.prettyPrint(stopWatch));
        }
        if (!NativeDetector.isAotRuntime()) {
            Solon.stopBlock(true, -1, execute);
        }
    }

    private static void setLogLevel(String[] args) {
        for (String arg : args) {
            if (arg.contains("debug=1") && !arg.contains("stm.debug=1")) {
                return;
            }
        }
        LogUtil.globalSet(new LogUtilToSlf4j());
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("org.noear.solon.Solon");
        logger.setLevel(Level.valueOf("warn"));
    }
}