package io.machinecode.chainlink.core.configuration.def;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface DeploymentDef<Dec extends DeclarationDef, Prop extends PropertyDef, Job extends JobOperatorDef<Dec, Prop>>
        extends ScopeDef<Dec, Prop, Job> {

    String getName();

    void setName(final String name);
}