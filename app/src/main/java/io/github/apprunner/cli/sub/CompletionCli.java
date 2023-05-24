package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.tools.AppRunnerUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author songyinyin
 * @since 2023/5/23 18:36
 */
@Slf4j
@Component
@CommandLine.Command(name = "completion", description = "Generate completion script")
public class CompletionCli implements AppRunnerSubCli {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;


    @Override
    public Integer execute() {
        String script = AutoComplete.bash(
            spec.parent().name(),
            spec.parent().commandLine());
        String filePath = AppRunnerUtils.getAppHome() + File.separator + spec.parent().name() + "_completion";

        FileUtil.writeString(script, filePath, Charset.defaultCharset());
        log.info("Generate completion script successfully, path: {}", filePath);
        log.info("You need to execute the following command to enable completion in current shell: `source %s`".formatted(filePath));
        log.info("If you want to enable completion permanently, you need to add `source %s` to your shell profile(~/.bash_profile or ~/.zshrc)".formatted(filePath));
        return 0;
    }
}
