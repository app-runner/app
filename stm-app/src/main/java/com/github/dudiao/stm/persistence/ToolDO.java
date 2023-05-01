package com.github.dudiao.stm.persistence;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songyinyin
 * @since 2023/4/22 18:56
 */
@Data
public class ToolDO implements Serializable {

    private String name;

    private String version;

    private String author;

    private String description;

    private String toolAppPath;

    private ApplicationType appType;

    private JavaDO java;


    @Data
    public static class JavaDO {

        private String javaHome;

        private String appArguments;

        private String jvmArguments;
    }

}
