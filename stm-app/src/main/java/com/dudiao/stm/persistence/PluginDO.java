package com.dudiao.stm.persistence;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songyinyin
 * @since 2023/4/22 18:56
 */
@Data
public class PluginDO implements Serializable {

    private String name;

    private String version;

    private String author;

    private String description;

    private String jar;
}
