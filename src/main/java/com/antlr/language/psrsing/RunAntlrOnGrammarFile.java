package com.antlr.language.psrsing;

import com.antlr.preview.PreviewState;
import com.antlr.service.AntlrService;
import com.antlr.setting.configdialogs.AntlrGrammarProperties;
import com.antlr.util.AntlrUtil;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.Grammar;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

import static com.antlr.setting.configdialogs.AntlrToolGrammarPropertiesStore.getGrammarProperties;

public class RunAntlrOnGrammarFile extends Task.Backgroundable {
    public static final Logger LOG = Logger.getInstance("RunANTLROnGrammarFile");
    public static final String OUTPUT_DIR_NAME = "gen";
    public static final String groupDisplayId = "ANTLR 4 Parser Generation";

    private static final Pattern PACKAGE_DEFINITION_REGEX = Pattern.compile("package\\s+[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_];");

    private final VirtualFile grammarFile;
    private final Project project;
    private final boolean forceGeneration;

    public RunAntlrOnGrammarFile(VirtualFile grammarFile,
                                 @NotNull final Project project,
                                 @NotNull final String title,
                                 final boolean canBeCancelled,
                                 boolean forceGeneration) {
        super(project, title, canBeCancelled);
        this.grammarFile = grammarFile;
        this.project = project;
        this.forceGeneration = forceGeneration;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        AntlrGrammarProperties grammarProperties = getGrammarProperties(project, grammarFile);
        boolean autogen = false;
        if (grammarProperties != null) {
            autogen = grammarProperties.shouldAutoGenerateParser();
        }
        if (forceGeneration || (autogen && isGrammarStale(grammarProperties))) {
            ReadAction.run(() -> AntlrUtil.INSTANCE.antlr(project, grammarFile));
        } else {
            AntlrService antlrService = project.getService(AntlrService.class);
            final PreviewState previewState = antlrService.previewState(grammarFile);
            // is lexer file? gen .tokens file no matter what as tokens might have changed;
            // a parser that feeds off of that file will need to see the changes.
            if (previewState.getG() == null && previewState.getLg() != null) {
                Grammar g = previewState.getLg();
                String language = g.getOptionString(AntlrGrammarProperties.PROP_LANGUAGE);
                Tool tool = ParsingUtils.createANTLRToolForLoadingGrammars(getGrammarProperties(project, grammarFile));
                CodeGenerator gen = CodeGenerator.create(tool, g, language);
                gen.writeVocabFile();
            }
        }
    }

    // TODO: lots of duplication with antlr() function.
    private boolean isGrammarStale(AntlrGrammarProperties grammarProperties) {
        String sourcePath = grammarProperties.resolveLibDir(project, AntlrUtil.INSTANCE.getParentDir(grammarFile));
        String fullyQualifiedInputFileName = sourcePath + File.separator + grammarFile.getName();

        AntlrService antlrService = project.getService(AntlrService.class);
        final PreviewState previewState = antlrService.previewState(grammarFile);
        Grammar g = previewState.getMainGrammar();
        // Grammar should be updated in the preview state before calling this function
        if (g == null) {
            return false;
        }

        String language = g.getOptionString(AntlrGrammarProperties.PROP_LANGUAGE);
        CodeGenerator generator = CodeGenerator.create(null, g, language);
        String recognizerFileName = generator.getRecognizerFileName();

        VirtualFile contentRoot = AntlrUtil.INSTANCE.getContentRoot(project, grammarFile);
        if (contentRoot == null) {
            return false;
        }
        String package_ = grammarProperties.getPkg();
        String outputDirName = grammarProperties.resolveOutputDirName(project, contentRoot, package_);
        String fullyQualifiedOutputFileName = outputDirName + File.separator + recognizerFileName;

        File inF = new File(fullyQualifiedInputFileName);
        File outF = new File(fullyQualifiedOutputFileName);
        boolean stale = inF.lastModified() > outF.lastModified();
        LOG.info((!stale ? "not" : "") + "stale: " + fullyQualifiedInputFileName + " -> " + fullyQualifiedOutputFileName);
        return stale;
    }


    public String getOutputDirName() {
        VirtualFile contentRoot = AntlrUtil.INSTANCE.getContentRoot(project, grammarFile);
        Map<String, String> argMap = AntlrUtil.INSTANCE.getArgs(project, grammarFile);
        String package_ = argMap.get("-package");

        return getGrammarProperties(project, grammarFile)
                .resolveOutputDirName(project, contentRoot, package_);
    }
}
