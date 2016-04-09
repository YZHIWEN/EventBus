package com.yzw;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public final class EventBusProcessor extends AbstractProcessor {
    // 元素操作的辅助类
    Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    ProcessingEnvironment env;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> s = new LinkedHashSet<String>();
        s.add(Subscribe.class.getCanonicalName());
        return s;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.env = env;
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        handleSubscribe(roundEnv);
        return true;
    }

    private void handleSubscribe(RoundEnvironment roundEnv) {
        Map<TypeElement, SubscribeClass> targetClassMap = findSubscribleClass(roundEnv);
        log(targetClassMap);
        generateSubscribeClass(targetClassMap);
    }

    private void generateSubscribeClass(
            Map<TypeElement, SubscribeClass> targetClassMap) {
        for (Map.Entry<TypeElement, SubscribeClass> entry : targetClassMap.entrySet()) {
            try {
                entry.getValue().brewJava().writeTo(filer);
            } catch (IOException io) {
            }
        }
    }

    String getFqcn(SubscribeClass subscribeClass, TypeElement typeElement) {
        return getPackageName(typeElement) + "." + subscribeClass.className;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void log(Map<TypeElement, SubscribeClass> targetClassMap) {
        for (Map.Entry<TypeElement, SubscribeClass> e : targetClassMap.entrySet()) {
            // 订阅者包名
            String sub = e.getValue().enclosingElement.getQualifiedName().toString();
            // 订阅者对象类型
            String s = sub.substring(0, sub.lastIndexOf("."));
            String subclass = sub.substring(sub.lastIndexOf(".") + 1);

            env.getMessager().printMessage(Diagnostic.Kind.WARNING, sub);
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, s);
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, subclass);

            env.getMessager().printMessage(Diagnostic.Kind.WARNING, "--------->" + e.getKey().getQualifiedName().toString()); // com.yzw.eventbus.MainActivity
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, "--------->" + e.getKey().getEnclosingElement().toString()); //com.yzw.eventbus

//            env.getMessager().printMessage(Diagnostic.Kind.WARNING, e.getKey() + "  map size: " + e.getValue().tagInfoMap.size() + " tag list size: " + e.getValue().tags.size());
//            env.getMessager().printMessage(Diagnostic.Kind.NOTE, " log " + e.getKey().getSimpleName() + " --  "
//                    + e.getValue().toString());
        }
    }

    private Map<TypeElement, SubscribeClass> findSubscribleClass(
            RoundEnvironment roundEnv) {
        Map<TypeElement, SubscribeClass> targetClassMap = new LinkedHashMap<TypeElement, SubscribeClass>();
        // 获得被该注解声明的元素
        Set<? extends Element> sas = roundEnv
                .getElementsAnnotatedWith(Subscribe.class);
        for (Element e : sas) {
            if (e.getKind() != ElementKind.METHOD) {
                // 报错
            }

            // 获取该元素所在类
            TypeElement enclosingElement = (TypeElement) e
                    .getEnclosingElement();

            SubscribeClass subscribeClass = targetClassMap
                    .get(enclosingElement);
            if (subscribeClass == null) {
                subscribeClass = new SubscribeClass(enclosingElement);
                targetClassMap.put(enclosingElement, subscribeClass);
            }

            // 获取注解
            Subscribe sa = e.getAnnotation(Subscribe.class);
            subscribeClass.addTag(sa.tag(), sa.priority(), sa.threadmode(), e.getSimpleName().toString());
        }
        return targetClassMap;
    }

}
