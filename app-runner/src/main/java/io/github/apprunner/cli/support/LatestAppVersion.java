package io.github.apprunner.cli.support;

import lombok.Data;

/**
 * 最新的版本信息
 *
 * @author songyinyin
 * @since 2023/11/15 18:45
 */
@Data
public class LatestAppVersion {

    private String version;

    private String downloadUrl;

    private String releaseNotes;

    private String releaseDate;

}
