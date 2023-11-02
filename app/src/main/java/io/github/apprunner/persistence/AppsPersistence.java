package io.github.apprunner.persistence;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import io.github.apprunner.tools.AppRunnerContext;
import io.github.apprunner.tools.Util;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.bean.LifecycleBean;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 持久层，一般是操作数据库，但是 app-runner 为了减少包大小，使用文件来存储数据，所以这里就是操作文件
 *
 * @author songyinyin
 * @since 2023/4/22 18:27
 */
@Component
public class AppsPersistence implements LifecycleBean {

    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);


    private File appsJson;

    @Override
    public void start() throws Throwable {
        appsJson = new File(Util.getAppHome(), "/metadata/apps.json");
    }

    /**
     * 添加应用
     */
    public int add(AppDO plugin) {
        List<AppDO> plugins = listAll();
        plugins.add(plugin);
        writeString(appsJson, ONode.stringify(plugins, jsonOptions));
        AppRunnerContext.clearAppsMeta();
        return 1;
    }

    public int remove(String name) {
        List<AppDO> plugins = listAll();
        plugins.removeIf(plugin -> plugin.getName().equals(name));
        writeString(appsJson, ONode.stringify(plugins, jsonOptions));
        AppRunnerContext.clearAppsMeta();
        return 1;
    }

    /**
     * 获取当前使用的应用版本
     */
    public AppDO getUsed(String name) {
        List<AppDO> plugins = listCurrent();
        return plugins.stream()
            .filter(plugin -> plugin.getName().equals(name))
            .findFirst().orElseThrow(() -> new AppRunnerException(String.format("Application [%s] does not exist", name)));
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
        List<AppDO> plugins = listAll();
        Optional<AppDO> first = plugins.stream().filter(e -> StrUtil.equals(e.getName(), appName)).findFirst();
        return first.isPresent();
    }

    /**
     * 获取当前安装的应用（包括历史版本）
     */
    public List<AppDO> listAll() {
        List<AppDO> appsMeta = AppRunnerContext.getAppsMeta();
        if (appsMeta != null) {
            return appsMeta;
        }
        FileUtil.touch(appsJson);
        String pluginStr = readString(appsJson);
        ONode pluginNode = ONode.loadStr(pluginStr, jsonOptions);
        List<AppDO> appDOList = pluginNode.toObjectList(AppDO.class);
        AppRunnerContext.setAppsMeta(appDOList);
        return appDOList;
    }

    /**
     * 获取当前安装的应用，应用有多个版本时，去最新安装的或取使用的版本
     */
    public List<AppDO> listCurrent() {
        List<AppDO> apps = listAll();
        Map<String, List<AppDO>> appMap = new LinkedHashMap<>();
        for (AppDO app : apps) {
            List<AppDO> list = appMap.computeIfAbsent(app.getId(), k -> new ArrayList<>());
            list.add(app);
        }
        return appMap.values()
            .stream().map(list -> {
                Optional<AppDO> isUsed = list.stream().filter(AppDO::isUsed).findFirst();
                return isUsed.orElseGet(() -> list.stream().max(Comparator.comparing(AppDO::getVersion, VersionComparator.INSTANCE)).orElse(null));
            })
            .collect(Collectors.toList());
    }

    private String readString(File file) {
        return FileUtil.readString(file, StandardCharsets.UTF_8);
    }

    private void writeString(File file, String content) {
        FileUtil.writeString(content, file, StandardCharsets.UTF_8);
    }

}
