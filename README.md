# Solon Toolkit Manager (stm)

`stm`是一个基于`solon`和`picocli`开发的工具包管理器，可以方便的管理工具包，包括安装、运行、卸载、更新、查看等功能。

# 工具包
## 工具包开发
每个工具包，是一个单独的项目，需要在项目的`pom.xml`中添加`stm-plugin`的依赖，如下：

```xml

<dependency>
    <groupId>com.dudiao.solon</groupId>
    <artifactId>stm-plugin</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 工具包发布

# 构建

```bash
mvn clean install -DskipTests -P tencent,\!77hub -pl stm-plugin -am

cd stm-app

mvn clean native:compile -P tencent,\!77hub,native 
```