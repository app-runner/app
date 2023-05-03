package com.github.dudiao.stm.nativex;

import com.github.dudiao.stm.persistence.ToolDO;
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
        nativeMetadata.registerArg("-H:+AddAllCharsets'");
    }
}
