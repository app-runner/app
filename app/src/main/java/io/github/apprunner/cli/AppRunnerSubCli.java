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
public abstract class AppRunnerSubCli implements Callable<Integer> {

    @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true, description = "Display this help message.")
    public boolean help;

    @Override
    public Integer call() {
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

    /**
     * 执行子命令
     *
     * @return 0 成功，非 0 失败
     */
    protected abstract Integer execute() throws Exception;

    public CommandLine getCommandLine() {
        CommandLine commandLine = new CommandLine(this);
        // 未匹配的参数作为位置参数
        commandLine.setUnmatchedOptionsArePositionalParams(true);
        commandLine.setUnmatchedArgumentsAllowed(true);
        commandLine.setUnmatchedOptionsAllowedAsOptionParameters(true);
        return commandLine;
    }

}
