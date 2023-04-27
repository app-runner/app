package com.github.dudiao.stm.plugin;

/**
 * @author songyinyin
 * @since 2023/4/22 20:17
 */
public class StmException extends RuntimeException {

    private Integer exitCode = 1;

    public StmException(String message) {
        super(message);
    }

    public StmException(String message, Integer exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public Integer getExitCode() {
        return exitCode;
    }
}
