package dev.mateas.rag.processor.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.quality.NotNull;
import dev.mateas.rag.annotations.GenerateApi;
import dev.mateas.rag.constants.AnnotationProcessorConstants;
import dev.mateas.rag.entity.AnnotationInfo;
import dev.mateas.rag.generator.JavaClassCodeBuilder;
import dev.mateas.rag.processor.AnnotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SpringBootAnnotationProcessor implements AnnotationProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootAnnotationProcessor.class);

    @Override
    public void process(@NotNull File file) {
        logger.info("Processing file " + file + ".");

        if (file.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(this::process);
        } else {
            processFile(file);
        }
    }

    private void processFile(File file) {
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);

            PackageDeclaration packageDeclaration = compilationUnit.getPackageDeclaration().orElse(null);
            NodeList<ImportDeclaration> importDeclarations = compilationUnit.getImports();

            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(clazz -> clazz.getAnnotations().stream()
                            .anyMatch(ann -> ann.getName().asString().equals(GenerateApi.class.getSimpleName())))
                    .forEach(result -> generate(result, packageDeclaration, importDeclarations));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generate(ClassOrInterfaceDeclaration clazz, PackageDeclaration packageDeclaration, NodeList<ImportDeclaration> importDeclarations) {
        logger.info("Identified @GenerateApi annotation inside class " + clazz.getFullyQualifiedName() + ".");
        AnnotationInfo annotationInfo = processAnnotatedClass(clazz);
        generateEntity(clazz, packageDeclaration, importDeclarations);
    }


    private void generateEntity(ClassOrInterfaceDeclaration clazz, PackageDeclaration packageDeclaration, NodeList<ImportDeclaration> importDeclarations) {
        List<FieldDeclaration> fieldDeclarationList = clazz.getFields();

        JavaClassCodeBuilder javaClassCodeBuilder = new JavaClassCodeBuilder();
        javaClassCodeBuilder.appendPackage(packageDeclaration.getNameAsString());

        importDeclarations.forEach(
            importDeclaration -> {
                if (!importDeclaration.getNameAsString().equals(GenerateApi.class.getName())) {
                    javaClassCodeBuilder.appendImport(importDeclaration.getNameAsString());
                }
            }
        );

        javaClassCodeBuilder.appendClass(clazz.getNameAsString());

        fieldDeclarationList
                .stream()
                .map(FieldDeclaration::getVariables)
                .map(variable -> variable.get(0))
                .forEach(
                        variable -> {
                            javaClassCodeBuilder.appendVariable(variable.getTypeAsString(), variable.getNameAsString());
                        }
                );

        javaClassCodeBuilder.appendEnd();

        logger.info(javaClassCodeBuilder.toString());
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
