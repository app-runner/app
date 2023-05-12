package io.github.apprunner.picocli;

import org.noear.solon.Solon;
import picocli.CommandLine;

/**
 * @author songyinyin
 * @since 2023/4/21 18:47
 */
public class PicocliSolonFactory implements CommandLine.IFactory {
    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        try {
            return Solon.context().getBean(aClass);
        } catch (Exception ex) {
            return CommandLine.defaultFactory().create(aClass);
        }
    }
}
