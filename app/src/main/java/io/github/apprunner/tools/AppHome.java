package io.github.apprunner.tools;

import cn.hutool.core.util.StrUtil;
import org.noear.solon.Solon;
import org.noear.solon.core.util.ClassUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author songyinyin
 * @since 2023/4/30 14:59
 */
public class AppHome {

    private final File source;

    private final File dir;

    public AppHome() {
        this.source = findSource(Solon.app().source() != null ? Solon.app().source() : getStartClass());
        this.dir = findHomeDir(this.source);
    }

    private Class<?> getStartClass() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return getStartClass(classLoader.getResources("META-INF/MANIFEST.MF"));
        } catch (Exception ex) {
            return null;
        }
    }

    private Class<?> getStartClass(Enumeration<URL> manifestResources) {
        while (manifestResources.hasMoreElements()) {
            try (InputStream inputStream = manifestResources.nextElement().openStream()) {
                Manifest manifest = new Manifest(inputStream);
                String startClass = manifest.getMainAttributes().getValue("Start-Class");
                if (startClass != null) {
                    return ClassUtil.loadClass(getClass().getClassLoader(), startClass);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private File findSource(Class<?> sourceClass) {
        try {
            ProtectionDomain domain = (sourceClass != null) ? sourceClass.getProtectionDomain() : null;
            CodeSource codeSource = (domain != null) ? domain.getCodeSource() : null;
            URL location = (codeSource != null) ? codeSource.getLocation() : null;
            File source = (location != null) ? findSource(location) : null;
            if (source != null && source.exists() && !isUnitTest()) {
                return source.getAbsoluteFile();
            }
        } catch (Exception ex) {
        }
        return null;
    }

    private boolean isUnitTest() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i = stackTrace.length - 1; i >= 0; i--) {
                if (stackTrace[i].getClassName().startsWith("org.junit.")) {
                    return true;
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    private File findSource(URL location) throws IOException, URISyntaxException {
        URLConnection connection = location.openConnection();
        if (connection instanceof JarURLConnection jarURLConnection) {
            return getRootJarFile(jarURLConnection.getJarFile());
        }
        return new File(location.toURI());
    }

    private File getRootJarFile(JarFile jarFile) {
        String name = jarFile.getName();
        int separator = name.indexOf("!/");
        if (separator > 0) {
            name = name.substring(0, separator);
        }
        return new File(name);
    }

    private File findHomeDir(File source) {
        File homeDir = source;
        homeDir = (homeDir != null) ? homeDir : findDefaultHomeDir();
        if (homeDir.isFile()) {
            homeDir = homeDir.getParentFile();
        }
        homeDir = homeDir.exists() ? homeDir : new File(".");
        return homeDir.getAbsoluteFile();
    }

    private File findDefaultHomeDir() {
        String userDir = System.getProperty("user.dir");
        return new File(StrUtil.isNotBlank(userDir) ? userDir : ".");
    }

    /**
     * Returns the underlying source used to find the home directory. This is usually the
     * jar file or a directory. Can return {@code null} if the source cannot be
     * determined.
     *
     * @return the underlying source or {@code null}
     */
    public File getSource() {
        return this.source;
    }

    /**
     * Returns the application home directory.
     *
     * @return the home directory (never {@code null})
     */
    public File getDir() {
        return this.dir;
    }

    @Override
    public String toString() {
        return getDir().toString();
    }
}
