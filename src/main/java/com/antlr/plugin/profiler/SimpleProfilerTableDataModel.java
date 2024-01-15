package com.antlr.plugin.profiler;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.ParseInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

public class SimpleProfilerTableDataModel extends ProfilerTableDataModel {
    public ParseInfo parseInfo;
    public LinkedHashMap<String, Integer> nameToColumnMap = new LinkedHashMap<>();
    public static final String[] columnNames = {
            "Rule", "Invocations", "Time (ms)", "Total k", "Max k", "Ambiguities", "DFA cache miss"
    };

    public static final String[] columnToolTips = {
            "name of rule and decision no",
            "# decision invocations",
            "Rough estimate of time (ms) spent in prediction",
            "Total lookahead symbols examined",
            "Max lookahead symbols examined in any decision event",
            "# of ambiguous input phrases",
            "# of non-DFA transitions during prediction (cache miss)"
    };

    private final String[] ruleNamesByDecision;

    public SimpleProfilerTableDataModel(ParseInfo parseInfo, Parser parser) {
        this.parseInfo = parseInfo;
        /*copying rule names to not hold ref to parser object*/
        ruleNamesByDecision = new String[parser.getATN().decisionToState.size()];
        for (int i = 0; i < ruleNamesByDecision.length; i++) {
            ruleNamesByDecision[i] = parser.getRuleNames()[parser.getATN().getDecisionState(i).ruleIndex];
        }
        for (int i = 0; i < columnNames.length; i++) {
            nameToColumnMap.put(columnNames[i], i);
        }
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public String[] getColumnToolTips() {
        return columnToolTips;
    }

    @Override
    public int getRowCount() {
        return parseInfo.getDecisionInfo().length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        DecisionInfo decisionInfo = parseInfo.getDecisionInfo()[row];
        return switch (col) { // laborious but more efficient than reflection
            case 0 -> String.format("%s (%d)", ruleNamesByDecision[row], row);
            case 1 -> decisionInfo.invocations;
            case 2 ->
                    BigDecimal.valueOf(decisionInfo.timeInPrediction / (1000.0 * 1000.0)).setScale(3, RoundingMode.HALF_DOWN);
            case 3 -> decisionInfo.LL_TotalLook + decisionInfo.SLL_TotalLook;
            case 4 -> Math.max(decisionInfo.LL_MaxLook, decisionInfo.SLL_MaxLook);
            case 5 -> decisionInfo.ambiguities.size();
            case 6 -> decisionInfo.SLL_ATNTransitions +
                    decisionInfo.LL_ATNTransitions;
            default -> "n/a";
        };
    }
}
