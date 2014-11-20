package xz.util.calc;

/**
* Created by eXistenZ on 20.11.14.
*/
abstract class Operator
{
    final String symbol;
    final int priority;
    final boolean unary;
    final int unaryNeutralValue = 0;

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

    public abstract int perform(int... ops);

    public boolean isUnary()
    {
        return unary;
    }

    public int getPriority()
    {
        return priority;
    }

    @Override
    public String toString()
    {
        return "Operator " + symbol;
    }
}
