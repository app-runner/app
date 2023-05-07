package com.github.dudiao.stm.cli;

import com.github.dudiao.stm.tools.StmUtils;
import org.noear.solon.Solon;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/21 18:59
 */
public class StmVersionProvider implements CommandLine.IVersionProvider {

    public static final String version = "0.0.2";

    @Override
    public String[] getVersion() throws Exception {
        String solonVersion = String.format(":: Solon  :: v(%s)", Solon.version());
        String stmVersion = String.format(":: STM :: v(%s)", version);
        String osName = StmUtils.getOsName();
        String osArch = StmUtils.getOsArch();
        String sysInfo = "STM run in: %s (%s)".formatted(osName, osArch);
        return new String[]{solonVersion, stmVersion, sysInfo};
    }
}
