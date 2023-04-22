package com.dudiao.stm.persistence;

import lombok.Builder;
import lombok.Data;

/**
 * @author songyinyin
 * @since 2023/4/22 18:56
 */
@Data
@Builder
public class StmPlugin {

    private String name;

    private String version;

    private String author;

    private String description;

    private String jar;
}
