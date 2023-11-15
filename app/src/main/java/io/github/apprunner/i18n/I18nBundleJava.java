package io.github.apprunner.i18n;

import org.noear.solon.core.Props;
import org.noear.solon.i18n.I18nBundle;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author songyinyin
 * @since 2023/11/15 14:08
 */
public class I18nBundleJava implements I18nBundle {

    ResourceBundle bundle;

    public I18nBundleJava(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public Props toProps() {
        Props props = new Props();
        for (String key : bundle.keySet()) {
            props.put(key, bundle.getString(key));
        }
        return props;
    }

    @Override
    public Locale locale() {
        return bundle.getLocale();
    }

    @Override
    public String get(String key) {
        return bundle.getString(key);
    }
}
