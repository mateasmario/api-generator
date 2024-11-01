package dev.mateas.rag.processor.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.quality.NotNull;
import dev.mateas.rag.annotations.GenerateApi;
import dev.mateas.rag.constants.AnnotationProcessorConstants;
import dev.mateas.rag.entity.AnnotationInfo;
import dev.mateas.rag.processor.AnnotationProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SpringBootAnnotationProcessor implements AnnotationProcessor {
    @Override
    public void process(@NotNull File file) {
        System.out.println("Analyzing: " + file);

        if (file.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(this::process);
        } else {
            processFile(file);
        }
    }

    private void processFile(File file) {
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);

            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(clazz -> clazz.getAnnotations().stream()
                            .anyMatch(ann -> ann.getName().asString().equals("GenerateApi")))
                    .forEach(this::generate);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generate(ClassOrInterfaceDeclaration clazz) {
        generateEntity(clazz);
    }


    private void generateEntity(ClassOrInterfaceDeclaration clazz) {
        AnnotationInfo annotationInfo = processAnnotatedClass(clazz);

        List<FieldDeclaration> fieldDeclarationList = clazz.getFields();

        StringBuilder stringBuilder = new StringBuilder();

        // Define class name
        stringBuilder.append(annotationInfo.getEntityPackage())
                .append(AnnotationProcessorConstants.MODIFIER_PUBLIC)
                .append(AnnotationProcessorConstants.SPACE)
                .append(AnnotationProcessorConstants.KEYWORD_CLASS)
                .append(AnnotationProcessorConstants.SPACE)
                .append(clazz.getNameAsString()).append(AnnotationProcessorConstants.BRACE_OPEN)
                .append(AnnotationProcessorConstants.NEWLINE);

        // Define fields
        fieldDeclarationList
                .stream()
                .map(FieldDeclaration::getVariables)
                .map(variable -> variable.get(0))
                .forEach(
                        variable -> {
                            stringBuilder.append(AnnotationProcessorConstants.MODIFIER_PRIVATE)
                                    .append(AnnotationProcessorConstants.SPACE)
                                    .append(variable.getType())
                                    .append(AnnotationProcessorConstants.SPACE)
                                    .append(variable.getName())
                                    .append(AnnotationProcessorConstants.SEMICOLON);
                        }
                );

        stringBuilder.append(AnnotationProcessorConstants.BRACE_CLOSED);

        System.out.println(stringBuilder.toString());
    }

    private AnnotationInfo processAnnotatedClass(ClassOrInterfaceDeclaration clazz) {
        AnnotationExpr annotationExpr = clazz.getAnnotations().stream()
                .filter(ann -> ann.getName().asString().equals("GenerateApi"))
                .findFirst().get();

        AnnotationInfo annotationInfo = new AnnotationInfo();
        if (annotationExpr instanceof NormalAnnotationExpr normalAnnotationExpr) {
            normalAnnotationExpr.getPairs().forEach(
                    pair -> {
                        switch (pair.getNameAsString()) {
                            case GenerateApi.ENTITY_PACKAGE -> annotationInfo.setEntityPackage(pair.getValue().toString());
                            case GenerateApi.REPOSITORY_PACKAGE -> annotationInfo.setRepositoryPackage(pair.getValue().toString());
                            case GenerateApi.SERVICE_PACKAGE -> annotationInfo.setServicePackage(pair.getValue().toString());
                            case GenerateApi.CONTROLLER_PACKAGE -> annotationInfo.setControllerPackage(pair.getValue().toString());
                        }
                    }
            );
        }
        return annotationInfo;
    }
}
