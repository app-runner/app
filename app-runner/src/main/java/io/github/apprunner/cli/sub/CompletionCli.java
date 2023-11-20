package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.tools.Util;
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
@CommandLine.Command(name = "completion", description = "${bundle:completion.description}")
public class CompletionCli extends AppRunnerSubCli {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;


    @Override
    public Integer execute() {
        String script = AutoComplete.bash(
            spec.parent().name(),
            spec.parent().commandLine());
        String filePath = Util.getAppHome() + File.separator + spec.parent().name() + "_completion";

        FileUtil.writeString(script, filePath, Charset.defaultCharset());

        log.info(getMessages("completion.log.success1", filePath));
        log.info(getMessages("completion.log.success2", filePath));
        log.info(getMessages("completion.log.success3", filePath));
        return 0;
    }
}
