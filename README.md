# Solon Toolkit Manager (stm)

`stm`是一个基于`solon`和`picocli`开发的应用管理器，可以方便的管理应用，包括安装、运行、卸载、更新、查看等功能。

# 应用
## 应用开发
每个应用，是一个单独的项目，`stm`只是对其进行管理，是应用启动的入口。

## 应用包发布

# 构建 native image

```bash
sh build.sh
```

## 使用

示例项目地址：https://github.com/dudiao/stm-examples

```shell
stm --help

stm install examples -p /Users/yourpath/stm-examples.jar

stm run examples -n abc -l
```