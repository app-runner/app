package io.github.apprunner.cli.support;

import io.github.apprunner.persistence.AppsPersistence;
import io.github.apprunner.persistence.entity.AppDO;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;

import java.util.Iterator;

/**
 * @author songyinyin
 * @since 2023/11/1 16:52
 */
@Component
public class AppNameCandidates implements Iterable<String> {
    @Override
    public Iterator<String> iterator() {
        AppsPersistence appsPersistence = Solon.context().getBean(AppsPersistence.class);
        return appsPersistence.listCurrent().stream().map(AppDO::getName).toList().iterator();
    }
}
