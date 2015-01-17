package io.machinecode.chainlink.ee.glassfish.command;

import io.machinecode.chainlink.core.configuration.def.DeploymentDef;
import io.machinecode.chainlink.core.configuration.def.JobOperatorDef;
import io.machinecode.chainlink.core.configuration.def.SubSystemDef;
import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.core.configuration.xml.XmlJobOperator;
import io.machinecode.chainlink.core.configuration.xml.subsystem.XmlChainlinkSubSystem;
import org.glassfish.api.Param;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class SetCommand extends BaseCommand {

    @Param(name = "file", shortName = "f", optional = true)
    protected String file;

    @Param(name = "base64", shortName = "b", optional = true)
    protected String base64;

    protected InputStream stream() throws Exception {
        if (file != null) {
            if (base64 != null) {
                throw new Exception("Only one of the arguments file or base64 may be provided.");
            }
            return new FileInputStream(file);
        }
        if (base64 != null) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(base64));
        }
        throw new Exception("One of the arguments file or base64 must be provided.");
    }

    public SubSystemDef<?,?,?,?> readSubsystem() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlChainlinkSubSystem.read(stream);
        }
    }

    public DeploymentDef<?,?,?> readDeployment() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlDeployment.read(stream);
        }
    }

    public JobOperatorDef<?,?> readJobOperator() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlJobOperator.read(stream);
        }
    }
}