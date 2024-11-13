package com.antlr.ui.view;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.ParseInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

public class ExpertProfilerTableDataModel extends ProfilerTableDataModel {
    public ParseInfo parseInfo;
    public LinkedHashMap<String, Integer> nameToColumnMap = new LinkedHashMap<>();
    public static final String[] columnNames = {
            "Decision", "Invocations", "Time (ms)", "# DFA states", "LL failover", "Total k",
            "Min SLL k", "Min LL k",
            "Max SLL k", "Max LL k",
            "DFA k", "SLL-ATN k", "LL-ATN k", "Full context", "Ambiguities", "Predicates"
    };

    public static final String[] columnToolTips = {
            "Decision index",
            "# decision invocations",
            "Rough estimate of time (ms) spent in prediction",
            "# DFA states",
            "# of SLL -> LL prediction failovers",
            "Total lookahead symbols examined",
            "Fewest SLL lookahead symbols examined in any decision event",
            "Fewest LL lookahead symbols examined in any decision event",
            "Max SLL lookahead symbols examined in any decision event",
            "Max LL lookahead symbols examined in any decision event",
            "# of DFA transitions during prediction (cache hit)",
            "# of conventional SLL ATN (non-DFA) transitions during prediction (cache miss)",
            "# of full-context LL ATN (non-DFA) transitions during prediction (cache miss)",
            "# of context-sensitive phrases found (not certain to be all)",
            "# of ambiguous input phrases",
            "# of predicate evaluations"
    };

    private final String[] ruleNamesByDecision;

    public ExpertProfilerTableDataModel(ParseInfo parseInfo, Parser parser) {
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
            case 3 -> parseInfo.getDFASize(row);
            case 4 -> decisionInfo.LL_Fallback;
            case 5 -> decisionInfo.LL_TotalLook + decisionInfo.SLL_TotalLook;
            case 6 -> decisionInfo.SLL_MinLook;
            case 7 -> decisionInfo.LL_MinLook;
            case 8 -> decisionInfo.SLL_MaxLook;
            case 9 -> decisionInfo.LL_MaxLook;
            case 10 -> decisionInfo.SLL_DFATransitions;
            case 11 -> decisionInfo.SLL_ATNTransitions;
            case 12 -> decisionInfo.LL_ATNTransitions;
            case 13 -> decisionInfo.contextSensitivities.size();
            case 14 -> decisionInfo.ambiguities.size();
            case 15 -> decisionInfo.predicateEvals.size();
            default -> "n/a";
        };
    }
}
