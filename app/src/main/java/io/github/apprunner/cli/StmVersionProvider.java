package io.github.apprunner.cli;

import io.github.apprunner.tools.StmUtils;
import org.noear.solon.Solon;
import picocli.CommandLine;

import java.nio.charset.Charset;

/**
 * @author songyinyin
 * @since 2023/4/21 18:59
 */
public class StmVersionProvider implements CommandLine.IVersionProvider {

    public static final String version = "0.0.3";

    @Override
    public String[] getVersion() throws Exception {
        String solonVersion = String.format(":: Solon  :: v(%s)", Solon.version());
        String stmVersion = String.format(":: STM :: v(%s)", version);
        if (StmUtils.isDebugMode()) {
            String osName = StmUtils.getOsName();
            String osArch = StmUtils.getOsArch();
            String sysInfo = "stm run in: %s (%s)".formatted(osName, osArch);
            String charsetInfo = "default charset: %s".formatted(Charset.defaultCharset());
            return new String[]{solonVersion, stmVersion, sysInfo, charsetInfo};
        }
        return new String[]{solonVersion, stmVersion};
    }
}
