package com.github.dudiao.stm.persistence;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songyinyin
 * @since 2023/5/8 15:27
 */
@Data
public class StmAppVersionDO implements Serializable {

    private String version;

    private String status;

    private String description;

    private String githubDownloadUrl;

    private String giteeDownloadUrl;
}
