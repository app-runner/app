package io.github.apprunner.cli;

import io.github.apprunner.tools.AppHome;
import io.github.apprunner.tools.Util;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import picocli.CommandLine;

import java.nio.charset.Charset;

/**
 * @author songyinyin
 * @since 2023/4/21 18:59
 */
@Component
public class AppRunnerVersionProvider implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        String solonVersion = String.format(":: Solon  :: v(%s)", Solon.version());
        String stmVersion = String.format(":: AppRunner :: v(%s)", Solon.cfg().get("apprunner.version", "unknown"));
        if (Util.isDebugMode()) {
            String osName = Util.getOsName();
            String osArch = Util.getOsArch();
            String sysInfo = "app-runner run in: %s (%s)".formatted(osName, osArch);
            String charsetInfo = "default charset: %s".formatted(Charset.defaultCharset());
            String currentDir = "current dir: %s".formatted(new AppHome().findDefaultHomeDir());
            return new String[]{solonVersion, stmVersion, sysInfo, charsetInfo, currentDir};
        }
        return new String[]{solonVersion, stmVersion};
    }
}
