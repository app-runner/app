package io.github.apprunner.persistence;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songyinyin
 * @since 2023/4/22 18:56
 */
@Data
public class StmAppDO implements Serializable {

    /**
     * 应用id
     */
    private String id;

    private String name;

    private String author;

    private ApplicationType appType;

    private String description;

    /**
     * 应用运行时，所需的环境版本
     */
    private Long requiredAppTypeVersionNum;

    private StmAppVersionDO appLatestVersion;


    // 以下字段 仅在 持久化到元数据 时使用

    private String version;

    private JavaDO java;

    private String toolAppPath;

    /**
     * 应用运行时以来的环境，比如 jre 的路径，如果不设置，则使用stm配置的对应版本的jre
     */
    private String appRuntimePath;

    @Data
    public static class JavaDO {

        private String javaHome;

        private String appArguments;

        private String jvmArguments;
    }

}
