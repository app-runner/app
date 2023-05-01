package com.github.dudiao.stm.persistence;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import com.github.dudiao.stm.plugin.StmException;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
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
public class ToolsPersistence {

    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);


    @Inject(value = "${stm.tools.metadata-path}", required = false)
    private File toolsJson;

    @Init
    public void init() {
        if (toolsJson == null) {
            toolsJson = new File(SystemPropsUtil.get("user.home"), "/.stm/metadata/tools.json");
            System.out.println(toolsJson.getAbsolutePath());
        }
    }

    public int add(ToolDO plugin) {
        List<ToolDO> plugins = list();
        Optional<ToolDO> first = plugins.stream().filter(e -> StrUtil.equals(e.getName(), plugin.getName())).findFirst();
        if (first.isPresent()) {
            throw new StmException(String.format("插件 [%s] 已存在", plugin.getName()));
        }
        plugins.add(plugin);
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), toolsJson, StandardCharsets.UTF_8);
        return 1;
    }

    public int remove(String name) {
        List<ToolDO> plugins = list();
        plugins.removeIf(plugin -> plugin.getName().equals(name));
        FileUtil.writeString(ONode.stringify(plugins, jsonOptions), toolsJson, StandardCharsets.UTF_8);
        return 1;
    }

    public ToolDO get(String name) {
        List<ToolDO> plugins = list();
        return plugins.stream().filter(plugin -> plugin.getName().equals(name)).findFirst().orElse(null);
    }

    public List<ToolDO> list() {
        FileUtil.touch(toolsJson);
        String pluginStr = FileUtil.readString(toolsJson, StandardCharsets.UTF_8);
        ONode pluginNode = ONode.loadStr(pluginStr, jsonOptions);
        return pluginNode.toObjectList(ToolDO.class);
    }

}
