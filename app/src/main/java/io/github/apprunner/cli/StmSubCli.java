package io.github.apprunner.cli;

import cn.hutool.core.date.StopWatch;
import io.github.apprunner.tools.StmContext;
import io.github.apprunner.plugin.StmException;
import org.noear.solon.core.util.LogUtil;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author songyinyin
 * @since 2023/4/21 22:24
 */
public interface StmSubCli extends Callable<Integer> {

    default Integer call() {
        try {
            StopWatch stopWatch = StmContext.getStopWatch();
            stopWatch.start(this.getClass().getSimpleName());
            Integer execute = execute();
            stopWatch.stop();
            return execute;
        } catch (StmException e) {
            LogUtil.global().error(e.getMessage());
            if (e.getException() != null) {
                e.printStackTrace();
            }
            return e.getExitCode();
        }
    }

    Integer execute();

    default CommandLine getCommandLine() {
        CommandLine commandLine = new CommandLine(this);
        // 未匹配的参数作为位置参数
        commandLine.setUnmatchedOptionsArePositionalParams(true);
        commandLine.setUnmatchedArgumentsAllowed(true);
        commandLine.setUnmatchedOptionsAllowedAsOptionParameters(true);
        return commandLine;
    }

}
