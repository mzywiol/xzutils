package xz.util.calc;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calc
{
    private final Pattern INNERMOST_PARENTHESIS_PATTERN = Pattern.compile("\\([^()]*\\)");
    private final String OPERATORS_PATTERN;
    private final String OPERATORS_DELIMITER;
    private final Map<String, Operator> operators = new HashMap<>();
    private final int MAX_PRIORITY;
    private final int MIN_PRIORITY;
    private final Operator ADD = new Operator("+", 1)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] + ops[1];
        }
    };
    private final Operator SUBTRACT = new Operator("-", 1)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] - ops[1];
        }
    };
    private final Operator MULTIPLY = new Operator("*", 2)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] * ops[1];
        }
    };
    private final Operator DIVIDE = new Operator("/", 2)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] / ops[1];
        }
    };

    public int compute(String formula) throws FormulaParseException
    {
        if (formula == null)
            throw new IllegalArgumentException();

        Matcher parenthesisMatcher = INNERMOST_PARENTHESIS_PATTERN.matcher(formula);
        while (parenthesisMatcher.find())
        {
            String parenthesis = parenthesisMatcher.group();

            formula = formula.substring(0, parenthesisMatcher.start())
                    + Integer.toString(compute(parenthesis.substring(1, parenthesis.length() - 1)))
                    + formula.substring(parenthesisMatcher.end());

            parenthesisMatcher.reset(formula);
        }

        List<Object> tokens;
        try
        {
            tokens = splitFormula(formula);
        }
        catch (NumberFormatException e)
        {
            throw new FormulaParseException("Invalid value.", e);
        }

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

        return tokens.size() == 0 ? 0 : (int) tokens.get(0);
    }

    private List<Object> performOperationsWithPriority(List<Object> tokens, int priority)
    {
        int opIdx = 0;

        while (true)
        {
            if (opIdx >= tokens.size())
                break;

            Object currentToken = tokens.get(opIdx);
            if (!isOperator(currentToken))
            {
                opIdx++;
                continue;
            }

            Operator op =  ((Operator) currentToken);
            if (op.getPriority() != priority)
            {
                opIdx++;
                continue;
            }

            try
            {
                int leftVal = (int) tokens.get(opIdx - 1);
                int rightVal = (int) tokens.get(opIdx + 1);
                tokens = replaceSublistWithEntry(tokens, op.perform(leftVal, rightVal), opIdx - 1, opIdx + 1);
            }
            catch (IndexOutOfBoundsException | NumberFormatException ex)
            {
                throw new FormulaParseException("Operand(s) not found for binary operator: " + op.symbol, ex);
            }
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

    private Operator parseOperator(String token)
    {
        return operators.get(token);
    }

    private boolean isOperator(Object token)
    {
        return token instanceof Operator;
    }

    private int parseValue(String s)
    {
        return Integer.parseInt(s);
    }

    private List<Object> splitFormula(String formula)
    {
        if (formula.trim().length() == 0)
            return new ArrayList<>();

        List<Object> tokens = new ArrayList<>();
        boolean expectValue = true;

        List<String> tokenStrings = Arrays.asList(formula.split(OPERATORS_DELIMITER));
        Iterator<String> tokenIt = tokenStrings.iterator();
        while (tokenIt.hasNext())
        {
            String tok = tokenIt.next().trim();
            if (tok.isEmpty())
                continue;

            if (expectValue)
            {
                tokens.add(consumeValue(tok, tokenIt));
            }
            else
            {
                tokens.add(consumeOperator(tok));
            }
            expectValue = !expectValue;
        }

        return tokens;
    }

    private Operator consumeOperator(String tok)
    {
        Operator op = parseOperator(tok);
        if (op == null)
            throw new FormulaParseException("Expected operator, got: " + tok);

        return op;
    }

    private Integer consumeValue(String tok, Iterator<String> tokenIt)
    {
        Operator op = parseOperator(tok);
        if (op != null)
        {
            if ("-".equals(op.symbol))
            {
                if (!tokenIt.hasNext())
                    throw new FormulaParseException("Unexpected end of formula after operator '-'");

                String nextTok = tokenIt.next().trim();
                return -consumeValue(nextTok, tokenIt);
            }
            else
                throw new FormulaParseException("Expected value, got operator: " + op.toString());
        }
        return parseValue(tok);
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

}
