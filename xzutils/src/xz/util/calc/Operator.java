package xz.util.calc;

/**
* Created by eXistenZ on 20.11.14.
*/
abstract class Operator
{
    public static final Operator ADD = new Operator("+", 1)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] + ops[1];
        }
    };
    public static final Operator SUBTRACT = new Operator("-", 1)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] - ops[1];
        }
    };
    public static final Operator MULTIPLY = new Operator("*", 2)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] * ops[1];
        }
    };
    public static final Operator DIVIDE = new Operator("/", 2)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] / ops[1];
        }
    };

    public static final Operator MODULO = new Operator("%", 2)
    {
        @Override
        public int perform(int... ops)
        {
            return ops[0] % ops[1];
        }
    };
    public static final Operator POWER = new Operator("^", 3)
    {
        @Override
        public int perform(int... ops) {
            return (int) Math.pow(ops[0], ops[1]);
        }
    };

    final String symbol;
    final int priority;

    Operator(String symbol, int priority)
    {
        this.symbol = symbol;
        this.priority = priority;
    }

    public abstract int perform(int... ops);

    public String getSymbol()
    {
        return symbol;
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
