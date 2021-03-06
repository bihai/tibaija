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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.xlrnet.tibaija.exception.IllegalControlFlowException;
import org.xlrnet.tibaija.memory.Variables;

/**
 * Tests for control flow logic.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecuteControlFlowTest extends AbstractTI83PlusTest {

    @Test(expected = IllegalControlFlowException.class)
    public void testExecute_invalidProgram_controlFlow_else_without_then() {
        storeAndExecute(":If 0" +
                ":1" +
                ":Else" +
                ":2" +
                ":End");
    }

    @Test(expected = IllegalControlFlowException.class)
    public void testExecute_invalidProgram_controlFlow_while_then() {
        storeAndExecute(":While 1" +
                ":Then" +
                ":End");
    }

    @Test(expected = IllegalControlFlowException.class)
    public void testExecute_invalidProgram_controlFlow_while_then_skipped() {
        storeAndExecute(":While 0" +
                ":Then" +
                ":End");
    }

    @Test
    public void testExecute_validProgram_controlFlow_if_else_false() {
        storeAndExecute(":If 0:Then" +
                ":2" +
                ":Ans + 3" +
                ":Else" +
                ":Ans + 7" +
                ":End");
        verifyLastResultValue(7);
    }

    @Test
    public void testExecute_validProgram_controlFlow_if_else_true() {
        storeAndExecute(":If 1:Then" +
                ":2" +
                ":Ans + 3" +
                ":Else" +
                ":Ans + 7" +
                ":End");
        verifyLastResultValue(5);
    }

    @Test
    public void testExecute_validProgram_controlFlow_if_single_false() {
        storeAndExecute(":If 0" +
                ":2" +
                ":Ans + 3");
        verifyLastResultValue(3);
    }

    @Test
    public void testExecute_validProgram_controlFlow_if_single_true() {
        storeAndExecute(":If 1" +
                ":2" +
                ":Ans + 3");
        verifyLastResultValue(5);
    }

    @Test
    public void testExecute_validProgram_controlFlow_if_then_false() {
        storeAndExecute(":9" +
                ":If 0:Then" +
                ":2" +
                ":Ans + 3" +
                ":End");
        verifyLastResultValue(9);
    }

    @Test
    public void testExecute_validProgram_controlFlow_if_then_true() {
        storeAndExecute(":If 1:Then" +
                ":2" +
                ":Ans + 3" +
                ":End");
        verifyLastResultValue(5);
    }

    @Test
    public void testExecute_validProgram_controlFlow_nested_single_false() {
        storeAndExecute(":If 0:Then" +
                ":If 1" +
                ":1" +
                ":Else" +
                ":2" +
                ":End");
        verifyLastResultValue(2);
    }

    @Test
    public void testExecute_validProgram_controlFlow_nested_then_false() {
        storeAndExecute(":If 0:Then" +
                ":If 1:Then" +
                ":1" +
                ":Else" +
                ":2" +
                ":End" +
                ":Else" +
                ":3" +
                ":End");
        verifyLastResultValue(3);
    }

    @Test(timeout = 500L)
    public void testExecute_validProgram_controlFlow_repeat_false_nested_stop() {
        storeAndExecute(":1→A:1→B" +
                ":Repeat 0" +
                ":Repeat 1" +
                ":B+1→B" +
                ":End" +
                ":A+1→A" +
                ":If A=5:Stop" +
                ":End");
        assertNumberVariableValue(Variables.NumberVariable.A, 5, 0);
        assertNumberVariableValue(Variables.NumberVariable.B, 5, 0);
    }

    @Test(timeout = 500L)
    public void testExecute_validProgram_controlFlow_repeat_false_stop() {
        // Repeat loop must be called infinitely and then be stopped manually
        storeAndExecute(":0→A" +
                ":Repeat 0" +
                ":A+1→A" +
                ":If A=5:Stop" +
                ":End");
        assertNumberVariableValue(Variables.NumberVariable.A, 5, 0);
    }

    @Test
    public void testExecute_validProgram_controlFlow_repeat_increment() {
        storeAndExecute(":0→A" +
                ":Repeat A>5" +
                ":A+1→A" +
                ":End");
        verifyLastResultValue(6);
        assertNumberVariableValue(Variables.NumberVariable.A, 6, 0);
    }

    @Test
    public void testExecute_validProgram_controlFlow_repeat_skip() {
        storeAndExecute(":1:" +
                ":If 0:Then" +
                ":Repeat 1" +
                ":2" +
                ":End" +
                ":End");
        verifyLastResultValue(1);
    }

    @Test
    public void testExecute_validProgram_controlFlow_repeat_true_stop() {
        // Repeat loop must be called exactly one time and checked at the end
        storeAndExecute(":0→A" +
                ":Repeat 1" +
                ":A+1→A" +
                ":If A=5:Stop" +
                ":End");
        assertNumberVariableValue(Variables.NumberVariable.A, 1, 0);
    }

    @Test
    public void testExecute_validProgram_controlFlow_while_false() {
        storeAndExecute(":1" +
                ":While 0" +
                ":2" +
                ":End");
        verifyLastResultValue(1);
    }

    @Test
    public void testExecute_validProgram_controlFlow_while_false_nested_true() {
        storeAndExecute(":1→A:1→B" +
                ":While 0" +
                ":2→A" +
                ":While 1" +
                ":2→B" +
                ":End" +
                ":End");
        assertNumberVariableValue(Variables.NumberVariable.A, 1, 0);
        assertNumberVariableValue(Variables.NumberVariable.B, 1, 0);
    }

    @Test
    public void testExecute_validProgram_controlFlow_while_increment() {
        storeAndExecute(":0→A" +
                ":While A<5" +
                ":A+1→A" +
                ":End");
        verifyLastResultValue(5);
        assertNumberVariableValue(Variables.NumberVariable.A, 5, 0);
    }

    @Test
    public void testExecute_validProgram_controlFlow_while_nested() {
        storeAndExecute(":1→A" +
                ":While A<5" +
                ":A+1→A" +
                ":0→B" +
                ":While B<A" +
                ":B+1→B" +
                ":End" +
                ":End");
        assertNumberVariableValue(Variables.NumberVariable.A, 5, 0);
        assertNumberVariableValue(Variables.NumberVariable.B, 5, 0);
    }
}
