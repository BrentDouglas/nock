package io.machinecode.chainlink.jsl.groovy.partition

import io.machinecode.chainlink.jsl.fluent.partition.FluentPlan
import io.machinecode.chainlink.jsl.groovy.GroovyProperties;

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyPlan {

    final FluentPlan _value = new FluentPlan();

    def partitions(final String partitions) {
        _value.partitions = partitions
    }

    def threads(final String threads) {
        _value.threads = threads
    }

    def props(final Closure cl) {
        def that = new GroovyProperties()
        _value.properties.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
