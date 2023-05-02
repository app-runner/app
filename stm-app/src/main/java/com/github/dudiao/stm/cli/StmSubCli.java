package com.github.dudiao.stm.cli;

import com.github.dudiao.stm.plugin.StmException;
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
            return execute();
        } catch (StmException e) {
            LogUtil.global().error(e.getMessage());
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
