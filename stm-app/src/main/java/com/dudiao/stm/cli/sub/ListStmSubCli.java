package com.dudiao.stm.cli.sub;

import com.dudiao.stm.plugin.StmSubCli;
import org.noear.solon.annotation.Component;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/22 09:58
 */
@Component
@CommandLine.Command(name = "list", description = "列出所有的支持的工具")
public class ListStmSubCli implements StmSubCli {

    @CommandLine.Parameters(index = "0", description = "关键字")
    private String search;

    @CommandLine.Option(names = {"-l", "--local"}, description = "是否只列出本地的工具")
    private boolean local;

    @Override
    public void execute() {

    }
}
