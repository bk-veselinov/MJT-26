package bg.sofia.uni.fmi.mjt.pipeline.stage;

import bg.sofia.uni.fmi.mjt.pipeline.step.Step;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageTest {


    @Mock
    private Step<String, Integer> stringToIntStep;

    @Mock
    private Step<Integer, Double> intToDoubleStep;

    @Mock
    private Step<Double, String> doubleToStringStep;

    @Test
    void testStartNullStepThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Stage.start(null);
        });
    }

    @Test
    void testStartNullStepThrowsCorrectMessage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            Stage.start(null);
        });

        assertEquals("initialStep cannot be null", ex.getMessage());
    }

    @Test
    void testStartSuccessfulReturnsStage() {
        Stage<String, Integer> stage = Stage.start(stringToIntStep);

        assertNotNull(stage);
    }

    @Test
    void testAddStepNullStepThrowsIllegalArgumentException() {
        Stage<String, Integer> stage = Stage.start(stringToIntStep);

        assertThrows(IllegalArgumentException.class, () -> {
            stage.addStep(null);
        });
    }

    @Test
    void testAddStepNullStepThrowsCorrectMessage() {
        Stage<String, Integer> stage = Stage.start(stringToIntStep);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stage.addStep(null);
        });

        assertEquals("step cannot be null", ex.getMessage());
    }

    @Test
    void testAddStepSuccessfulReturnsSameStageInstance() {
        Stage<String, Integer> stage = Stage.start(stringToIntStep);

        Stage<String, Double> newStage = stage.addStep(intToDoubleStep);
        assertSame(stage, newStage);
    }

    @Test
    void testExecuteSingleStepReturnsCorrectResult() {
        when(stringToIntStep.process("input")).thenReturn(123);
        Stage<String, Integer> stage = Stage.start(stringToIntStep);

        Integer result = stage.execute("input");
        assertEquals(123, result);
    }

    @Test
    void testExecuteSingleStepCallsProcess() {
        when(stringToIntStep.process("input")).thenReturn(123);
        Stage<String, Integer> stage = Stage.start(stringToIntStep);

        stage.execute("input");
        verify(stringToIntStep).process("input");
    }

    @Test
    void testExecuteMultiStepReturnsFinalResult() {
        when(stringToIntStep.process("start")).thenReturn(10);
        when(intToDoubleStep.process(10)).thenReturn(20.5);
        when(doubleToStringStep.process(20.5)).thenReturn("end");

        Stage<String, String> stage = Stage.start(stringToIntStep)
                .addStep(intToDoubleStep)
                .addStep(doubleToStringStep);

        String result = stage.execute("start");
        assertEquals("end", result);
    }

    @Test
    void testExecuteMultiStepCallsStep1WithCorrectInput() {
        when(stringToIntStep.process("start")).thenReturn(10);
        Stage<String, String> stage = Stage.start(stringToIntStep)
                .addStep(intToDoubleStep)
                .addStep(doubleToStringStep);

        stage.execute("start");
        verify(stringToIntStep).process("start");
    }

    @Test
    void testExecuteMultiStepCallsStep2WithCorrectInput() {
        when(stringToIntStep.process("start")).thenReturn(10);
        when(intToDoubleStep.process(10)).thenReturn(20.5);
        Stage<String, String> stage = Stage.start(stringToIntStep)
                .addStep(intToDoubleStep)
                .addStep(doubleToStringStep);

        stage.execute("start");
        verify(intToDoubleStep).process(10);
    }

    @Test
    void testExecuteMultiStepCallsStep3WithCorrectInput() {
        when(stringToIntStep.process("start")).thenReturn(10);
        when(intToDoubleStep.process(10)).thenReturn(20.5);
        when(doubleToStringStep.process(20.5)).thenReturn("end");
        Stage<String, String> stage = Stage.start(stringToIntStep)
                .addStep(intToDoubleStep)
                .addStep(doubleToStringStep);

        stage.execute("start");
        verify(doubleToStringStep).process(20.5);
    }

    @Test
    void testExecuteHandlesNullInput() {
        when(stringToIntStep.process(null)).thenReturn(123);
        Stage<String, Integer> stage = Stage.start(stringToIntStep);

        stage.execute(null);
        verify(stringToIntStep).process(null);
    }

    @Test
    void testExecuteHandlesNullIntermediateResult() {
        when(stringToIntStep.process("start")).thenReturn(null);
        when(intToDoubleStep.process(null)).thenReturn(20.5);
        Stage<String, Double> stage = Stage.start(stringToIntStep).addStep(intToDoubleStep);

        Double result = stage.execute("start");
        assertEquals(20.5, result);
    }

    @Test
    void testExecuteHandlesNullIntermediateCallsNextStepWithNull() {
        when(stringToIntStep.process("start")).thenReturn(null);
        when(intToDoubleStep.process(null)).thenReturn(20.5);
        Stage<String, Double> stage = Stage.start(stringToIntStep).addStep(intToDoubleStep);

        stage.execute("start");
        verify(intToDoubleStep).process(null);
    }
}