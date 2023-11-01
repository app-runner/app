package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.tools.ApiUtils;
import io.github.apprunner.tools.DownloadStreamProgress;
import io.github.apprunner.tools.Util;
import io.github.apprunner.cli.AppRunnerSubCli;
import io.github.apprunner.persistence.entity.ApplicationType;
import io.github.apprunner.persistence.AppsPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.io.File;

/**
 * @author songyinyin
 * @since 2023/4/22 16:35
 */
@Slf4j
@Component
@CommandLine.Command(name = "install", description = "Installing Applications")
public class InstallCli implements AppRunnerSubCli {


    @CommandLine.Parameters(index = "0", description = "application name")
    private String name;

    @CommandLine.Option(names = {"-p", "--path"}, description = "Local application file path")
    private File path;

    @CommandLine.Option(names = {"-rv", "--requiredVersion"}, description = "The minimum version of the application to run, for example, Java applications need to specify the Java version, 17")
    private Long requiredVersion;

    @CommandLine.Option(names = {"-v", "--version"}, defaultValue = "local_version", description = "version")
    private String version;

    @Inject
    private AppsPersistence appsPersistence;

    @Override
    public Integer execute() {
        AppDO appDO;
        if (path != null) {
            appDO = localInstall();
        } else {
            appsPersistence.existAndThrow(name);
            appDO = ApiUtils.apiLatestVersion(name, null);
            File downloadFile = HttpUtil.downloadFileFromUrl(appDO.getAppLatestVersion().getGithubDownloadUrl(), FileUtil.mkdir(Util.getAppPath(appDO)), new DownloadStreamProgress());
            appDO.setAppPath(downloadFile.getAbsolutePath());
        }
        appsPersistence.add(appDO);
        log.info("Application [{}] installed successfully", name);
        return 0;
    }

    private AppDO localInstall() {
        if (requiredVersion == null) {
            throw new AppRunnerException("Please specify the minimum version that the application runs, for example, Java applications need to specify the Java version, 17");
        }
        AppDO appDO = new AppDO();
        appDO.setName(name);
        appDO.setAppType(getAppType(path));
        if (ApplicationType.java.equals(appDO.getAppType())) {
            appDO.setJava(new AppDO.JavaDO());
            appDO.setRequiredAppTypeVersionNum(requiredVersion);
        }
        appDO.setVersion(version);
        String installedAppPath = Util.getAppPath(appDO) + "/" + path.getName();
        File copy = FileUtil.copy(path, new File(installedAppPath), true);
        log.info("copy application [{}] to: {}", name, copy.getAbsolutePath());
        appDO.setAppPath(copy.getAbsolutePath());
        return appDO;
    }

    private ApplicationType getAppType(File file) {
        String suffix = FileUtil.getSuffix(file);
        ApplicationType[] values = ApplicationType.values();
        for (ApplicationType applicationType : values) {
            for (String typeSuffix : applicationType.getSuffix().split(",")) {
                if (StrUtil.equalsIgnoreCase(suffix, typeSuffix)) {
                    return applicationType;
                }
            }
        }
        throw new AppRunnerException("unsupported file suffix");
    }
}
