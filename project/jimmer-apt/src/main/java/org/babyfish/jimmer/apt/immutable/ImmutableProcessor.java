package org.babyfish.jimmer.apt.immutable;

import org.babyfish.jimmer.apt.Context;
import org.babyfish.jimmer.apt.entry.EntryProcessor;
import org.babyfish.jimmer.apt.immutable.generator.*;
import org.babyfish.jimmer.apt.immutable.meta.ImmutableType;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;

public class ImmutableProcessor {

    private final Context context;

    private final Messager messager;

    public ImmutableProcessor(Context context, Messager messager) {
        this.context = context;
        this.messager = messager;
    }

    public Map<TypeElement, ImmutableType> process(RoundEnvironment roundEnv) {
        Map<TypeElement, ImmutableType> immutableTypeMap = parseImmutableTypes(roundEnv);
        generateJimmerTypes(immutableTypeMap);
        return immutableTypeMap;
    }

    private Map<TypeElement, ImmutableType> parseImmutableTypes(RoundEnvironment roundEnv) {
        Map<TypeElement, ImmutableType> map = new HashMap<>();
        for (Element element : roundEnv.getRootElements()) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                if (context.isImmutable(typeElement) && context.include(typeElement)) {
                    ImmutableType immutableType = context.getImmutableType(typeElement);
                    map.put(typeElement, immutableType);
                }
            }
        }
        return map;
    }

    private void generateJimmerTypes(Map<TypeElement, ImmutableType> immutableTypeMap) {
        for (ImmutableType immutableType : immutableTypeMap.values()) {
            new DraftGenerator(
                    context,
                    immutableType
            ).generate();
            new PropsGenerator(
                    context,
                    immutableType
            ).generate();
            messager.printMessage(Diagnostic.Kind.NOTE, "Immutable: " + immutableType.getQualifiedName());
            if (immutableType.isEntity()) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Entity: " + immutableType.getQualifiedName());
                new TableGenerator(
                        context,
                        immutableType,
                        false
                ).generate();
                new TableGenerator(
                        context,
                        immutableType,
                        true
                ).generate();
                new FetcherGenerator(
                        context,
                        immutableType
                ).generate();
            } else if (immutableType.isEmbeddable()) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Embeddable: " + immutableType.getQualifiedName());
                new PropExpressionGenerator(
                        context,
                        immutableType
                ).generate();
                new FetcherGenerator(
                        context,
                        immutableType
                ).generate();
            }
        }
    }
}
