/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.apprunner.tools;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.github.apprunner.plugin.AppRunnerException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Provides access to the java binary executable, regardless of OS.
 *
 * @author Phillip Webb
 * @since 1.1.0
 */
public class JavaExecutable {

    private final File file;

    public JavaExecutable(String javaHome) {
        if (StrUtil.isBlank(javaHome)) {
            javaHome = System.getProperty("java.home");
        }
        if (StrUtil.isBlank(javaHome)) {
            javaHome = System.getenv("JAVA_HOME");
        }
        Assert.state(StrUtil.isNotBlank(javaHome), "Unable to find java executable due to missing 'java.home'");
        this.file = findInJavaHome(javaHome);
    }

    private File findInJavaHome(String javaHome) {
        File bin = new File(new File(javaHome), "bin");
        File command = new File(bin, "java.exe");
        command = command.exists() ? command : new File(bin, "java");
        if (!command.exists()) {
            throw new AppRunnerException("Unable to find java in " + javaHome);
        }
        // 添加执行权限
        if (!command.canExecute()) {
            command.setExecutable(true);
        }
        return command;
    }

    public boolean canExecute() {
        return this.file.canExecute();
    }

    /**
     * Create a new {@link ProcessBuilder} that will run with the Java executable.
     *
     * @param arguments the command arguments
     * @return a {@link ProcessBuilder}
     */
    public ProcessBuilder processBuilder(String... arguments) {
        ProcessBuilder processBuilder = new ProcessBuilder(toString());
        processBuilder.command().addAll(Arrays.asList(arguments));
        return processBuilder;
    }

    @Override
    public String toString() {
        try {
            return this.file.getCanonicalPath();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
