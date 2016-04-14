package com.yzw;

import com.yzw.util.Annotations;
import com.yzw.util.AnnotiationUtil;
import com.yzw.util.EventBusLog;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.swing.border.EtchedBorder;
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
//        s.add(Annotations.SUBSCRIBE);
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
        for (TypeElement annotation : annotations) {
            final String annotationClassName = annotation.asType().toString();
//            EventBus Log : annotation --   Info : com.yzw.eventbuscore.Subscribe
            EventBusLog.i("annotation --- ", annotationClassName);

//            EventBus Log :  ---   Info : Subscribe
            EventBusLog.i(" --- ", annotation.getSimpleName().toString());

//            EventBus Log :  ---   Info :
            EventBusLog.i(" --- ", annotation.getTypeParameters().toString());

//            EventBus Log :  ---   Info : TOP_LEVEL
            EventBusLog.i(" --- ", annotation.getNestingKind().toString());

            try {
                Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith((Class<? extends Annotation>) Class.forName(Annotations.SUBSCRIBE));
                if (elementsAnnotatedWith == null)
                    EventBusLog.i(" elementsAnnotatedWith ", "elementsAnnotatedWith == null");
                else {

                    EventBusLog.i(" elementsAnnotatedWith ", "elementsAnnotatedWith != null");

//                    EventBus Log :  elementsAnnotatedWith   Info : [s(com.yzw.EventBundle)]
                    EventBusLog.i(" elementsAnnotatedWith ", elementsAnnotatedWith.toString());
                    for (Element e : elementsAnnotatedWith) {
                        if (e.getKind() != ElementKind.METHOD) {
                            // 报错
                        }

                        // 获取该元素所在类
                        TypeElement enclosingElement = (TypeElement) e
                                .getEnclosingElement();

//                        SubscribeClass subscribeClass = targetClassMap
//                                .get(enclosingElement);
//                        if (subscribeClass == null) {
//                            subscribeClass = new SubscribeClass(enclosingElement);
//                            targetClassMap.put(enclosingElement, subscribeClass);
//                        }
                        // 获取注解

//                        EventBus Log :  annotation   Info : @com.yzw.eventbuscore.Subscribe(priority=22, threadmode=IO, tag=MainAcS)
                        Annotation annotation1 = e.getAnnotation((Class<? extends Annotation>) Class.forName(Annotations.SUBSCRIBE));
                        EventBusLog.i(" annotation ",annotation1.toString());

//                        Subscribe sa = e.getAnnotation(Subscribe.class);
//                        subscribeClass.addTag(sa.tag(), sa.priority(), sa.threadmode(), e.getSimpleName().toString());
                    }
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

//            if (annotationClassName.equals(Annotations.SUBSCRIBE)) {
            final Set<? extends Element> elementsWithListenerAnnotation = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elementsWithListenerAnnotation) {
//                    EventBus Log :  aaaa   Info : s // 方法名
                EventBusLog.i(" aaaa ", element.getSimpleName().toString());

//                    EventBus Log :  bbbb   Info : METHOD
                EventBusLog.i(" bbbb ", element.getKind().toString());

//                    EventBus Log :  cccc   Info : [public]
                EventBusLog.i(" cccc ", element.getModifiers().toString());

//                    EventBus Log :  eeee   Info : (com.yzw.EventBundle)void
                EventBusLog.i(" eeee ", element.asType().toString());

//                    EventBus Log :  dddd   Info : com.yzw.eventbus.MainActivity
                EventBusLog.i(" dddd ", element.getEnclosingElement().toString());

            }

            List<? extends AnnotationMirror> annotationMirrors = annotation.getAnnotationMirrors();
            EventBusLog.i(" annotationMirrors ", annotationMirrors.toString());


//            }
        }

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
