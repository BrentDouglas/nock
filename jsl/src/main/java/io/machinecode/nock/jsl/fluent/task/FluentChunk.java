package io.machinecode.nock.jsl.fluent.task;

import io.machinecode.nock.jsl.api.task.CheckpointAlgorithm;
import io.machinecode.nock.jsl.api.task.Chunk;
import io.machinecode.nock.jsl.api.task.ExceptionClassFilter;
import io.machinecode.nock.jsl.api.task.ItemProcessor;
import io.machinecode.nock.jsl.api.task.ItemReader;
import io.machinecode.nock.jsl.api.task.ItemWriter;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentChunk implements Chunk {

    private String checkpointPolicy = CheckpointPolicy.ITEM;
    private String itemCount = TEN;
    private String timeLimit = ZERO;
    private String skipLimit = ZERO;
    private String retryLimit = ZERO;
    private ItemReader reader;
    private ItemProcessor processor;
    private ItemWriter writer;
    private CheckpointAlgorithm checkpointAlgorithm;
    private ExceptionClassFilter skippableExceptionClasses;
    private ExceptionClassFilter retryableExceptionClasses;
    private ExceptionClassFilter noRollbackExceptionClasses;


    @Override
    public String getCheckpointPolicy() {
        return this.checkpointPolicy;
    }

    public FluentChunk setCheckpointPolicy(final String checkpointPolicy) {
        this.checkpointPolicy = checkpointPolicy;
        return this;
    }

    @Override
    public String getItemCount() {
        return this.itemCount;
    }

    public FluentChunk setItemCount(final String itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    @Override
    public String getTimeLimit() {
        return this.timeLimit;
    }

    public FluentChunk setTimeLimit(final String timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    @Override
    public String getSkipLimit() {
        return this.skipLimit;
    }

    public FluentChunk setSkipLimit(final String skipLimit) {
        this.skipLimit = skipLimit;
        return this;
    }

    @Override
    public String getRetryLimit() {
        return this.retryLimit;
    }

    public FluentChunk setRetryLimit(final String retryLimit) {
        this.retryLimit = retryLimit;
        return this;
    }

    @Override
    public ItemReader getReader() {
        return this.reader;
    }

    public FluentChunk setReader(final ItemReader reader) {
        this.reader = reader;
        return this;
    }

    @Override
    public ItemProcessor getProcessor() {
        return this.processor;
    }

    public FluentChunk setProcessor(final ItemProcessor processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public ItemWriter getWriter() {
        return this.writer;
    }

    public FluentChunk setWriter(final ItemWriter writer) {
        this.writer = writer;
        return this;
    }

    @Override
    public CheckpointAlgorithm getCheckpointAlgorithm() {
        return this.checkpointAlgorithm;
    }

    public FluentChunk setCheckpointAlgorithm(final CheckpointAlgorithm checkpointAlgorithm) {
        this.checkpointAlgorithm = checkpointAlgorithm;
        return this;
    }

    @Override
    public ExceptionClassFilter getSkippableExceptionClasses() {
        return this.skippableExceptionClasses;
    }

    public FluentChunk setSkippableExceptionClasses(final ExceptionClassFilter skippableExceptionClasses) {
        this.skippableExceptionClasses = skippableExceptionClasses;
        return this;
    }

    @Override
    public ExceptionClassFilter getRetryableExceptionClasses() {
        return this.retryableExceptionClasses;
    }

    public FluentChunk setRetryableExceptionClasses(final ExceptionClassFilter retryableExceptionClasses) {
        this.retryableExceptionClasses = retryableExceptionClasses;
        return this;
    }

    @Override
    public ExceptionClassFilter getNoRollbackExceptionClasses() {
        return this.noRollbackExceptionClasses;
    }

    public FluentChunk setNoRollbackExceptionClasses(final ExceptionClassFilter noRollbackExceptionClasses) {
        this.noRollbackExceptionClasses = noRollbackExceptionClasses;
        return this;
    }
}
