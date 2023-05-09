package com.github.dudiao.stm.cli.sub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.persistence.ApplicationType;
import com.github.dudiao.stm.persistence.AppsPersistence;
import com.github.dudiao.stm.persistence.StmAppDO;
import com.github.dudiao.stm.plugin.StmException;
import com.github.dudiao.stm.tools.DownloadStreamProgress;
import com.github.dudiao.stm.tools.StmUtils;
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
@CommandLine.Command(name = "install", description = "安装应用")
public class InstallCli implements StmSubCli {


    @CommandLine.Parameters(index = "0", description = "应用名称")
    private String name;

    @CommandLine.Option(names = {"-p", "--path"}, description = "本地应用文件路径")
    private File path;

    @CommandLine.Option(names = {"-rv", "--requiredVersion"}, description = "应用运行的最低版本，比如Java应用需要指定Java版本，17")
    private Long requiredVersion;

    @CommandLine.Option(names = {"-v", "--version"}, defaultValue = "local_version", description = "版本号")
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
        log.info("应用[{}]安装成功", name);
        return 0;
    }

    private StmAppDO localInstall() {
        if (requiredVersion == null) {
            throw new StmException("请指定应用运行的最低版本，比如Java应用需要指定Java版本，17");
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
        log.info("将应用[{}]复制到：{}", name, copy.getAbsolutePath());
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
        throw new StmException("不支持的文件后缀");
    }
}
