package io.github.apprunner.cli;

import io.github.apprunner.plugin.AppRunnerException;
import io.github.apprunner.tools.AppRunnerContext;
import io.github.apprunner.tools.ReentrantStopWatch;
import org.noear.solon.core.util.LogUtil;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author songyinyin
 * @since 2023/4/21 22:24
 */
public interface AppRunnerSubCli extends Callable<Integer> {

    default Integer call() {
        try {
            ReentrantStopWatch stopWatch = AppRunnerContext.getStopWatch();
            stopWatch.start(this.getClass().getSimpleName());
            Integer execute = execute();
            stopWatch.stop();
            return execute;
        } catch (Exception e) {
            if (e instanceof AppRunnerException ae) {
                LogUtil.global().error(ae.getMessage(), ae.getException());
                return ae.getExitCode();
            }
            LogUtil.global().error(e.getMessage(), e);
            return -1;
        }
    }

    Integer execute() throws Exception;

    default CommandLine getCommandLine() {
        CommandLine commandLine = new CommandLine(this);
        // 未匹配的参数作为位置参数
        commandLine.setUnmatchedOptionsArePositionalParams(true);
        commandLine.setUnmatchedArgumentsAllowed(true);
        commandLine.setUnmatchedOptionsAllowedAsOptionParameters(true);
        return commandLine;
    }

}
