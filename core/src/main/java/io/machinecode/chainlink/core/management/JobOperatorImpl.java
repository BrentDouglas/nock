package io.machinecode.chainlink.core.management;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.DelegateJobExecutionImpl;
import io.machinecode.chainlink.core.DelegateStepExecutionImpl;
import io.machinecode.chainlink.core.configuration.ConfigurationFactoryImpl;
import io.machinecode.chainlink.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.core.util.PropertiesConverter;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.core.work.JobExecutable;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.ExecutorFactory;
import io.machinecode.chainlink.spi.security.SecurityCheck;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobOperatorImpl implements ExtendedJobOperator {

    private static final Logger log = Logger.getLogger(JobOperatorImpl.class);

    private final RuntimeConfigurationImpl configuration;
    private final Executor executor;
    private final SecurityCheck securityCheck;

    //TODO This whole business needs sorting out
    public JobOperatorImpl() {
        this.configuration = new RuntimeConfigurationImpl(ConfigurationFactoryImpl.INSTANCE.produce());

        final List<ExecutorFactory> transportFactories;
        try {
            transportFactories = new ResolvableService<ExecutorFactory>(ExecutorFactory.class).resolve(configuration.getClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        final ExecutorFactory executorFactory;
        if (transportFactories.isEmpty()) {
            executorFactory = new EventedExecutorFactory();
        } else {
            executorFactory = transportFactories.get(0);
        }
        this.executor = executorFactory.produce(configuration, 8); //TODO
        this.securityCheck = this.configuration.getSecurityCheck();
    }

    public JobOperatorImpl(final RuntimeConfigurationImpl configuration, final Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
        this.securityCheck = this.configuration.getSecurityCheck();
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        try {
            final Set<String> jobNames = executor.getRepository().getJobNames();
            final Set<String> copy = new THashSet<String>(jobNames.size());
            for (final String jobName : jobNames) {
                if (!this.securityCheck.filterJobName(jobName)) {
                    copy.add(jobName);
                }
            }
            return copy;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            this.securityCheck.canAccessJob(jobName);
            return executor.getRepository().getJobInstanceCount(jobName); //TODO This needs to fetch a list of id's that we can then filter on
        } catch (final NoSuchJobException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        try {
            securityCheck.canAccessJob(jobName);
            final List<JobInstance> jobInstances =  executor.getRepository().getJobInstances(jobName, start, count);
            final ArrayList<JobInstance> copy = new ArrayList<JobInstance>(jobInstances.size());
            for (final JobInstance jobInstance : jobInstances) {
                if (!securityCheck.filterJobInstance(jobInstance.getInstanceId())) {
                    copy.add(jobInstance);
                }
            }
            return copy;
        } catch (final NoSuchJobException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        try {
            final List<Long> jobExecutionIds = executor.getRepository().getRunningExecutions(jobName); //TODO This should probably go through Transport
            final ArrayList<Long> copy = new ArrayList<Long>(jobExecutionIds.size());
            for (final Long jobExecutionId : jobExecutionIds) {
                if (!securityCheck.filterJobExecution(jobExecutionId)) {
                    copy.add(jobExecutionId);
                }
            }
            return copy;
        } catch (final NoSuchJobException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        try {
            this.securityCheck.canAccessJobExecution(jobExecutionId);
            return executor.getRepository().getParameters(jobExecutionId);
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long start(final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        this.securityCheck.canStartJob(jslName);
        try {
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(jslName);
            final JobImpl job = JobFactory.produce(theirs, parameters);

            return _startJob(job, jslName, parameters).getJobExecutionId();
        } catch (final JobStartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public JobOperationImpl startJob(final JobWork job, final String jslName, final Properties parameters) throws JobStartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001200.operator.start"), jslName);
        this.securityCheck.canStartJob(jslName);
        try {
            return _startJob(job, jslName, parameters);
        } catch (final JobStartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    private JobOperationImpl _startJob(final JobWork job, final String jslName, final Properties parameters) throws Exception {
        JobFactory.validate(job);

        final ExecutionRepository repository = executor.getRepository();
        final ExtendedJobInstance instance = repository.createJobInstance(job, jslName, new Date());
        final ExtendedJobExecution execution = repository.createJobExecution(instance, parameters, new Date());
        final long jobExecutionId = execution.getExecutionId();
        final ExecutionContext context = new ExecutionContextImpl(
                job,
                new JobContextImpl(instance, execution, PropertiesConverter.convert(job.getProperties())),
                null,
                jobExecutionId,
                null,
                null,
                null
        );
        final Deferred<?> deferred = executor.execute(jobExecutionId, new JobExecutable(null, job, context));
        return new JobOperationImpl(
                jobExecutionId,
                deferred,
                repository
        );
    }

    @Override
    public JobOperationImpl getJobOperation(final long jobExecutionId) throws JobExecutionNotRunningException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
            final Deferred<?> deferred = executor.getJob(jobExecutionId);
            return new JobOperationImpl(
                    jobExecutionId,
                    deferred,
                    executor.getRepository()
            );
        } catch (final JobExecutionNotRunningException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    @Override
    public long restart(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        this.securityCheck.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters);
            return _restart(job, jobExecutionId, instance, parameters).getJobExecutionId();
        } catch (final JobExecutionAlreadyCompleteException e) {
            throw e;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionNotMostRecentException e) {
            throw e;
        } catch (final JobRestartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    @Override
    public JobOperationImpl restartJob(final long jobExecutionId, final Properties parameters) throws JobExecutionAlreadyCompleteException, NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobRestartException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001201.operator.restart"), jobExecutionId);
        this.securityCheck.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            final ExtendedJobInstance instance = repository.getJobInstanceForExecution(jobExecutionId);
            final io.machinecode.chainlink.spi.element.Job theirs = configuration.getJobLoader().load(instance.getJslName());
            final JobImpl job = JobFactory.produce(theirs, parameters);
            return _restart(job, jobExecutionId, instance, parameters);
        } catch (final JobExecutionAlreadyCompleteException e) {
            throw e;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionNotMostRecentException e) {
            throw e;
        } catch (final JobRestartException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobRestartException(e);
        }
    }

    private JobOperationImpl _restart(final JobWork job, final long jobExecutionId, final JobInstance instance, final Properties parameters) throws Exception {
        JobFactory.validate(job);
        final ExecutionRepository repository = executor.getRepository();
        final ExtendedJobExecution lastExecution = repository.getJobExecution(jobExecutionId);
        final ExtendedJobExecution execution = repository.restartJobExecution(jobExecutionId, parameters);
        final long restartExecutionId = execution.getExecutionId();
        if (!job.isRestartable()) {
            throw new JobRestartException(Messages.format("CHAINLINK-001100.operator.cant.restart.job", jobExecutionId, job.getId(), restartExecutionId));
        }
        final ExecutionContext context = new ExecutionContextImpl(
                job,
                new JobContextImpl(instance, execution, PropertiesConverter.convert(job.getProperties())),
                null,
                execution.getExecutionId(),
                lastExecution.getExecutionId(),
                lastExecution.getRestartElementId(),
                null
        );
        final Deferred<?> deferred = executor.execute(restartExecutionId, new JobExecutable(null, job, context));
        return new JobOperationImpl(
                restartExecutionId,
                deferred,
                repository
        );
    }

    @Override
    public void stop(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        this.securityCheck.canStopJob(jobExecutionId);
        stopJob(jobExecutionId);
    }

    @Override
    public Deferred<?> stopJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001202.operator.stop"), jobExecutionId);
        this.securityCheck.canRestartJob(jobExecutionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            repository.getJobExecution(jobExecutionId); //This will throw a NoSuchJobExecutionException if required
            final Deferred<?> deferred = executor.getJob(jobExecutionId);
            if (deferred == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-001002.operator.not.running", jobExecutionId));
            }
            executor.cancel(deferred);
            return deferred;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionNotRunningException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public void abandon(final long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionIsRunningException, JobSecurityException {
        log.tracef(Messages.get("CHAINLINK-001203.operator.abandon"), jobExecutionId);
        this.securityCheck.canAbandonJob(jobExecutionId);
        try {
            try {
                executor.getJob(jobExecutionId);
                throw new JobExecutionIsRunningException(Messages.format("CHAINLINK-001001.operator.running", jobExecutionId));
            } catch (final JobExecutionNotRunningException e) {
                Repository.abandonedJob(executor.getRepository(), jobExecutionId);
            }
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobExecutionIsRunningException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public JobInstance getJobInstance(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
        final JobInstance jobInstance = executor.getRepository().getJobInstanceForExecution(jobExecutionId);
            this.securityCheck.canAccessJobInstance(jobInstance.getInstanceId());
            return jobInstance;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        this.securityCheck.canAccessJobInstance(instance.getInstanceId());
        try {
            final ExecutionRepository repository = executor.getRepository();
            final List<? extends JobExecution> jobExecutions =  repository.getJobExecutions(instance);
            final List<JobExecution> delegates = new ArrayList<JobExecution>(jobExecutions.size());
            for (final JobExecution jobExecution : jobExecutions) {
                if (!this.securityCheck.filterJobExecution(jobExecution.getExecutionId())) {
                    delegates.add(new DelegateJobExecutionImpl(jobExecution, repository));
                }
            }
            return delegates;
        } catch (final NoSuchJobInstanceException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public JobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            return new DelegateJobExecutionImpl(repository.getJobExecution(jobExecutionId), repository);
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        this.securityCheck.canAccessJobExecution(jobExecutionId);
        try {
            final ExecutionRepository repository = executor.getRepository();
            final List<? extends StepExecution> stepExecutions =  repository.getStepExecutionsForJobExecution(jobExecutionId);
            final List<StepExecution> delegates = new ArrayList<StepExecution>(stepExecutions.size());
            for (final StepExecution stepExecution : stepExecutions) {
                if (!this.securityCheck.filterStepExecution(stepExecution.getStepExecutionId())) {
                    delegates.add(new DelegateStepExecutionImpl(stepExecution, repository));
                }
            }
            return delegates;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobStartException(e);
        }
    }
}