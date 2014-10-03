package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.core.DeferredImpl;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class DistributedProxyExecutionRepository<A, R extends DistributedRegistry<A, R>> implements ExecutionRepository {

    protected final R registry;
    protected final A address;
    protected final ExecutionRepositoryId executionRepositoryId;

    public DistributedProxyExecutionRepository(final R registry, final ExecutionRepositoryId executionRepositoryId, final A address) {
        this.registry = registry;
        this.address = address;
        this.executionRepositoryId = executionRepositoryId;
    }

    protected abstract <T> DistributedCommand<T, A, R> _cmd(final String name, final Serializable... params);

    @Override
    public ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<ExtendedJobInstance,Throwable,Void>();
            registry.invoke(address, this.<ExtendedJobInstance>_cmd("createJobInstance", jobId, jslName, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<ExtendedJobExecution,Throwable,Void>();
            registry.invoke(address, this.<ExtendedJobExecution>_cmd("createJobExecution", jobInstance, parameters, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution createStepExecution(final ExtendedJobExecution jobExecution, final String stepName, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, this.<ExtendedStepExecution>_cmd("createStepExecution", jobExecution, stepName, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<PartitionExecution,Throwable,Void> promise = new DeferredImpl<PartitionExecution,Throwable,Void>();
            registry.invoke(address, this.<PartitionExecution>_cmd("createPartitionExecution", stepExecutionId, partitionId, properties, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("startJobExecution", jobExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("updateJobExecution", jobExecutionId, batchStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("finishJobExecution", jobExecutionId, batchStatus, exitStatus, restartElementId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("linkJobExecutions", jobExecutionId, restartJobExecutionId), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("startStepExecution", stepExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("finishStepExecution", stepExecutionId, metrics, batchStatus, exitStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("startPartitionExecution", partitionExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("updatePartitionExecution", partitionExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, this.<Void>_cmd("finishPartitionExecution", partitionExecutionId, metrics, persistentUserData, batchStatus, exitStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        try {
            final DeferredImpl<Set<String>,Throwable,Void> promise = new DeferredImpl<Set<String>,Throwable,Void>();
            registry.invoke(address, this.<Set<String>>_cmd("getJobNames"), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        try {
            final DeferredImpl<Integer,Throwable,Void> promise = new DeferredImpl<Integer,Throwable,Void>();
            registry.invoke(address, this.<Integer>_cmd("getJobInstanceCount", jobName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        try {
            final DeferredImpl<List<JobInstance>,Throwable,Void> promise = new DeferredImpl<List<JobInstance>,Throwable,Void>();
            registry.invoke(address, this.<List<JobInstance>>_cmd("getJobInstances", jobName, start, count), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        try {
            final DeferredImpl<List<Long>,Throwable,Void> promise = new DeferredImpl<List<Long>,Throwable,Void>();
            registry.invoke(address, this.<List<Long>>_cmd("getRunningExecutions", jobName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<Properties,Throwable,Void> promise = new DeferredImpl<Properties,Throwable,Void>();
            registry.invoke(address, this.<Properties>_cmd("getParameters", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        try {
            final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<ExtendedJobInstance,Throwable,Void>();
            registry.invoke(address, this.<ExtendedJobInstance>_cmd("getJobInstance", jobInstanceId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<ExtendedJobInstance,Throwable,Void>();
            registry.invoke(address, this.<ExtendedJobInstance>_cmd("getJobInstanceForExecution", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<? extends JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        try {
            final DeferredImpl<List<? extends JobExecution>,Throwable,Void> promise = new DeferredImpl<List<? extends JobExecution>,Throwable,Void>();
            registry.invoke(address, this.<List<? extends JobExecution>>_cmd("getJobExecutions", jobInstanceId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<ExtendedJobExecution,Throwable,Void>();
            registry.invoke(address, this.<ExtendedJobExecution>_cmd("getJobExecution", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        try {
            final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<ExtendedJobExecution,Throwable,Void>();
            registry.invoke(address, this.<ExtendedJobExecution>_cmd("restartJobExecution", jobExecutionId, parameters), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<List<? extends StepExecution>,Throwable,Void> promise = new DeferredImpl<List<? extends StepExecution>,Throwable,Void>();
            registry.invoke(address, this.<List<? extends StepExecution>>_cmd("getStepExecutionsForJobExecution", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, this.<ExtendedStepExecution>_cmd("getStepExecution", stepExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, this.<ExtendedStepExecution>_cmd("getPreviousStepExecution", jobExecutionId, stepExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, this.<ExtendedStepExecution>_cmd("getLatestStepExecution", jobExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        try {
            final DeferredImpl<Integer,Throwable,Void> promise = new DeferredImpl<Integer,Throwable,Void>();
            registry.invoke(address, this.<Integer>_cmd("getStepExecutionCount", jobExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        try {
            final DeferredImpl<StepExecution[],Throwable,Void> promise = new DeferredImpl<StepExecution[],Throwable,Void>();
            registry.invoke(address, this.<StepExecution[]>_cmd("getStepExecutions", new Serializable[]{ stepExecutionIds }), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        try {
            final DeferredImpl<PartitionExecution[],Throwable,Void> promise = new DeferredImpl<PartitionExecution[],Throwable,Void>();
            registry.invoke(address, this.<PartitionExecution[]>_cmd("getUnfinishedPartitionExecutions", stepExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        try {
            final DeferredImpl<PartitionExecution,Throwable,Void> promise = new DeferredImpl<PartitionExecution,Throwable,Void>();
            registry.invoke(address, this.<PartitionExecution>_cmd("getPartitionExecution", partitionExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
