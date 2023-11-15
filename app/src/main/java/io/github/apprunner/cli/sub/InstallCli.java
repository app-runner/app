package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.cli.AppRelatedCli;
import io.github.apprunner.persistence.AppPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.persistence.entity.ApplicationType;
import io.github.apprunner.plugin.AppRunnerException;
import io.github.apprunner.tools.ApiUtils;
import io.github.apprunner.tools.DownloadStreamProgress;
import io.github.apprunner.tools.Util;
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
@CommandLine.Command(name = "install", description = "${bundle:install.description}")
public class InstallCli extends AppRelatedCli {

    @CommandLine.Option(names = {"-p", "--path"}, description = "${bundle:install.parameter.path}")
    private File path;

    @CommandLine.Option(names = {"-rv", "--requiredVersion"}, description = "${bundle:install.parameter.requiredVersion}")
    private Long requiredVersion;

    @CommandLine.Option(names = {"-v", "--version"}, description = "${bundle:install.parameter.version}")
    private String version;

    @Inject
    private AppPersistence appPersistence;

    @Override
    public Integer execute() {
        AppDO appDO;
        if (path != null) {
            appDO = localInstall();
        } else {
            appPersistence.existAndThrow(name);
            appDO = ApiUtils.apiLatestVersion(name, null);
            String downloadUrl = StrUtil.isBlank(appDO.getAppLatestVersion().getGithubDownloadUrl()) ? appDO.getAppLatestVersion().getGiteeDownloadUrl() : appDO.getAppLatestVersion().getGithubDownloadUrl();
            File downloadFile = HttpUtil.downloadFileFromUrl(downloadUrl, FileUtil.mkdir(Util.getAppPath(appDO)), new DownloadStreamProgress());
            appDO.setAppPath(downloadFile.getAbsolutePath());
        }
        appDO.setUsed(true);
        appPersistence.add(appDO);
        log.info(getMessages("install.log.success", name));
        return 0;
    }

    private AppDO localInstall() {
        if (requiredVersion == null) {
            throw new AppRunnerException("requiredVersion is required");
        }
        AppDO appDO = new AppDO();
        appDO.setName(name);
        appDO.setAppType(getAppType(path));
        if (ApplicationType.java.equals(appDO.getAppType())) {
            appDO.setJavaParams(new AppDO.JavaDO());
            appDO.setRequiredAppTypeVersionNum(requiredVersion);
        }
        appDO.setVersion("local");
        String installedAppPath = Util.getAppPath(appDO) + "/" + path.getName();
        File copy = FileUtil.copy(path, new File(installedAppPath), true);
        log.info(getMessages("install.log.copy", name, copy.getAbsolutePath()));
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
