package io.github.apprunner.cli;

import org.noear.solon.annotation.Component;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/21 19:03
 */
@Component
@CommandLine.Command(name = "app", mixinStandardHelpOptions = true, versionProvider = AppRunnerVersionProvider.class, description = "app-runner is a tool for managing applications")
public class AppRunnerCli implements Runnable {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        // if the command was invoked without subcommand, show the usage help
        spec.commandLine().usage(System.err);
    }
}
