package com.github.dudiao.stm.nativex;

import com.github.dudiao.stm.persistence.StmAppDO;
import com.github.dudiao.stm.tools.StmUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AopContext;

import java.nio.charset.Charset;

/**
 * @author songyinyin
 * @since 2023/4/24 10:44
 */
@Slf4j
@Component
public class StmNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AopContext context, RuntimeNativeMetadata nativeMetadata) {
        nativeMetadata.registerSerialization(StmAppDO.class);
        nativeMetadata.registerArg("--enable-http");
        nativeMetadata.registerArg("--enable-https");

        if (StmUtils.isWindows()) {
            log.info("当前系统为 Windows，默认编码为 {}", Charset.defaultCharset());
            nativeMetadata.registerArg("-H:+AddAllCharsets", "-H:DefaultLocale=zh-Hans-CN");
        } else {
            log.info("当前系统为 Linux or Mac，默认编码为 {}", Charset.defaultCharset());
            nativeMetadata.registerArg("-H:DefaultCharset=UTF-8", "-H:DefaultLocale=zh-Hans-CN");
        }
    }
}
