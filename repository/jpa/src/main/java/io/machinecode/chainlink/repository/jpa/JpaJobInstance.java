package io.machinecode.chainlink.repository.jpa;

import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@Entity
@Table(name = "job_instance", schema = "public")
@NamedQueries({
        @NamedQuery(name = "JpaJobInstance.countWithJobName", query = "select count(i) from JpaJobInstance i where i in (select distinct j from JpaJobInstance j where j.jobName=:jobName)"),
        @NamedQuery(name = "JpaJobInstance.jobNames", query = "select distinct j.jobName from JpaJobInstance j")
})
public class JpaJobInstance implements ExtendedJobInstance {
    private long id;
    private String jobName;
    private String jslName;
    private Date created;
    private List<JpaJobExecution> jobExecutions;

    public JpaJobInstance() {
    }

    public JpaJobInstance(final JpaJobInstance builder) {
        this.jobName = builder.jobName;
        this.jslName = builder.jslName;
        this.created = builder.created;
        this.jobExecutions = builder.jobExecutions;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getInstanceId() {
        return id;
    }

    public JpaJobInstance setInstanceId(final long instanceId) {
        this.id = instanceId;
        return this;
    }

    @Override
    @Column(name = "job_name", nullable = false)
    public String getJobName() {
        return jobName;
    }

    public JpaJobInstance setJobName(final String jobName) {
        this.jobName = jobName;
        return this;
    }

    @Override
    @Column(name = "jsl_name", nullable = false)
    public String getJslName() {
        return jslName;
    }

    public JpaJobInstance setJslName(final String jslName) {
        this.jslName = jslName;
        return this;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", length = 29)
    public Date getCreateTime() {
        return created;
    }

    public JpaJobInstance setCreateTime(final Date created) {
        this.created = created;
        return this;
    }

    @OneToMany(mappedBy = "jobInstance", cascade = {CascadeType.ALL})
    @OrderBy("startDate desc")
    public List<JpaJobExecution> getJobExecutions() {
        return jobExecutions;
    }

    public JpaJobInstance setJobExecutions(final List<JpaJobExecution> jobExecutions) {
        this.jobExecutions = jobExecutions;
        return this;
    }
}