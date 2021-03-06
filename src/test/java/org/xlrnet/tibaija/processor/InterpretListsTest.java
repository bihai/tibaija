/*
 * Copyright (c) 2015 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE
 */

package org.xlrnet.tibaija.processor;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.xlrnet.tibaija.exception.PreprocessException;
import org.xlrnet.tibaija.exception.TIArgumentException;
import org.xlrnet.tibaija.memory.Value;
import org.xlrnet.tibaija.memory.Variables;

import static org.mockito.Mockito.doReturn;

/**
 * All tests regarding list interpretation.
 */
@RunWith(MockitoJUnitRunner.class)
public class InterpretListsTest extends AbstractTI83PlusTest {

    @Test(expected = TIArgumentException.class)
    public void testInterpret_invalidProgram_add_mismatch() {
        calculator.interpret("{1} + {1,2}");
    }

    @Test(expected = PreprocessException.class)
    public void testInterpret_invalidProgram_listname_digit() {
        calculator.interpret("∟1");
    }

    @Test(expected = PreprocessException.class)
    public void testInterpret_invalidProgram_listname_toolong() {
        calculator.interpret("∟ABCDEF");
    }

    @Test(expected = PreprocessException.class)
    public void testInterpret_invalidProgram_nestedlist() {
        calculator.interpret("{2, {2}");
    }

    @Test(expected = PreprocessException.class)
    public void testInterpret_invalidProgram_nestedlist_variable() {
        doReturn(Value.of(ImmutableList.of(Complex.ONE))).when(mockedMemory).getListVariableValue("A");
        calculator.interpret("{2, ∟A");
    }

    @Test
    public void testInterpret_validProgram_add_two_lists() {
        calculator.interpret("{2+i, 3+2i} + {1,2");
        verifyLastResultValueList(Complex.valueOf(3, 1), Complex.valueOf(5, 2));
    }

    @Test
    public void testInterpret_validProgram_list() {
        calculator.interpret("{1,2,3,456");
        verifyLastResultValueList(1d, 2d, 3d, 456d);
    }

    @Test
    public void testInterpret_validProgram_list_complex() {
        calculator.interpret("{123.456i, ‾12.34");
        verifyLastResultValueList(Complex.valueOf(0, 123.456), Complex.valueOf(-12.34));
    }

    @Test
    public void testInterpret_validProgram_list_single() {
        calculator.interpret("{1}");
        verifyLastResultValueList(Complex.valueOf(1));
    }

    /**
     * Test if any postfix operators work with lists.
     */
    @Test
    public void testInterpret_validProgram_list_variable_postfix() {
        doReturn(Value.of(ImmutableList.of(Complex.valueOf(2), Complex.valueOf(3)))).when(mockedMemory).getListVariableValue("ABC");
        calculator.interpret("∟ABC²");
        verifyLastResultValueList(4d, 9d);
    }

    /**
     * Test if any prefix operators work with lists.
     */
    @Test
    public void testInterpret_validProgram_list_variable_prefix() {
        doReturn(Value.of(ImmutableList.of(Complex.valueOf(4), Complex.valueOf(9)))).when(mockedMemory).getListVariableValue("ABC");
        calculator.interpret("√(∟ABC");
        verifyLastResultValueList(2d, 3d);
    }

    @Test
    public void testInterpret_validProgram_listname_default() {
        doReturn(Value.of(ImmutableList.of(Complex.ONE))).when(mockedMemory).getListVariableValue("₂");
        calculator.interpret("∟₂");
        verifyLastResultValueList(1d);
    }

    @Test
    public void testInterpret_validProgram_listname_digits() {
        doReturn(Value.of(ImmutableList.of(Complex.ONE))).when(mockedMemory).getListVariableValue("A1234");
        calculator.interpret("∟A1234");
        verifyLastResultValueList(1d);
    }

    @Test
    public void testInterpret_validProgram_listname_theta() {
        doReturn(Value.of(ImmutableList.of(Complex.ONE))).when(mockedMemory).getListVariableValue("θ1θ2A");
        calculator.interpret("∟θ1θ2A");
        verifyLastResultValueList(1d);
    }

    @Test
    public void testInterpret_validProgram_multiply_implicit_numbervariable() {
        doReturn(Value.of(2)).when(mockedMemory).getNumberVariableValue(Variables.NumberVariable.A);
        calculator.interpret("{1, 2}A");
        verifyLastResultValueList(Complex.valueOf(2), Complex.valueOf(4));
    }

    @Test
    public void testInterpret_validProgram_multiply_implicit_numbervariable_left() {
        doReturn(Value.of(ImmutableList.of(Complex.valueOf(2)))).when(mockedMemory).getListVariableValue("A");
        doReturn(Value.of(2)).when(mockedMemory).getNumberVariableValue(Variables.NumberVariable.A);
        calculator.interpret("A∟A");
        verifyLastResultValueList(4d);
    }

    @Test
    public void testInterpret_validProgram_multiply_implicit_numbervariable_right() {
        doReturn(Value.of(ImmutableList.of(Complex.valueOf(2)))).when(mockedMemory).getListVariableValue("A");
        doReturn(Value.of(2)).when(mockedMemory).getNumberVariableValue(Variables.NumberVariable.A);
        calculator.interpret("∟A(A");
        verifyLastResultValueList(4d);
    }

    @Test
    public void testInterpret_validProgram_multiply_implicit_single_right_list() {
        calculator.interpret("{1, 2}(2+3.5i)");
        verifyLastResultValueList(Complex.valueOf(2, 3.5), Complex.valueOf(4, 7));
    }

    @Test
    public void testInterpret_validProgram_multiply_single_right_list() {
        calculator.interpret("(2+3.5i) * {1, 2");
        verifyLastResultValueList(Complex.valueOf(2, 3.5), Complex.valueOf(4, 7));
    }

}
