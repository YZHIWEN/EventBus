package com.yzw;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class SubscribeClass {
    String PACKAGE_NAME;
    final static String CLASS_SUFFIX = "$$Subscriber";

    // Subscriber所在类
    TypeElement enclosingElement;
    // 生成的类名
    String className;
    // Tag集合
    Set<String> tags;
    // Tag：按优先级排序的TagInfo
    Map<String, List<SubscribeMethod>> tagInfoMap;

    // 订阅者包名
    String sub;
    // 订阅者对象类型
    String s;
    String targetclass;

    SubscribeClass(TypeElement enclosingElement) {
        this.enclosingElement = enclosingElement;
        this.className = enclosingElement.getSimpleName() + CLASS_SUFFIX;
        this.tags = new HashSet<>();
        this.tagInfoMap = new HashMap<String, List<SubscribeMethod>>();

        PACKAGE_NAME = enclosingElement.getEnclosingElement().toString();
        // 订阅者包名
        sub = enclosingElement.getQualifiedName().toString();
        // 订阅者对象类型
        s = sub.substring(0, sub.lastIndexOf("."));
        targetclass = sub.substring(sub.lastIndexOf(".") + 1);
    }

    public void addTag(String tag, int priority, ThreadMode threadmode, String receiveMethod) {
        tags.add(tag);
        List<SubscribeMethod> taginfos = tagInfoMap.get(tag);
        if (taginfos == null) {
            taginfos = new ArrayList<>();
            taginfos.add(new SubscribeMethod(tag, priority, receiveMethod, threadmode));
            tagInfoMap.put(tag, taginfos);
        } else {
            SubscribeMethod temp = new SubscribeMethod(tag, priority, receiveMethod, threadmode);
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
        // ---------->成员变量
        ClassName string = ClassName.get("java.lang", String.class.getSimpleName());
        ClassName list = ClassName.get("java.util", ArrayList.class.getSimpleName());
        TypeName tagList = ParameterizedTypeName.get(list, string);

        ClassName subscription = ClassName.get("com.yzw", Subscription.class.getSimpleName());
        TypeName subscriptionlist = ParameterizedTypeName.get(list, subscription);
        ClassName hashmap = ClassName.get("java.util", HashMap.class.getSimpleName());
        TypeName subscriptionMap = ParameterizedTypeName.get(hashmap, string, subscriptionlist);

        FieldSpec tagListField = FieldSpec.builder(tagList, "tagList").initializer("new ArrayList<String>()").build();
        FieldSpec subscriptionMapField = FieldSpec.builder(subscriptionMap, "subscriptionMap").initializer("new HashMap<String,ArrayList<Subscription>>()").build();
        FieldSpec subscriberTargetField = FieldSpec.builder(TypeName.get(enclosingElement.asType()), "target").build();

        // ----------->方法
        MethodSpec.Builder inittaglistBuilder = MethodSpec.methodBuilder("initTagList");
        MethodSpec.Builder initsubscriptionMap = MethodSpec.methodBuilder("initSubscriptionMap");
        initsubscriptionMap.addCode("ArrayList<Subscription> subscriptions;\n");
        for (String tag : tags) {
            inittaglistBuilder.addCode("tagList.add(\"" + tag + "\");\n");

            List<SubscribeMethod> subscribeMethods = tagInfoMap.get(tag);
            for (SubscribeMethod sm : subscribeMethods) {
                initsubscriptionMap.addCode("subscriptions = subscriptionMap.get(\"" + tag + "\");\n");
                initsubscriptionMap.addCode("if( subscriptions == null ){ subscriptions = new ArrayList<Subscription>(); subscriptionMap.put(\"" + tag + "\",subscriptions);}\n");
                initsubscriptionMap.addCode("subscriptions.add(new Subscription(\"" + tag + "\",this," + sm.priority + "));\n");
            }
        }
        MethodSpec inittaglist = inittaglistBuilder.build();


        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addCode("this.target = target;\n")
                .addCode("initTagList();\n")
                .addCode("initSubscriptionMap();\n")
                .addCode("\n").build();

        MethodSpec settargetsubscriber = MethodSpec.methodBuilder("setTargetSubscriber")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Object.class, "target")
                .addCode("this.target = (" + targetclass + ")target;\n")
                .build();

        MethodSpec gettags = MethodSpec.methodBuilder("getTags")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(tagList)
                .addStatement("return tagList").build();

        MethodSpec getproxy = MethodSpec.methodBuilder("getTagSubscription")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(subscriptionlist)
                .addParameter(String.class, "tag")
                .addStatement("return subscriptionMap.get(tag)").build();

        MethodSpec.Builder onreceiveBuilder = MethodSpec.methodBuilder("onReceive")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(String.class, "tag")
                .addParameter(int.class, "priority")
                .addParameter(EventBundle.class, "bundle");
        for (Map.Entry<String, List<SubscribeMethod>> entry : tagInfoMap.entrySet()) {
            String tag = entry.getKey();
            onreceiveBuilder.addCode("if( tag.equals(\"" + tag + "\") ) {\n");
            for (SubscribeMethod ti : entry.getValue()) {
                onreceiveBuilder.addCode("  if( priority == " + ti.priority + " )     target." + ti.methodName + "(bundle);\n");
            }
            onreceiveBuilder.addCode("}\n\n");
        }

        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addField(subscriberTargetField)
                .addSuperinterface(ParameterizedTypeName.get(Subscriber.class))
                .addField(tagListField)
                .addField(subscriptionMapField)
                .addMethod(inittaglist)
                .addMethod(initsubscriptionMap.build())
                .addMethod(constructor)
                .addMethod(onreceiveBuilder.build())
                .addMethod(settargetsubscriber)
                .addMethod(gettags)
                .addMethod(getproxy);

        return JavaFile.builder(PACKAGE_NAME, result.build()).build();
    }

    @Override
    public String toString() {
        return "SubscribeClass [tags=" + tags + ", tagInfoMap=" + tagInfoMap
                + "]";
    }
}
