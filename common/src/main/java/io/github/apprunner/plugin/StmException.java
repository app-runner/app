package io.github.apprunner.plugin;

/**
 * @author songyinyin
 * @since 2023/4/22 20:17
 */
public class StmException extends RuntimeException {

    private Integer exitCode = 1;

    private Exception exception;

    public StmException(String message) {
        super(message);
    }

    public StmException(String message, Exception e) {
        super(message, e);
        this.exception = e;
    }

    public StmException(String message, Integer exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public Exception getException() {
        return exception;
    }
}
