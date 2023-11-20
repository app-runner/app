package io.github.apprunner.persistence;

import io.github.apprunner.persistence.entity.AppDO;

import java.util.List;

/**
 * @author songyinyin
 * @since 2023/11/14 11:40
 */
public interface AppPersistence {

    /**
     * 添加应用
     */
    int add(AppDO app);

    /**
     * 更新应用
     */
    int update(AppDO app);

    int remove(String name);

    /**
     * 获取当前使用的应用版本
     */
    AppDO getUsed(String name);

    /**
     * 判断应用是否存在
     *
     * @param appName
     */
    void existAndThrow(String appName);

    boolean exist(String appName);

    /**
     * 获取当前安装的应用（包括历史版本）
     */
    List<AppDO> listAll();

    /**
     * 获取当前安装的应用，应用有多个版本时，取最新安装的或取使用的版本
     */
    List<AppDO> listCurrent();
}
