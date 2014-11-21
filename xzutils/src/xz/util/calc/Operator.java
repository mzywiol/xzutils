package xz.util.calc;

/**
* Created by eXistenZ on 20.11.14.
*/
abstract class Operator
{
    final String symbol;
    final int priority;

    Operator(String symbol, int priority)
    {
        this.symbol = symbol;
        this.priority = priority;
    }

    public abstract int perform(int... ops);

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
