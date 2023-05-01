package com.github.dudiao.stm.cli.sub;

import com.github.dudiao.stm.cli.StmSubCli;
import com.github.dudiao.stm.persistence.ToolDO;
import com.github.dudiao.stm.persistence.ToolsPersistence;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import picocli.CommandLine;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author songyinyin
 * @since 2023/4/22 09:58
 */
@Slf4j
@Component
@CommandLine.Command(name = "list", description = "列出所有的支持的应用")
public class ListCli implements StmSubCli {

    @CommandLine.Option(names = {"-l", "--local"}, description = "是否只列出本地的应用")
    private boolean local;

    @Inject
    private ToolsPersistence toolsPersistence;

    @Override
    public Integer execute() {
        if (local) {
            List<ToolDO> list = toolsPersistence.list();
            List<String> collect = list.stream().map(ToolDO::getName).collect(Collectors.toList());
            log.info("list: {}", collect);
        }
        return 0;
    }
}
