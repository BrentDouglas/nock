package io.machinecode.nock.core.model;

import io.machinecode.nock.core.impl.JobContextImpl;
import io.machinecode.nock.core.model.execution.ExecutionImpl;
import io.machinecode.nock.core.util.PropertiesConverter;
import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.core.work.job.AfterJob;
import io.machinecode.nock.core.work.job.FailJob;
import io.machinecode.nock.core.work.job.RunJob;
import io.machinecode.nock.jsl.validation.InvalidJobException;
import io.machinecode.nock.jsl.validation.JobValidator;
import io.machinecode.nock.jsl.visitor.JobTraversal;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Pair;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.JobWork;
import org.jboss.logging.Logger;

import javax.batch.api.listener.JobListener;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobImpl implements Job, JobWork {

    private static final Logger log = Logger.getLogger(JobImpl.class);

    private final String id;
    private final String version;
    private final String restartable;
    private final PropertiesImpl properties;
    private final ListenersImpl listeners;
    private final List<ExecutionImpl> executions;
    private final JobTraversal traversal;

    private transient List<JobListener> _listeners;

    public JobImpl(final String id, final String version, final String restartable, final PropertiesImpl properties,
                   final ListenersImpl listeners, final List<ExecutionImpl> executions) throws InvalidJobException {
        this.id = id;
        this.version = version;
        this.restartable = restartable;
        this.properties = properties;
        this.listeners = listeners;
        this.executions = executions;
        this.traversal = new JobTraversal(JobValidator.INSTANCE.visit(this));
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getRestartable() {
        return this.restartable;
    }

    public boolean isRestartable() {
        return Boolean.parseBoolean(this.restartable);
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public ListenersImpl getListeners() {
        return this.listeners;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }

    // Lifecycle

    @Override
    public void before(final Transport transport, final Context context) throws Exception {
        final ExecutionRepository repository = transport.getRepository();
        final JobContextImpl jobContext = new JobContextImpl(
                repository.getJobInstance(context.getJobInstanceId()),
                repository.getJobExecution(context.getJobExecutionId()),
                PropertiesConverter.convert(this.properties)
        );
        context.setJobContext(jobContext);
        this._listeners = this.listeners.getListenersImplementing(transport, context, JobListener.class);
        Exception exception = null;
        for (final JobListener listener : this._listeners) {
            try {
                listener.beforeJob();
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public String element() {
        return Job.ELEMENT;
    }

    @Override
    public Plan run(final Transport transport, final Context context) throws Exception {
        if (Status.isStopping(context) || Status.isComplete(context)) {
            return null;
        }
        return this.executions.get(0).plan(transport, context);
    }

    @Override
    public void after(final Transport transport, final Context context) throws Exception {
        try {
            if (this._listeners == null) {
                throw new IllegalStateException();
            }
            Exception exception = null;
            for (final JobListener listener : this._listeners) {
                try {
                    listener.afterJob();
                } catch (final Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        } finally {
            context.setJobContext(null);
        }
    }

    @Override
    public ExecutionWork next(final String next) {
        return traversal.next(next);
    }

    @Override
    public List<? extends Pair<String, String>> properties(final Element element) {
        return traversal.properties(element);
    }

    @Override
    public Plan plan(final Transport transport, final Context context) {
        final RunJob run = new RunJob(this, context);
        final AfterJob after = new AfterJob(this, context);
        final FailJob fail = new FailJob(this, context);

        final PlanImpl runPlan = new PlanImpl(run, TargetThread.ANY, element());
        final PlanImpl afterPlan = new PlanImpl(after, TargetThread.that(run), element());
        final PlanImpl failPlan = new PlanImpl(fail, TargetThread.that(run), element());
        final PlanImpl afterFailPlan = new PlanImpl(fail, TargetThread.that(run), element());

        runPlan.fail(failPlan)
            .always(afterPlan.fail(afterFailPlan));

        return runPlan;
    }
}
