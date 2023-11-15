package io.github.apprunner.cli;

import io.github.apprunner.cli.support.AppNameCandidates;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/11/15 14:21
 */
public abstract class AppRelatedCli extends AppRunnerSubCli {

    @CommandLine.Parameters(index = "0", description = "${bundle:parameter.app.name}", completionCandidates = AppNameCandidates.class)
    protected String name;

}
