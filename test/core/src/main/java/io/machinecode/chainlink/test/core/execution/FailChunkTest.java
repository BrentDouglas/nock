package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.EventOrderAccumulator;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.EventOrderTransactionManager;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_CHUNK;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_JOB;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_PROCESS;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_READ;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_STEP;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_WRITE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_CHUNK;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_JOB;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_PROCESS;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_READ;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_STEP;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_WRITE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEGIN_TRANSACTION;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.COMMIT_TRANSACTION;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.ON_CHUNK_ERROR;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.ON_PROCESS_ERROR;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.ON_READ_ERROR;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.ON_WRITE_ERROR;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.PROCESS;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READ;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READER_CHECKPOINT;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READER_CLOSE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READER_OPEN;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.ROLLBACK_TRANSACTION;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public abstract class FailChunkTest extends OperatorTest {

    protected TransactionManager _transactionManager() throws Exception {
        return new EventOrderTransactionManager(180, TimeUnit.SECONDS);
    }

    @Test
    public void failReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("failReadEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-read-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void failProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("sixEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("failEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-process-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void failWriteChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setReader(Jsl.reader().setRef("sixEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("failWriteEventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-write-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,

                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,

                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    // Checkpoint

    @Test
    public void failReadCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("failCheckpointEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-read-checkpoint", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT /* throws */,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void failWriteCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setReader(Jsl.reader().setRef("sixEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("failCheckpointEventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-write-checkpoint", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT /* throws */,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    // Open

    @Test
    public void failReaderOpenChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("failOpenEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-read-open", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN /* throws */,
                READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void failWriterOpenChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("neverEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("failOpenEventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-write-open", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN /* throws */,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    // Close

    @Test
    public void failReaderCloseChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("failCloseEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-read-close", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE /* throws */,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void failWriterCloseChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("neverEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("failCloseEventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-write-close", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE /* throws */, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }
}
