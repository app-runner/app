package com.dudiao.stm.persistence;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dudiao.stm.plugin.StmException;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author songyinyin
 * @since 2023/4/22 18:27
 */
@Component
public class StmPluginManager {

    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);


    @Inject("stm.plugin.json")
    private File pluginJson;

    public int add(StmPlugin plugin) {
        List<StmPlugin> plugins = list();
        Optional<StmPlugin> first = plugins.stream().filter(e -> StrUtil.equals(e.getName(), plugin.getName())).findFirst();
        if (first.isPresent()) {
            throw new StmException(String.format("插件 [%s] 已存在", plugin.getName()));
        }
        plugins.add(plugin);
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), pluginJson, StandardCharsets.UTF_8);
        return 1;
    }

    public int remove(String name) {
        List<StmPlugin> plugins = list();
        plugins.removeIf(plugin -> plugin.getName().equals(name));
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), pluginJson, StandardCharsets.UTF_8);
        return 1;
    }

    public StmPlugin get(String name) {
        List<StmPlugin> plugins = list();
        return plugins.stream().filter(plugin -> plugin.getName().equals(name)).findFirst().orElse(null);
    }

    public List<StmPlugin> list() {
        FileUtil.touch(pluginJson);
        String pluginStr = FileUtil.readString(pluginJson, StandardCharsets.UTF_8);
        ONode pluginNode = ONode.loadStr(pluginStr, jsonOptions);
        return pluginNode.toObjectList(StmPlugin.class);
    }

}
