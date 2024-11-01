package dev.mateas.rag.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface GenerateApi {
    String ENTITY_PACKAGE = "entityPackage";
    String REPOSITORY_PACKAGE = "repositoryPackage";
    String SERVICE_PACKAGE = "servicePackage";
    String CONTROLLER_PACKAGE = "controllerPackage";

    String entityPackage();
    String repositoryPackage();
    String servicePackage();
    String controllerPackage();
    String endpoint() default "";
}
