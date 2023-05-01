package com.github.dudiao.stm.tools;

import cn.hutool.core.util.SystemPropsUtil;
import com.github.dudiao.stm.persistence.ToolDO;
import org.noear.solon.Solon;

/**
 * @author songyinyin
 * @since 2023/4/30 20:04
 */
public class StmUtils {

    public static String getAppPath(ToolDO toolDO) {
        String appHome = Solon.cfg().get("stm.tools.app");
        if (appHome == null) {
            appHome = SystemPropsUtil.get("user.home") + "/.stm/app";
        }
        return "%s/%s/%s".formatted(appHome, toolDO.getName(), toolDO.getVersion());
    }
}
