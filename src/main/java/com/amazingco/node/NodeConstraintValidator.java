package com.amazingco.node;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NodeConstraintValidator implements ConstraintValidator<NodeConstraint, Node> {

    @Override
    public boolean isValid(Node node, ConstraintValidatorContext context) {
        return isParentNode(node) || hasParentNodeAndHeightIsHigher(node);
    }

    private boolean hasParentNodeAndHeightIsHigher(Node node) {
        return node.getParent() != null && node.getRoot() != null && node.getHeight() > node.getParent().getHeight();
    }

    private boolean isParentNode(Node node) {
        return node.getParent() == null && node.getRoot() == null;
    }
}
