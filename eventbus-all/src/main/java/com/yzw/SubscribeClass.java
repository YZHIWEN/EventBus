package com.yzw;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class SubscribeClass {
    final static String PACKAGE_NAME = "com.yzw";
    final static String CLASS_SUFFIX = "$$Subscriber";

    ClassName string = ClassName.get("java.lang", String.class.getSimpleName());
    ClassName list = ClassName.get("java.util", ArrayList.class.getSimpleName());
    TypeName tagList = ParameterizedTypeName.get(list, string);

    ClassName integer = ClassName.get("java.lang", Integer.class.getSimpleName());
    ClassName hashmap = ClassName.get("java.util", HashMap.class.getSimpleName());
    TypeName priorityMap = ParameterizedTypeName.get(hashmap, string, integer);

    // Subscriber所在类
    TypeElement enclosingElement;
    // 生成的类名
    String className;
    Set<String> tags;
    Map<String, List<TagInfo>> tagInfoMap;

    SubscribeClass(TypeElement enclosingElement) {
        this.enclosingElement = enclosingElement;
        this.className = enclosingElement.getSimpleName() + CLASS_SUFFIX;
        this.tags = new HashSet<>();
        this.tagInfoMap = new HashMap<String, List<TagInfo>>();
    }

    public void addTag(String tag, int priority, ThreadMode threadmode, String receiveMethod) {
        tags.add(tag);
        List<TagInfo> taginfos = tagInfoMap.get(tag);
        if (taginfos == null) {
            taginfos = new ArrayList<>();
            taginfos.add(new TagInfo(priority, threadmode, receiveMethod));
            tagInfoMap.put(tag, taginfos);
        } else {
            TagInfo temp = new TagInfo(priority, threadmode, receiveMethod);
            for (int i = 0; i < taginfos.size(); i++) {
                if (taginfos.get(i).priority < temp.priority) {
                    taginfos.add(temp);
                    return;
                }
            }
            taginfos.add(temp);
        }
    }


    JavaFile brewJava() {
        FieldSpec taglist = FieldSpec.builder(tagList, "tagList").initializer("new ArrayList<String>()").build();
        FieldSpec prioritymap = FieldSpec.builder(priorityMap, "priorityMap").initializer("new HashMap<String,Integer>()").build();
        FieldSpec subscribertarget = FieldSpec.builder(TypeName.get(enclosingElement.asType()), "target").build();

        MethodSpec.Builder addtaglistBuilder = MethodSpec.methodBuilder("addTagList");
        for (String tag : tags) {
            addtaglistBuilder.addCode("tagList.add(\"" + tag + "\");\n");
        }
        MethodSpec addtaglist = addtaglistBuilder.build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addParameter(TypeName.get(enclosingElement.asType()), "target")
                .addCode("this.target = target;\n")
                .addCode("addTagList();\n")
                .addCode("\n").build();

        MethodSpec.Builder onreceiveBuilder = MethodSpec.methodBuilder("onReceive")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(String.class, "tag")
                .addParameter(EventBundle.class, "bundle");
        for (Map.Entry<String, List<TagInfo>> entry : tagInfoMap.entrySet()) {
            String tag = entry.getKey();
            onreceiveBuilder.addCode("if( tag.equals(\"" + tag + "\") ) {\n");
            for (TagInfo ti : entry.getValue()) {
                onreceiveBuilder.addCode("      target." + ti.receiveMethod + "(bundle);\n");
            }
            onreceiveBuilder.addCode("}\n\n");
        }

        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addField(subscribertarget)
                .addSuperinterface(ParameterizedTypeName.get(Subscriber.class))
                .addField(taglist)
                .addField(prioritymap)
                .addMethod(addtaglist)
                .addMethod(constructor)
                .addMethod(onreceiveBuilder.build());

        return JavaFile.builder(PACKAGE_NAME, result.build()).build();


    }

    public static class TagInfo {
        int priority;
        ThreadMode threadmode;
        String receiveMethod;

        TagInfo(int p, ThreadMode tm, String receiveMethod) {
            this.priority = p;
            this.threadmode = tm;
            this.receiveMethod = receiveMethod;
        }

        @Override
        public String toString() {
            return "TagInfo{" +
                    "priority=" + priority +
                    ", threadmode=" + threadmode +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SubscribeClass [tags=" + tags + ", tagInfoMap=" + tagInfoMap
                + "]";
    }


}
