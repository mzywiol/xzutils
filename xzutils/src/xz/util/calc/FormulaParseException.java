package xz.util.calc;

/**
 * Created by eXistenZ on 19.11.14.
 */
public class FormulaParseException extends RuntimeException
{
    public FormulaParseException(String message)
    {
        super(message);
    }

    public FormulaParseException(String message, Exception e)
    {
        super(message, e);
    }
}
