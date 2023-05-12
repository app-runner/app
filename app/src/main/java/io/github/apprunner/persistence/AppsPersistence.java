package io.github.apprunner.persistence;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.github.apprunner.tools.StmContext;
import io.github.apprunner.tools.StmUtils;
import io.github.apprunner.plugin.StmException;
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
        appsJson = new File(StmUtils.getAppHome(), "/metadata/apps.json");
    }

    public int add(StmAppDO plugin) {
        existAndThrow(plugin.getName());

        List<StmAppDO> plugins = list();
        plugins.add(plugin);
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), appsJson, StandardCharsets.UTF_8);
        StmContext.clearAppsMeta();
        return 1;
    }

    public int remove(String name) {
        List<StmAppDO> plugins = list();
        plugins.removeIf(plugin -> plugin.getName().equals(name));
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), appsJson, StandardCharsets.UTF_8);
        StmContext.clearAppsMeta();
        return 1;
    }

    /**
     * 获取当前使用的应用版本
     */
    public StmAppDO getUsed(String name) {
        List<StmAppDO> plugins = list();
        return plugins.stream()
            .filter(plugin -> plugin.getName().equals(name))
            .max(Comparator.comparing(StmAppDO::getVersion, VersionComparator.INSTANCE)).orElse(null);
    }

    /**
     * 判断应用是否存在
     *
     * @param appName
     */
    public void existAndThrow(String appName) {
        if (exist(appName)) {
            throw new StmException(String.format("插件 [%s] 已存在", appName));
        }
    }

    public boolean exist(String appName) {
        List<StmAppDO> plugins = list();
        Optional<StmAppDO> first = plugins.stream().filter(e -> StrUtil.equals(e.getName(), appName)).findFirst();
        return first.isPresent();
    }

    public List<StmAppDO> list() {
        List<StmAppDO> appsMeta = StmContext.getAppsMeta();
        if (appsMeta != null) {
            return appsMeta;
        }
        FileUtil.touch(appsJson);
        String pluginStr = FileUtil.readString(appsJson, StandardCharsets.UTF_8);
        ONode pluginNode = ONode.loadStr(pluginStr, jsonOptions);
        List<StmAppDO> appDOList = pluginNode.toObjectList(StmAppDO.class);
        StmContext.setAppsMeta(appDOList);
        return appDOList;
    }

}
