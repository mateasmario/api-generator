package dev.mateas.rag.generator;

import dev.mateas.rag.constants.AnnotationProcessorConstants;

public class JavaClassCodeBuilder {
    private StringBuilder stringBuilder = new StringBuilder();

    public JavaClassCodeBuilder appendPackage(String packageName) {
        stringBuilder.append(AnnotationProcessorConstants.KEYWORD_PACKAGE)
                .append(AnnotationProcessorConstants.SPACE)
                .append(packageName)
                .append(AnnotationProcessorConstants.SEMICOLON)
                .append(AnnotationProcessorConstants.NEWLINE);

        return this;
    }

    public JavaClassCodeBuilder appendImport(String importName) {
        stringBuilder.append(AnnotationProcessorConstants.KEYWORD_IMPORT);
        stringBuilder.append(AnnotationProcessorConstants.SPACE);
        stringBuilder.append(importName);
        stringBuilder.append(AnnotationProcessorConstants.SEMICOLON);
        stringBuilder.append(AnnotationProcessorConstants.NEWLINE);

        return this;
    }

    public JavaClassCodeBuilder appendClass(String className) {
        stringBuilder.append(AnnotationProcessorConstants.MODIFIER_PUBLIC)
                .append(AnnotationProcessorConstants.SPACE)
                .append(AnnotationProcessorConstants.KEYWORD_CLASS)
                .append(AnnotationProcessorConstants.SPACE)
                .append(className)
                .append(AnnotationProcessorConstants.SPACE)
                .append(AnnotationProcessorConstants.BRACE_OPEN)
                .append(AnnotationProcessorConstants.SPACE)
                .append(AnnotationProcessorConstants.NEWLINE);

        return this;
    }

    public JavaClassCodeBuilder appendVariable(String type, String name) {
        stringBuilder.append(AnnotationProcessorConstants.TAB)
                .append(AnnotationProcessorConstants.MODIFIER_PRIVATE)
                .append(AnnotationProcessorConstants.SPACE)
                .append(type)
                .append(AnnotationProcessorConstants.SPACE)
                .append(name)
                .append(AnnotationProcessorConstants.SEMICOLON)
                .append(AnnotationProcessorConstants.NEWLINE);

        return this;
    }

    public JavaClassCodeBuilder appendEnd() {
        stringBuilder.append(AnnotationProcessorConstants.BRACE_CLOSED);

        return this;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
