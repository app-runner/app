package io.github.apprunner.persistence;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.github.apprunner.tools.AppRunnerContext;
import io.github.apprunner.tools.AppRunnerUtils;
import io.github.apprunner.plugin.AppRunnerException;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.bean.LifecycleBean;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author songyinyin
 * @since 2023/4/22 18:27
 */
@Component
public class AppsPersistence implements LifecycleBean {

    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);


    private File appsJson;

    @Override
    public void start() throws Throwable {
        appsJson = new File(AppRunnerUtils.getAppHome(), "/metadata/apps.json");
    }

    public int add(AppDO plugin) {
        List<AppDO> plugins = list();
        plugins.add(plugin);
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), appsJson, StandardCharsets.UTF_8);
        AppRunnerContext.clearAppsMeta();
        return 1;
    }

    public int remove(String name) {
        List<AppDO> plugins = list();
        plugins.removeIf(plugin -> plugin.getName().equals(name));
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), appsJson, StandardCharsets.UTF_8);
        AppRunnerContext.clearAppsMeta();
        return 1;
    }

    /**
     * 获取当前使用的应用版本
     */
    public AppDO getUsed(String name) {
        List<AppDO> plugins = list();
        return plugins.stream()
            .filter(plugin -> plugin.getName().equals(name))
            .max(Comparator.comparing(AppDO::getVersion, VersionComparator.INSTANCE)).orElse(null);
    }

    /**
     * 判断应用是否存在
     *
     * @param appName
     */
    public void existAndThrow(String appName) {
        if (exist(appName)) {
            throw new AppRunnerException(String.format("Application [%s] already exists", appName));
        }
    }

    public boolean exist(String appName) {
        List<AppDO> plugins = list();
        Optional<AppDO> first = plugins.stream().filter(e -> StrUtil.equals(e.getName(), appName)).findFirst();
        return first.isPresent();
    }

    public List<AppDO> list() {
        List<AppDO> appsMeta = AppRunnerContext.getAppsMeta();
        if (appsMeta != null) {
            return appsMeta;
        }
        FileUtil.touch(appsJson);
        String pluginStr = FileUtil.readString(appsJson, StandardCharsets.UTF_8);
        ONode pluginNode = ONode.loadStr(pluginStr, jsonOptions);
        List<AppDO> appDOList = pluginNode.toObjectList(AppDO.class);
        AppRunnerContext.setAppsMeta(appDOList);
        return appDOList;
    }

}
