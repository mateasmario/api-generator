package dev.mateas.rag;

import dev.mateas.rag.processor.AnnotationProcessor;
import dev.mateas.rag.processor.impl.SpringBootAnnotationProcessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE)
    public class GenerateMojo extends AbstractMojo {
        private static final Logger logger = LoggerFactory.getLogger(GenerateMojo.class);

        @Parameter(defaultValue = "${project}", required = true, readonly = true)
        private MavenProject project;

        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
            logger.info("A GenerateMojo instance has been successfully created.");
            AnnotationProcessor annotationProcessor = new SpringBootAnnotationProcessor();
            annotationProcessor.process(new File(project.getCompileSourceRoots().get(0)));
        }
    }
