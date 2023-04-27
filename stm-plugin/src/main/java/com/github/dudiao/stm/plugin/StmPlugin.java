package com.github.dudiao.stm.plugin;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author songyinyin
 * @since 2023/4/23 10:36
 */
public abstract class StmPlugin implements Plugin {

    protected AopContext context;

    @Override
    public void start(AopContext context) throws Throwable {
        this.context = context;

        // 扫描自己的组件
        this.context.beanScan(getClass());
    }

}
