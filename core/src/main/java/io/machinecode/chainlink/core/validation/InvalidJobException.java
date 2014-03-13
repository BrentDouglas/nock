package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.visitor.VisitorNode;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvalidJobException extends RuntimeException {

    private final VisitorNode node;

    public InvalidJobException(final VisitorNode node) {
        this.node = node;
    }

    @Override
    public String getMessage() {
        return node.toTree(new StringBuilder(System.lineSeparator())).toString();
    }
}