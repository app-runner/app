package com.dudiao.stm.cli;

import org.noear.solon.Solon;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/21 18:59
 */
public class StmVersionProvider implements CommandLine.IVersionProvider {

    public static final String version = "1.0.0";

    @Override
    public String[] getVersion() throws Exception {
        String solonVersion = String.format(":: Solon  :: v(%s)", Solon.version());
        String stmVersion = String.format(":: STM :: v(%s)", version);
        return new String[]{solonVersion, stmVersion};
    }
}
