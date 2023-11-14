package io.github.apprunner.persistence.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songyinyin
 * @since 2023/4/22 18:56
 */
@Data
public class AppDO implements Serializable {

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

    private JavaDO javaParams;

    /**
     * 是否为当前使用的版本
     */
    private boolean isUsed;

    /**
     * 应用的路径，形如：~/.apprunner/app/{appName}/{version}
     */
    private String appPath;

    public String getAppRuntimePath() {
        if (appType.equals(ApplicationType.java)) {
            return javaParams == null ? null : javaParams.getJavaHome();
        }
        return null;
    }

    public JavaDO getJavaParams() {
        if (appType.equals(ApplicationType.java) && javaParams == null) {
            javaParams = new JavaDO();
        }
        return javaParams;
    }

    @Data
    public static class JavaDO {

        private String javaHome;

        private String programArguments;

        private String jvmArguments;
    }

}
