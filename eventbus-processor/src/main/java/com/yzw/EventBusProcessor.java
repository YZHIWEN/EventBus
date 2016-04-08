package com.yzw;

import com.google.auto.service.AutoService;
import com.yzw.annotations.Subscribe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

// 注解 @AutoService 自动生成 javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
public final class EventBusProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
//    // 元素操作的辅助类
//    Elements elementUtils;
//    private Types typeUtils;
//    private Filer filer;
//    ProcessingEnvironment env;
//
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.RELEASE_6;
//    }
//
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> s = new LinkedHashSet<>();
//        s.add(Subscribe.class.getCanonicalName());
//        return s;
//    }
//
//
//    @Override
//    public synchronized void init(ProcessingEnvironment env) {
//        super.init(env);
//
//        this.env = env;
//        elementUtils = env.getElementUtils();
//        typeUtils = env.getTypeUtils();
//        filer = env.getFiler();
//    }
//
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations,
//                           RoundEnvironment roundEnv) {
////        handleSubscribe(roundEnv);
//        return true;
//    }
//
//    private void handleSubscribe(RoundEnvironment roundEnv) {
//        Map<TypeElement, SubscribeClass> targetClassMap = findSubscribleClass(roundEnv);
//        log(targetClassMap);
//        generateSubscribeClass(targetClassMap);
//    }
//
//    private void generateSubscribeClass(
//            Map<TypeElement, SubscribeClass> targetClassMap) {
//        File dir = new File("f://a");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        for (TypeElement key : targetClassMap.keySet()) {
//            File file = new File(dir, "adfa.txt");
//            try {
//                FileWriter fw = new FileWriter(file);
//                fw.append("...........");
//                fw.flush();
//                fw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    private void log(Map<TypeElement, SubscribeClass> targetClassMap) {
//        for (Entry<TypeElement, SubscribeClass> e : targetClassMap.entrySet()) {
//            env.getMessager().printMessage(Diagnostic.Kind.NOTE, "i am here" + e.getKey().getSimpleName() + "  "
//                    + e.getValue().toString());
//        }
//    }
//
//    private Map<TypeElement, SubscribeClass> findSubscribleClass(
//            RoundEnvironment roundEnv) {
//        Map<TypeElement, SubscribeClass> targetClassMap = new LinkedHashMap<TypeElement, SubscribeClass>();
//        // 获得被该注解声明的元素
//        Set<? extends Element> sas = roundEnv
//                .getElementsAnnotatedWith(Subscribe.class);
//        for (Element e : sas) {
//            if (e.getKind() != ElementKind.METHOD) {
//                // 报错
//            }
//
//            // 获取该元素所在类
//            TypeElement enclosingElement = (TypeElement) e
//                    .getEnclosingElement();
//
//            SubscribeClass subscribeClass = targetClassMap
//                    .get(enclosingElement);
//            if (subscribeClass == null) {
//                subscribeClass = new SubscribeClass();
//                targetClassMap.put(enclosingElement, subscribeClass);
//            }
//
//            // 获取注解
//            Subscribe sa = e.getAnnotation(Subscribe.class);
//            subscribeClass.addTag(sa.tag(), sa.priority(), sa.threadmode());
//        }
//        return targetClassMap;
//    }

}
