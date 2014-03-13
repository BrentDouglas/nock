package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import javax.batch.runtime.BatchStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobExecutionImpl implements ExtendedJobExecution {
    private final long executionId;
    private final String jobName;
    private final BatchStatus batchStatus;
    private final Date start;
    private final Date end;
    private final String exitStatus;
    private final Date created;
    private final Date updated;
    private final Properties parameters;
    private final String restartElementId;

    public JobExecutionImpl(final Builder builder) {
        this.executionId = builder.executionId;
        this.jobName = builder.jobName;
        this.batchStatus = builder.batchStatus;
        this.start = builder.start;
        this.end = builder.end;
        this.exitStatus = builder.exitStatus;
        this.created = builder.created;
        this.updated = builder.updated;
        this.parameters = builder.parameters;
        this.restartElementId = builder.restartElementId;
    }

    public JobExecutionImpl(final ExtendedJobExecution builder) {
        this.executionId = builder.getExecutionId();
        this.jobName = builder.getJobName();
        this.batchStatus = builder.getBatchStatus();
        this.start = builder.getStartTime();
        this.end = builder.getEndTime();
        this.exitStatus = builder.getExitStatus();
        this.created = builder.getCreateTime();
        this.updated = builder.getLastUpdatedTime();
        this.parameters = builder.getJobParameters();
        this.restartElementId = builder.getRestartElementId();
    }

    public static List<JobExecutionImpl> copy(final List<? extends ExtendedJobExecution> list) {
        final ArrayList<JobExecutionImpl> copy = new ArrayList<JobExecutionImpl>();
        for (final ExtendedJobExecution item : list) {
            copy.add(new JobExecutionImpl(item));
        }
        return copy;
    }

    @Override
    public long getExecutionId() {
        return executionId;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public Date getStartTime() {
        return start;
    }

    @Override
    public Date getEndTime() {
        return end;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public Date getCreateTime() {
        return created;
    }

    @Override
    public Date getLastUpdatedTime() {
        return updated;
    }

    @Override
    public Properties getJobParameters() {
        return parameters;
    }

    @Override
    public String getRestartElementId() {
        return restartElementId;
    }

    public static Builder from(final ExtendedJobExecution builder) {
        final Builder that = new Builder();
        that.executionId = builder.getExecutionId();
        that.jobName = builder.getJobName();
        that.batchStatus = builder.getBatchStatus();
        that.start = builder.getStartTime();
        that.end = builder.getEndTime();
        that.exitStatus = builder.getExitStatus();
        that.created = builder.getCreateTime();
        that.updated = builder.getLastUpdatedTime();
        that.parameters = builder.getJobParameters();
        that.restartElementId = builder.getRestartElementId();
        return that;
    }

    public static class Builder {
        private long executionId;
        private String jobName;
        private BatchStatus batchStatus;
        private Date start;
        private Date end;
        private String exitStatus;
        private Date created;
        private Date updated;
        private Properties parameters;
        private String restartElementId;

        public Builder setExecutionId(final long executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder setJobName(final String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder setBatchStatus(final BatchStatus batchStatus) {
            this.batchStatus = batchStatus;
            return this;
        }

        public Builder setStart(final Date start) {
            this.start = start;
            return this;
        }

        public Builder setEnd(final Date end) {
            this.end = end;
            return this;
        }

        public Builder setExitStatus(final String exitStatus) {
            this.exitStatus = exitStatus;
            return this;
        }

        public Builder setCreated(final Date created) {
            this.created = created;
            return this;
        }

        public Builder setUpdated(final Date updated) {
            this.updated = updated;
            return this;
        }

        public Builder setParameters(final Properties parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder setRestartElementId(final String restartElementId) {
            this.restartElementId = restartElementId;
            return this;
        }

        public JobExecutionImpl build() {
            return new JobExecutionImpl(this);
        }
    }
}