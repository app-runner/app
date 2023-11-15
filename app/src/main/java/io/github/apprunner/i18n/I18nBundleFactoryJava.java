package io.github.apprunner.i18n;

import org.noear.solon.annotation.Component;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nBundleFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author songyinyin
 * @since 2023/11/15 14:06
 */
@Component
public class I18nBundleFactoryJava implements I18nBundleFactory {
    @Override
    public I18nBundle create(String bundleName, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        return new I18nBundleJava(bundle);
    }


}
