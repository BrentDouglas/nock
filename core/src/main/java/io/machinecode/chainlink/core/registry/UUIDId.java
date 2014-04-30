package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class UUIDId implements ChainId, ExecutableId, WorkerId, ExecutionRepositoryId, Serializable {

    final UUID uuid;

    protected UUIDId(final UUID uuid) {
        this.uuid = uuid;
    }

    public UUIDId() {
        this(UUID.randomUUID());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UUIDId that = (UUIDId) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[uuid=" + uuid + "]";
    }
}