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

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import io.github.apprunner.persistence.entity.AppDO;
import io.github.apprunner.plugin.AppRunnerException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Ease the execution of a Java process using Maven's toolchain support.
 *
 * @author Stephane Nicoll
 */
@Slf4j
public class JavaProcessExecutor {

    private static final int EXIT_CODE_SIGINT = 130;

    private String javaHome;

    private Consumer<RunProcess> runProcessCustomizer;

    private AppDO javaApp;

    private String[] appParameters;


    public JavaProcessExecutor(String javaHome) {
        this.javaHome = javaHome;
    }

    public JavaProcessExecutor(String javaHome, Consumer<RunProcess> runProcessCustomizer) {
        this.javaHome = javaHome;
        this.runProcessCustomizer = runProcessCustomizer;
    }

    public JavaProcessExecutor(AppDO appDO, String[] appParameters) {
        this.javaApp = appDO;
        this.javaHome = appDO.getAppRuntimePath();
        this.appParameters = appParameters;
    }


    public JavaProcessExecutor withRunProcessCustomizer(String javaHome, Consumer<RunProcess> customizer) {
        return new JavaProcessExecutor(javaHome, customizer);
    }

    public int run(File workingDirectory) {
        List<String> args = new ArrayList<>();

        if (javaApp.getJava() != null && StrUtil.isNotBlank(javaApp.getJava().getJvmArguments())) {
            String[] jvmArgs = CommandLineUtils.parseArgs(javaApp.getJava().getJvmArguments());
            args.addAll(Arrays.asList(jvmArgs));
        }

        args.add("-jar");
        args.add(javaApp.getAppPath());

        if (javaApp.getJava() != null && StrUtil.isNotBlank(javaApp.getJava().getAppArguments())) {
            String[] appArgs = CommandLineUtils.parseArgs(javaApp.getJava().getAppArguments());
            args.addAll(Arrays.asList(appArgs));
        }

        if (ArrayUtil.isNotEmpty(appParameters)) {
            args.addAll(Arrays.asList(appParameters));
        }

        Map<String, String> environmentVariables = new LinkedHashMap<>();
        System.getProperties().forEach((k, v) -> {
            if (k != null && v != null) {
                environmentVariables.put(k.toString(), v.toString());
            }
        });
        if (Util.isDebugMode()) {
            log.info("start run java app, work dir=%s, args=%s".formatted(workingDirectory, args));
        }
        return this.run(workingDirectory, args, environmentVariables);
    }

    public int run(File workingDirectory, List<String> args, Map<String, String> environmentVariables) {
        RunProcess runProcess = new RunProcess(workingDirectory, getJavaExecutable());
        if (this.runProcessCustomizer != null) {
            this.runProcessCustomizer.accept(runProcess);
        }
        try {
            int exitCode = runProcess.run(true, args, environmentVariables);
            if (!hasTerminatedSuccessfully(exitCode)) {
                throw new AppRunnerException("Process terminated with exit code: " + exitCode);
            }
            return exitCode;
        } catch (IOException ex) {
            throw new AppRunnerException("Process execution failed", ex);
        }
    }

    public RunProcess runAsync(File workingDirectory, List<String> args, Map<String, String> environmentVariables) {
        try {
            RunProcess runProcess = new RunProcess(workingDirectory, getJavaExecutable());
            runProcess.run(false, args, environmentVariables);
            return runProcess;
        } catch (IOException ex) {
            throw new AppRunnerException("Process execution failed", ex);
        }
    }

    private boolean hasTerminatedSuccessfully(int exitCode) {
        return (exitCode == 0 || exitCode == EXIT_CODE_SIGINT);
    }

    private String getJavaExecutable() {
        return new JavaExecutable(this.javaHome).toString();
    }

}
