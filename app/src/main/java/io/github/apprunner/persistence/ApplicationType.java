package io.github.apprunner.persistence;

import lombok.Getter;

/**
 * @author songyinyin
 * @since 2023/4/29 19:08
 */
public enum ApplicationType {

    java("Java", "jar"),
    shell("shell", "sh"),
    python("python", "py");

    /**
     * 应用类型
     */
    @Getter
    private final String type;

    /**
     * 后缀名，多个使用,隔开
     */
    @Getter
    private final String suffix;

    ApplicationType(String type, String suffix) {
        this.type = type;
        this.suffix = suffix;
    }
}
