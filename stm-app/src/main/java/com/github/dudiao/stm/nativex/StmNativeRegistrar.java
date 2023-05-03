package com.github.dudiao.stm.nativex;

import com.github.dudiao.stm.persistence.ToolDO;
import com.github.dudiao.stm.tools.StmUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AopContext;

/**
 * @author songyinyin
 * @since 2023/4/24 10:44
 */
@Component
public class StmNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AopContext context, RuntimeNativeMetadata nativeMetadata) {
        nativeMetadata.registerSerialization(ToolDO.class);

        if (StmUtils.isWindows()) {
            nativeMetadata.registerArg("-H:DefaultCharset=GBK", "-H:DefaultLocale=zh-Hans-CN");
        } else {
            nativeMetadata.registerArg("-H:DefaultCharset=UTF-8", "-H:DefaultLocale=zh-Hans-CN");
        }
    }
}
