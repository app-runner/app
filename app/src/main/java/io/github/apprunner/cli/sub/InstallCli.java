package io.github.apprunner.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.github.apprunner.tools.DownloadStreamProgress;
import io.github.apprunner.tools.StmUtils;
import io.github.apprunner.cli.StmSubCli;
import io.github.apprunner.persistence.ApplicationType;
import io.github.apprunner.persistence.AppsPersistence;
import io.github.apprunner.persistence.StmAppDO;
import io.github.apprunner.plugin.StmException;
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
public class InstallCli implements StmSubCli {


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
        StmAppDO stmAppDO;
        if (path != null) {
            stmAppDO = localInstall();
        } else {
            appsPersistence.existAndThrow(name);
            stmAppDO = StmUtils.apiLatestVersion(name, null);
            File downloadFile = HttpUtil.downloadFileFromUrl(stmAppDO.getAppLatestVersion().getGithubDownloadUrl(), FileUtil.mkdir(StmUtils.getAppPath(stmAppDO)), new DownloadStreamProgress());
            stmAppDO.setToolAppPath(downloadFile.getAbsolutePath());
        }
        appsPersistence.add(stmAppDO);
        log.info("Application [{}] installed successfully", name);
        return 0;
    }

    private StmAppDO localInstall() {
        if (requiredVersion == null) {
            throw new StmException("Please specify the minimum version that the application runs, for example, Java applications need to specify the Java version, 17");
        }
        StmAppDO stmAppDO = new StmAppDO();
        stmAppDO.setName(name);
        stmAppDO.setAppType(getAppType(path));
        if (ApplicationType.java.equals(stmAppDO.getAppType())) {
            stmAppDO.setJava(new StmAppDO.JavaDO());
            stmAppDO.setRequiredAppTypeVersionNum(requiredVersion);
        }
        stmAppDO.setVersion(version);
        String installedAppPath = StmUtils.getAppPath(stmAppDO) + "/" + path.getName();
        File copy = FileUtil.copy(path, new File(installedAppPath), true);
        log.info("copy application [{}] to: {}", name, copy.getAbsolutePath());
        stmAppDO.setToolAppPath(copy.getAbsolutePath());
        return stmAppDO;
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
        throw new StmException("unsupported file suffix");
    }
}
