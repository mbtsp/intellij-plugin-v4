package com.antlr.ui.view;

import org.antlr.v4.runtime.tree.Tree;

public interface ParsingResultSelectionListener {
    void onParserRuleSelected(Tree rule);
}
