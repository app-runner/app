import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @author songyinyin
 * @since 2023/5/2 11:39
 */
@Slf4j
@Ignore
public class TestApp {

    @Test
    public void testCharset() {
        log.info("1. default charset: " + System.getProperty("file.encoding"));
        log.info("2. default charset: " + Charset.defaultCharset());
    }

}
