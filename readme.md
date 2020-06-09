

## 使用方式

maven pom添加以下依赖
```xml
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>1.3.7</version>
    <configuration>
        <verbose>true</verbose>
        <overwrite>true</overwrite>
        <configurationFile>${mybatis.generator.configFile}</configurationFile>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql-connector-java.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-core</artifactId>
            <version>1.3.7</version>
        </dependency>
        <dependency>
            <groupId>com.kevin</groupId>
            <artifactId>mybatis-generator-lombok-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</plugin>
```

generateConfig添加系列配置
```xml
<plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
<plugin type="com.kevin.generator.api.LombokPlugin">

    <!-- enable annotations -->
    <property name="author" value="kevin"/>
    <property name="builder" value="true"/>
    <property name="toString" value="true"/>
    <property name="noArgsConstructor" value="true"/>
    <property name="allArgsConstructor" value="true"/>
</plugin>

```


## 更多参考
https://github.com/itfsw/mybatis-generator-plugin
MyBatisCodeHelperPro