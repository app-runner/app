package io.github.apprunner.nativex;

import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.persistence.entity.StmAppVersionDO;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AppContext;

/**
 * @author songyinyin
 * @since 2023/4/24 10:44
 */
@Slf4j
@Component
public class AppRunnerNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AppContext context, RuntimeNativeMetadata nativeMetadata) {
        nativeMetadata.registerSerialization(AppDO.class);
        nativeMetadata.registerSerialization(AppDO.JavaDO.class);
        nativeMetadata.registerSerialization(StmAppVersionDO.class);

        nativeMetadata.registerResourceInclude("i18n/messages_.*\\.properties");

        nativeMetadata.registerArg("--enable-http");
        nativeMetadata.registerArg("--enable-https");

        nativeMetadata.registerArg("-H:+AddAllCharsets");
        nativeMetadata.registerArg("-march=compatibility");
        nativeMetadata.registerArg("-H:Name=app");

    }
}
