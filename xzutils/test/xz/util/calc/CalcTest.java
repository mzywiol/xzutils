package xz.util.calc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by eXistenZ on 19.11.14.
 */
public class CalcTest
{

   @Test
   public void shouldReturn0ForEmptyFormula()
   {
       assertEquals(0, Calc.calculate(""));
   }
}
