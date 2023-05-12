package io.github.apprunner.cli;

import org.noear.solon.annotation.Component;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/21 19:03
 */
@Component
@CommandLine.Command(name = "stm", mixinStandardHelpOptions = true, versionProvider = StmVersionProvider.class, description = "应用集合")
public class StmCli implements Runnable {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        // if the command was invoked without subcommand, show the usage help
        spec.commandLine().usage(System.err);
    }
}
