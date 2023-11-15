package io.github.apprunner.cli;

import io.github.apprunner.tools.Util;
import org.noear.solon.annotation.Component;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/21 19:03
 */
@Component
@CommandLine.Command(name = "app", mixinStandardHelpOptions = true, versionProvider = AppRunnerVersionProvider.class, description = "${bundle:app.description}")
public class AppRunnerCli implements Runnable {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {
        "--debug", "--app.debug" }, description = "${bundle:parameter.debug}", scope = CommandLine.ScopeType.INHERIT)
    void setDebug(String debug) {
        Util.setDebugMode("1".equals(debug));
    }

    @Override
    public void run() {
        // if the command was invoked without subcommand, show the usage help
        spec.commandLine().usage(System.err);
    }
}
