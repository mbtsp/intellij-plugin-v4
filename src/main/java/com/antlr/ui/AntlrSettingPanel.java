package com.antlr.ui;

import com.antlr.language.psrsing.CaseChangingStrategy;
import com.antlr.setting.configdialogs.AntlrGrammarProperties;
import com.antlr.setting.configdialogs.AntlrToolGrammarPropertiesStore;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class AntlrSettingPanel {
    private JPanel root;
    private JCheckBox autoGenerateParsersUponCheckBox;
    private TextFieldWithBrowseButton outputDirField;
    private TextFieldWithBrowseButton libDirField;
    private JBTextField fileEncodingField;
    private JBTextField packageField;
    private JBTextField languageField;
    private JComboBox<CaseChangingStrategy> caseTransformation;
    private JCheckBox generateParseTreeListenerCheckBox;
    private JCheckBox generateParseTreeVisitorCheckBox;

    public AntlrSettingPanel(Project project, String qualFileName, boolean isSetting) {
        $$$setupUI$$$();
        for (CaseChangingStrategy value : CaseChangingStrategy.values()) {
            caseTransformation.addItem(value);
        }
        initFields(project, qualFileName);
        if (isSetting) {
            generateParseTreeListenerCheckBox.setVisible(false);
            generateParseTreeVisitorCheckBox.setVisible(false);
            autoGenerateParsersUponCheckBox.setVisible(false);
        }

    }

    private void initFields(Project project, String quailFileName) {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
        outputDirField.addActionListener(new ComponentWithBrowseButton.BrowseFolderActionListener<>("Select Output Dir", null, outputDirField, project, fileChooserDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT));
        outputDirField.setTextFieldPreferredWidth(50);

        libDirField.addActionListener(new ComponentWithBrowseButton.BrowseFolderActionListener<>("Select Lib Dir", null, outputDirField, project, fileChooserDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT));
        libDirField.setTextFieldPreferredWidth(50);
        initData(project, quailFileName);
    }

    public void initData(Project project, String quailFileName) {
        AntlrGrammarProperties antlrGrammarProperties = AntlrToolGrammarPropertiesStore.Companion.getGrammarProperties(project, quailFileName);
        if (antlrGrammarProperties != null) {
            autoGenerateParsersUponCheckBox.setSelected(antlrGrammarProperties.shouldAutoGenerateParser());
            outputDirField.setText(antlrGrammarProperties.getOutputDir());
            libDirField.setText(antlrGrammarProperties.getLibDir());
            fileEncodingField.setText(antlrGrammarProperties.getEncoding());
            packageField.setText(antlrGrammarProperties.getPkg());
            languageField.setText(antlrGrammarProperties.getLanguage());
            caseTransformation.setSelectedItem(antlrGrammarProperties.getCaseChangingStrategy());
            generateParseTreeListenerCheckBox.setSelected(antlrGrammarProperties.shouldGenerateParseTreeListener());
            generateParseTreeVisitorCheckBox.setSelected(antlrGrammarProperties.shouldGenerateParseTreeVisitor());
        }
    }

    public void saveValues(Project project, String qualFileName) {
        AntlrGrammarProperties grammarProperties = AntlrToolGrammarPropertiesStore.Companion.getOrCreateGrammarProperties(project, qualFileName);
        if (grammarProperties == null) {
            return;
        }
        grammarProperties.autoGen = autoGenerateParsersUponCheckBox.isSelected();
        grammarProperties.outputDir = outputDirField.getText();
        grammarProperties.libDir = libDirField.getText();
        grammarProperties.encoding = fileEncodingField.getText();
        grammarProperties.pkg = packageField.getText();
        grammarProperties.language = languageField.getText();
        grammarProperties.caseChangingStrategy = (CaseChangingStrategy) caseTransformation.getSelectedItem();
        grammarProperties.generateListener = generateParseTreeListenerCheckBox.isSelected();
        grammarProperties.generateVisitor = generateParseTreeVisitorCheckBox.isSelected();
    }

    public boolean isModified(AntlrGrammarProperties originalProperties) {
        return !Objects.equals(originalProperties.getOutputDir(), outputDirField.getText())
                || !Objects.equals(originalProperties.getLibDir(), libDirField.getText())
                || !Objects.equals(originalProperties.getEncoding(), fileEncodingField.getText())
                || !Objects.equals(originalProperties.getPkg(), packageField.getText())
                || !Objects.equals(originalProperties.getLanguage(), languageField.getText())
                || !Objects.equals(originalProperties.caseChangingStrategy, caseTransformation.getSelectedItem());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(10, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JBLabel jBLabel1 = new JBLabel();
        jBLabel1.setText("Output directory where all output is generated");
        root.add(jBLabel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(290, 17), null, 0, false));
        outputDirField = new TextFieldWithBrowseButton();
        root.add(outputDirField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        autoGenerateParsersUponCheckBox = new JCheckBox();
        autoGenerateParsersUponCheckBox.setText("Auto-generate parsers upon save");
        root.add(autoGenerateParsersUponCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(290, 24), null, 0, false));
        final JBLabel jBLabel2 = new JBLabel();
        jBLabel2.setText("Location of imported grammars");
        root.add(jBLabel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        libDirField = new TextFieldWithBrowseButton();
        root.add(libDirField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(290, 14), null, 0, false));
        final JBLabel jBLabel3 = new JBLabel();
        jBLabel3.setText("Grammar file encoding; e.g., euc-jp");
        root.add(jBLabel3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fileEncodingField = new JBTextField();
        root.add(fileEncodingField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JBLabel jBLabel4 = new JBLabel();
        jBLabel4.setText("Package/namespace for the generated code");
        root.add(jBLabel4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        packageField = new JBTextField();
        root.add(packageField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JBLabel jBLabel5 = new JBLabel();
        jBLabel5.setText("Language (e.g., Java, Python2, CSharp, ...)");
        root.add(jBLabel5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        languageField = new JBTextField();
        root.add(languageField, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JBLabel jBLabel6 = new JBLabel();
        jBLabel6.setText("Case transformation in the Preview window");
        root.add(jBLabel6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        caseTransformation = new JComboBox();
        root.add(caseTransformation, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateParseTreeListenerCheckBox = new JCheckBox();
        generateParseTreeListenerCheckBox.setText("generate parse tree listener (default)");
        root.add(generateParseTreeListenerCheckBox, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateParseTreeVisitorCheckBox = new JCheckBox();
        generateParseTreeVisitorCheckBox.setText("generate parse tree visitor");
        root.add(generateParseTreeVisitorCheckBox, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }
}
