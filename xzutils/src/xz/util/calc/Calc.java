package xz.util.calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Calc
{
    private final String OPERATORS_PATTERN;
    private final String OPERATORS_DELIMITER;
    private final Map<String, Operator> operators = new HashMap<>();
    private final int MAX_PRIORITY;
    private final int MIN_PRIORITY;
    private final Operator ADD = new Operator("+", 1)
    {
        @Override
        public int perform(int leftOperand, int rightOperand)
        {
            return leftOperand + rightOperand;
        }
    };
    private final Operator SUBTRACT = new Operator("-", 1, true)
    {
        @Override
        public int perform(int leftOperand, int rightOperand)
        {
            return leftOperand - rightOperand;
        }
    };
    private final Operator MULTIPLY = new Operator("*", 2)
    {
        @Override
        public int perform(int leftOperand, int rightOperand)
        {
            return leftOperand * rightOperand;
        }
    };
    private final Operator DIVIDE = new Operator("/", 2)
    {
        @Override
        public int perform(int leftOperand, int rightOperand)
        {
            return leftOperand / rightOperand;
        }
    };

    public int compute(String formula) throws FormulaParseException
    {
        if (formula == null)
            throw new IllegalArgumentException();

        List<String> tokens = Arrays.asList(splitFormula(formula)).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        try
        {
            for (int priority = MAX_PRIORITY; priority >= MIN_PRIORITY; priority--)
            {
                tokens = performOperationsWithPriority(tokens, priority);
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new FormulaParseException("Parse error.", e);
        }

        if (tokens.size() > 1)
            throw new FormulaParseException("More than one value left after processing formula.");

        return tokens.size() == 0 ? 0 : parseValue(getToken(tokens, 0));
    }

    private List<String> performOperationsWithPriority(List<String> tokens, int priority)
    {
        int leftOpIdx = 0;

        while (true)
        {
            int opIdx = leftOpIdx + 1;
            int rightOpIdx = leftOpIdx + 2;

            if (rightOpIdx > tokens.size())
                break;

            Operator op = parseOperator(tokens, leftOpIdx);
            if (op != null && op.isUnary())
            {
                tokens.set(1, getToken(tokens, leftOpIdx) + getToken(tokens, opIdx));
                tokens = remainder(tokens, opIdx);
            }
            int leftOperand = parseValue(getToken(tokens, leftOpIdx));
            op = parseOperator(tokens, opIdx);
            if (op.getPriority() != priority)
            {
                leftOpIdx += 2;
                continue;
            }
            int rightOperand = parseValue(getToken(tokens, rightOpIdx));
            int result = op.perform(leftOperand, rightOperand);
            tokens = replaceSublistWithEntry(tokens, Integer.toString(result), leftOpIdx, rightOpIdx);
        }
        return tokens;
    }

    private <T> List<T> replaceSublistWithEntry(List<T> source, T object, int replaceStart, int replaceEnd)
    {
        List<T> result = new ArrayList<>(source.size() + replaceEnd - replaceStart);
        result.addAll(source.subList(0, replaceStart));
        result.add(object);
        result.addAll(source.subList(replaceEnd + 1, source.size()));
        return result;
    }

    private List<String> remainder(List<String> tokens, int from)
    {
        return tokens.subList(from, tokens.size());
    }

    private Operator parseOperator(List<String> tokens, int index)
    {
        return operators.get(getToken(tokens, index));
    }

    private String getToken(List<String> tokens, int index)
    {
        return tokens.get(index);
    }

    private int parseValue(String s)
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            throw new FormulaParseException("Unable to parse value: " + s);
        }
    }

    private String[] splitFormula(String formula)
    {
        if (formula.trim().length() == 0)
            return new String[] {};

        return formula.split(OPERATORS_DELIMITER);
    }

    public Calc()
    {
        operators.put("+", ADD);
        operators.put("-", SUBTRACT);
        operators.put("*", MULTIPLY);
        operators.put("/", DIVIDE);


        MAX_PRIORITY = operators.values().stream().mapToInt(Operator::getPriority).max().getAsInt();
        MIN_PRIORITY = operators.values().stream().mapToInt(Operator::getPriority).min().getAsInt();
        OPERATORS_PATTERN = compileOperatorPattern();
        OPERATORS_DELIMITER = "((?<=" + OPERATORS_PATTERN + ")|(?=" + OPERATORS_PATTERN + "))";
    }

    private String compileOperatorPattern()
    {
        StringJoiner operatorPatternJoiner = new StringJoiner("|", "(", ")");
        for (String operatorSymbol : operators.keySet())
        {
            operatorPatternJoiner.add(Pattern.quote(operatorSymbol));
        }
        return operatorPatternJoiner.toString();
    }

    public static int calculate(String formula) throws IllegalArgumentException, FormulaParseException
    {

        Calc calculation = new Calc();

        calculation.compute(formula);

        return 0;
    }

    private abstract static class Operator
    {
        final String symbol;
        final int priority;
        final boolean unary;

        Operator(String symbol, int priority, boolean unary)
        {
            this.symbol = symbol;
            this.priority = priority;
            this.unary = unary;
        }

        Operator(String symbol, int priority)
        {
            this(symbol, priority, false);
        }

        public abstract int perform(int leftOperand, int rightOperand);

        public boolean isUnary()
        {
            return unary;
        }

        public int getPriority()
        {
            return priority;
        }
    }
}
