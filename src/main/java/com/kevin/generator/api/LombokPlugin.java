package com.kevin.generator.api;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author kevin
 * @Description Lombok Plugin
 * @Date Created on 2020/6/8 15:34
 */
public class LombokPlugin extends PluginAdapter {

    private final Collection<Annotations> annotations;
    private String currentDateStr;
    private String author;

    private String supperClass;

    public LombokPlugin() {
        annotations = new LinkedHashSet<Annotations>(Annotations.values().length);
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {


        // 添加domain的注释
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * @author   " + author);
        topLevelClass.addJavaDocLine(" * @date   " + currentDateStr);
        topLevelClass.addJavaDocLine(" */");

        // 设置父类
        if (supperClass != null) {
            topLevelClass.setSuperClass(new FullyQualifiedJavaType(supperClass));
        }

        // 将需要忽略生成的属性过滤掉
        List<Field> fields = topLevelClass.getFields();

        // 给每个字段加注释
        for (Field field : fields) {
            StringBuilder fieldSb = new StringBuilder();
            String fieldName = field.getName(); // java字段名是驼峰的，需要转成下划线分割
            String underlineFieldName = camelToUnderline(fieldName);
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(underlineFieldName);

            if (null != introspectedColumn) {
                if(StringUtils.isNotBlank(introspectedColumn.getRemarks())){
                    field.addJavaDocLine("/**");
                    fieldSb.append(" * ");
                    fieldSb.append(introspectedColumn.getRemarks());
                    field.addJavaDocLine(fieldSb.toString().replace("\n", " "));
                    field.addJavaDocLine(" */");
                }else {
                    //表没有写注释 不生成
                    continue;
                }
            }

        }

        addAnnotations(topLevelClass);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {

        addAnnotations(topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        addAnnotations(topLevelClass);
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            ModelClassType modelClassType
    ) {
        //不生成getter
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            ModelClassType modelClassType
    ) {
        //不生成setter
        return false;
    }

    private void addAnnotations(TopLevelClass topLevelClass) {
        for (Annotations annotation : annotations) {
            topLevelClass.addImportedType(annotation.javaType);
            topLevelClass.addAnnotation(annotation.asAnnotation());
        }
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        //@Data is default annotation
        annotations.add(Annotations.DATA);
        author = properties.getProperty("author");
        for (String annotationName : properties.stringPropertyNames()) {
            if (annotationName.contains(".")) {
                // Not an annotation name
                continue;
            }
            String value = properties.getProperty(annotationName);
            if (!Boolean.parseBoolean(value)) {
                continue;
            }
            Annotations annotation = Annotations.getValueOf(annotationName);
            if (annotation == null) {
                continue;
            }
            String optionsPrefix = annotationName + ".";
            for (String propertyName : properties.stringPropertyNames()) {
                if (!propertyName.startsWith(optionsPrefix)) {
                    // A property not related to this annotation
                    continue;
                }
                String propertyValue = properties.getProperty(propertyName);
                annotation.appendOptions(propertyName, propertyValue);
                annotations.add(annotation);
                annotations.addAll(Annotations.getDependencies(annotation));
            }
            annotations.add(annotation);
        }
    }


    /**
     * @Author kevin
     * @Description Mapper文件的注释
     * @Date Created on 2020/6/8 16:50
     * @param
     * @return
     */
    @Override
    public boolean clientGenerated(
            Interface interfaze,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        interfaze.addImportedType(new FullyQualifiedJavaType(
                "org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");

        // Mapper文件的注释
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine("* Created by Mybatis Generator on " + currentDateStr);
        interfaze.addJavaDocLine("*/");

        return true;
    }

    /**
     *
     * @Title: camelToUnderline
     * @Description: 把 java 驼峰的变量变为下划线的
     * @param fieldName
     * @return String
     */
    private static String camelToUnderline(String fieldName) {
        StringBuilder result = new StringBuilder();
        if (fieldName != null && fieldName.length() > 0) {
            // 将第一个字符处理成大写
            result.append(fieldName.substring(0, 1).toUpperCase());
            // 循环处理其余字符
            for (int i = 1; i < fieldName.length(); i++) {
                String s = fieldName.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成大写
                result.append(s.toUpperCase());
            }
        }
        return result.toString();
    }
}
