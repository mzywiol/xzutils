package xz.util.calc;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by eXistenZ on 19.11.14.
 */
public class CalcTest
{
    Calc calc;

    @Before
    public void before()
    {
        calc = new Calc();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNullInput()
    {
        calc.compute(null);
    }

    @Test
    public void shouldReturn0ForEmptyFormula()
    {
        assertEquals(0, calc.compute(""));
    }

    @Test
    public void shouldReturnNumberForSingleNumberInput()
    {
        assertEquals(17, calc.compute("17"));
    }

    @Test(expected = FormulaParseException.class)
    public void shouldThrowNumberFormatExceptionWhenInputNotParseable()
    {
        calc.compute("a");
    }

    @Test(expected = FormulaParseException.class)
    public void shouldThrowParseExceptionWhenNumberFollowsNumber()
    {
        calc.compute("17 2");
    }

    @Test
    public void shouldAddTwoNumbers()
    {
        assertEquals(19, calc.compute("17 + 2"));
    }

    @Test
    public void shouldAddThreeNumbers()
    {
        assertEquals(50, calc.compute("17 + 2+31"));
    }

    @Test(expected = FormulaParseException.class)
    public void shouldThrowExceptionWhenFormulaStartsWithOperator()
    {
        calc.compute("+5");
    }

    @Test(expected = FormulaParseException.class)
    public void shouldThrowExceptionWhenFormulaEndsWithOperator()
    {
        calc.compute("5+");
    }

    @Test(expected = FormulaParseException.class)
    public void shouldThrowExceptionWhenOperatorIsUnrecognized()
    {
        calc.compute("7#5");
    }

    @Test
    public void shouldSubtract()
    {
        assertEquals(15, calc.compute("17- 2"));
    }

    @Test
    public void shouldSubtractAndAddFromLeftToRight()
    {
        assertEquals(11, calc.compute("17- 8 +2"));
    }

    @Test
    public void canStartWithNegativeNumber()
    {
        assertEquals(12, calc.compute("-5+ 17"));
        assertEquals(12, calc.compute("  -  5   +  17   "));
    }

    @Test
    public void shouldMultiply()
    {
        assertEquals(30, calc.compute("  15 *2"));
    }

    @Test
    public void shouldMultiplyBeforeAddition()
    {
        assertEquals(33, calc.compute("  3+15 *2"));
    }

    @Test
    public void shouldDivide()
    {
        assertEquals(6, calc.compute("  18 /3"));
    }

    @Test
    public void shouldComputeLeftToRightWithPriorities()
    {
        assertEquals(-5, calc.compute(" 5 -2*6 + 8/ 4"));
    }

    @Test
    public void shouldDivideBeforeSubtraction()
    {
        assertEquals(24, calc.compute("30-  18 /3"));
    }

    @Test
    public void shouldStartWithParenthesis()
    {
        assertEquals(4, calc.compute("(30 -18)/ 3"));
    }

    @Test
    public void shouldStartWithInnermostParenthesis()
    {
        assertEquals(4, calc.compute("(6* (22 -20) )/ 3"));
    }

    @Test
    public void shouldKeepParenthesisSeparate()
    {
        assertEquals(23, calc.compute("(6+ 22) -(20/ 4)"));
    }

    @Test
    public void shouldParseNegativeIntegers()
    {
        assertEquals(4, calc.compute("(-6* (20 -22) )/ 3"));
    }

    @Test
    public void sandbox()
    {
        //assertTrue(Pattern.compile("a" + Pattern.quote("+")).matcher("a+").matches());

//        String op = "(\\+|-)";
//        String oppat = "((?<=" + op + ")|(?=" + op + "))";
//        System.out.println(Arrays.toString(" + b - ".split(oppat)));
//        Pattern pat = Pattern.compile(oppat);
//        Matcher m = pat.matcher("a+b");

        Pattern p = Pattern.compile("\\([^()]*\\)");
        String s = "(30 (22-18))/ (5-2)";
        Matcher m = p.matcher(s);
        m.find();
        System.out.println(m.groupCount());
        System.out.println(m.group(0));
        System.out.println(s.substring(0, m.start()) + "x" + s.substring(m.end()));

    }
}
