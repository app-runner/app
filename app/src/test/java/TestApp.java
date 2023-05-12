import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.noear.solon.aot.Settings;
import org.noear.solon.aot.SolonAotProcessor;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;

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

    public void test() throws ClassNotFoundException {
        String[] args = new String[]{
            "com.github.dudiao.stm.App", "/Users/songyinyin/study/stm/stm-app/target/classes",
            "/Users/songyinyin/study/stm/stm-app/target/solon-aot/main/sources",
            "com.github.dudiao.solon", "stm-app"
        };
        int requiredArgs = 5;
        Class<?> application = Class.forName(args[0]);
        Settings build = new Settings(Paths.get(args[1]), Paths.get(args[2]), args[3], args[4]);

        String[] applicationArgs = (args.length > requiredArgs) ? Arrays.copyOfRange(args, requiredArgs, args.length)
            : new String[0];

        new SolonAotProcessor(build, applicationArgs, application).process();
    }
}
