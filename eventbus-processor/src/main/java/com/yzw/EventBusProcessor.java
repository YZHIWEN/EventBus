package com.yzw;

import com.yzw.util.Annotations;
import com.yzw.util.AnnotiationUtil;
//import com.yzw.util.EventBusLog;

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

        handleSubscribe(roundEnv);
        return false;
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
            String sub = e.getValue().enclosingElement.getQualifiedName().toString();
            String s = sub.substring(0, sub.lastIndexOf("."));
            String subclass = sub.substring(sub.lastIndexOf(".") + 1);

            env.getMessager().printMessage(Diagnostic.Kind.WARNING, sub);
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, s);
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, subclass);

            env.getMessager().printMessage(Diagnostic.Kind.WARNING, "--------->" + e.getKey().getQualifiedName().toString()); // com.yzw.eventbus.MainActivity
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, "--------->" + e.getKey().getEnclosingElement().toString()); //com.yzw.eventbus
        }
    }

    private Map<TypeElement, SubscribeClass> findSubscribleClass(
            RoundEnvironment roundEnv) {
        Map<TypeElement, SubscribeClass> targetClassMap = new LinkedHashMap<TypeElement, SubscribeClass>();
        Set<? extends Element> sas = roundEnv
                .getElementsAnnotatedWith(Subscribe.class);
        for (Element e : sas) {
            if (e.getKind() != ElementKind.METHOD) {
            }
            TypeElement enclosingElement = (TypeElement) e
                    .getEnclosingElement();

            SubscribeClass subscribeClass = targetClassMap
                    .get(enclosingElement);
            if (subscribeClass == null) {
                subscribeClass = new SubscribeClass(enclosingElement);
                targetClassMap.put(enclosingElement, subscribeClass);
            }

            Subscribe sa = e.getAnnotation(Subscribe.class);
            subscribeClass.addTag(sa.tag(), sa.priority(), sa.threadmode(), e.getSimpleName().toString());
        }
        return targetClassMap;
    }

}
