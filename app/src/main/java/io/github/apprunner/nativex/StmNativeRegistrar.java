package io.github.apprunner.nativex;

import io.github.apprunner.persistence.StmAppDO;
import io.github.apprunner.persistence.StmAppVersionDO;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AopContext;

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
        nativeMetadata.registerSerialization(StmAppVersionDO.class);

        nativeMetadata.registerArg("--enable-http");
//        nativeMetadata.registerArg("--enable-https");

        nativeMetadata.registerArg("-H:+AddAllCharsets", "-Dfile.encoding=UTF-8");

    }
}
