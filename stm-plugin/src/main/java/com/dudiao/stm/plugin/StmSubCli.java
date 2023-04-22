package com.dudiao.stm.plugin;

import org.noear.solon.core.util.LogUtil;

/**
 * @author songyinyin
 * @since 2023/4/21 22:24
 */
public interface StmSubCli extends Runnable {

    default void run() {
        try {
            execute();
        } catch (StmException e) {
            LogUtil.global().error(e.getMessage());
        }
    }

    void execute();

}
