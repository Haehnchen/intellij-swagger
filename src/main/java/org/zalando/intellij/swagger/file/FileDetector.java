package org.zalando.intellij.swagger.file;

import com.google.common.collect.Lists;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLPsiElement;
import org.jetbrains.yaml.psi.YAMLValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileDetector {

    private static final String SWAGGER_KEY = "swagger";

    public boolean isMainSwaggerJsonFile(final PsiFile psiFile) {
        return psiFile.getName().equals(FileConstants.MAIN_SWAGGER_JSON_FILE) || hasSwaggerJsonKey(psiFile);
    }

    public boolean isMainSwaggerYamlFile(final PsiFile psiFile) {
        return psiFile.getName().equals(FileConstants.MAIN_SWAGGER_YAML_FILE) ||
                psiFile.getName().equals(FileConstants.MAIN_SWAGGER_YML_FILE) ||
                hasSwaggerYamlKey(psiFile);
    }

    private boolean hasSwaggerYamlKey(final PsiFile psiFile) {
        final List<YAMLPsiElement> children = Optional.of(psiFile)
                .filter(f -> f instanceof YAMLFile)
                .map(YAMLFile.class::cast)
                .map(YAMLFile::getDocuments)
                .map(yamlDocuments -> yamlDocuments.get(0))
                .map(YAMLDocument::getTopLevelValue)
                .map(YAMLValue::getYAMLElements)
                .orElse(Lists.newArrayList());

        return children.stream()
                .anyMatch(psiElement -> psiElement instanceof YAMLKeyValue
                        && SWAGGER_KEY.equals(psiElement.getName()));
    }

    private boolean hasSwaggerJsonKey(final PsiFile psiFile) {
        final List<PsiElement> children = Optional.of(psiFile)
                .filter(f -> f instanceof JsonFile)
                .map(JsonFile.class::cast)
                .map(JsonFile::getTopLevelValue)
                .map(jsonValue -> Arrays.asList(jsonValue.getChildren()))
                .orElse(Lists.newArrayList());

        return children.stream()
                .anyMatch(psiElement -> psiElement instanceof JsonProperty
                        && SWAGGER_KEY.equals(((JsonProperty) psiElement).getName()));
    }

    public boolean isSwaggerFile(final PsiFile file) {
        return isMainSwaggerJsonFile(file) || isMainSwaggerYamlFile(file);
    }

    public boolean isSwaggerContentCompatible(VirtualFile file) {
        return FilenameUtils.isExtension(file.getName(),
                new String[]{FileConstants.JSON_FILE_EXTENSION,
                        FileConstants.YAML_FILE_EXTENSION,
                        FileConstants.YML_FILE_EXTENSION});
    }
}
