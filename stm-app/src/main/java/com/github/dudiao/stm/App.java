package com.github.dudiao.stm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.github.dudiao.stm.cli.StmCli;
import com.github.dudiao.stm.cli.StmSubCli;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.utils.LogUtilToSlf4j;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;

@SolonMain
public class App {

    public static void main(String[] args) {
        // 设置日志级别
        setLogLevel(args);

        // 启动应用
        Solon.start(App.class, args);

        // stm cli
        StmCli stmCli = Solon.context().getBean(StmCli.class);
        CommandLine commandLine = new CommandLine(stmCli);
        List<StmSubCli> stmSubClis = Solon.context().getBeansOfType(StmSubCli.class);
        for (StmSubCli stmSubCli : stmSubClis) {
            commandLine.addSubcommand(stmSubCli.getCommandLine());
        }
        commandLine.execute(args);
    }

    private static void setLogLevel(String[] args) {
        for (String arg : args) {
            if (arg.contains("debug=1")) {
                return;
            }
        }
        LogUtil.globalSet(new LogUtilToSlf4j());
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("org.noear.solon.Solon");
        logger.setLevel(Level.valueOf("warn"));
    }
}