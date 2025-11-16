package bg.sofia.uni.fmi.mjt.pipeline;

import bg.sofia.uni.fmi.mjt.pipeline.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PipelineTest {

    @Mock
    Stage<Integer, String> stage1;

    @Mock
    Stage<String, String> stage2;

    @Test
    void testStartWithNull() {

        assertThrows(IllegalArgumentException.class,
                () -> Pipeline.start(null),
                "IllegalArgumentException should be thrown when start is called with null");
    }

    @Test
    void testAddStageWithValidData() {
        assertInstanceOf(Pipeline.class, Pipeline.start(stage2),
                "An instance of Pipeline with the types of the stage must be returned");

    }

    @Test
    void testExecuteWithTwoStages() {

        Pipeline<Integer, String> pipeline = Pipeline.start(stage1);
        when(stage1.execute(1)).thenReturn("str");
        when(stage2.execute("str")).thenReturn("output");
        pipeline.addStage(stage2);

        assertEquals("output", pipeline.execute(1));
    }

    @Test
    void testExecuteWithCachedData() {

        Pipeline<Integer, String> pipeline = Pipeline.start(stage1);
        when(stage1.execute(1)).thenReturn("str");
        when(stage2.execute("str")).thenReturn("output");
        pipeline.addStage(stage2);

        pipeline.execute(1);
        pipeline.execute(1);

        Mockito.verify(stage1, times(1)).execute(1);
    }
}