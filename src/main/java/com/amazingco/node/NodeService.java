package com.amazingco.node;

import com.amazingco.node.NodeController.NodePayload;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    private List<Node> children;

    List<Node> getChildren(Node node) {
        children = new ArrayList<>();

        if (node.getRoot() == null) {
            return nodeRepository.findByRootId(node.getId());
        } else {
            findChildren(node.getId());
            return children;
        }
    }

    Node updateParent(Node currentNode, UUID parentNodeId) {
        Optional<Node> parentNode = nodeRepository.findById(parentNodeId);

        return parentNode.map(parent -> {
            int newNodeHeight = parent.getHeight() + 1;
            int diffHeight = currentNode.getHeight() - newNodeHeight;
            currentNode.setHeight(newNodeHeight);
            currentNode.setParent(parent);

            updateChildrenNodesHeight(currentNode, diffHeight);

            return nodeRepository.saveAndFlush(currentNode);
        }).orElseThrow(() -> new NodeException(String.format("Node with the id = %s does not exist", parentNodeId)));
    }

    Node createNode(NodePayload nodePayload) {

        validatePayload(nodePayload);

        if (nodePayload.getRootId() == null ) {
            return nodeRepository.saveAndFlush(Node.builder().build());
        }

        Optional<Node> parentNode = nodeRepository.findById(nodePayload.getParentId());
        Optional<Node> rootNode = nodeRepository.findById(nodePayload.getRootId());

        validateParentAndRootNodes(nodePayload, parentNode, rootNode);

        Node node = Node.builder()
                .height(parentNode.get().getHeight() + 1)
                .parent(parentNode.get())
                .root(rootNode.get())
                .build();

        return nodeRepository.saveAndFlush(node);
    }

    private void findChildren(UUID id) {
        List<Node> currentChildren = nodeRepository.findByParentId(id);

        children.addAll(currentChildren);

        currentChildren.forEach(child -> findChildren(child.getId()));
    }

    private void validatePayload(NodePayload nodePayload) {
        if (hasParentAndNoRoot(nodePayload) || hasRootAndNoParent(nodePayload)) {
            throw new NodeException("Child node must have root and parent node");
        }

        if(nodePayload.getRootId() == null && !nodeRepository.findByParentId(null).isEmpty()){
            throw new NodeException("Root Node already exists in the tree.");
        }
    }

    private boolean hasRootAndNoParent(NodePayload nodePayload) {
        return nodePayload.getParentId() == null && nodePayload.getRootId() != null;
    }

    private boolean hasParentAndNoRoot(NodePayload nodePayload) {
        return nodePayload.getParentId() != null && nodePayload.getRootId() == null;
    }

    private void validateParentAndRootNodes(NodePayload node, Optional<Node> parentNode, Optional<Node> rootNode) {
        if (!parentNode.isPresent()) {
            throw new NodeException(String.format("Node with the id = %s does not exist", node.getParentId()));
        }

        if (!rootNode.isPresent()) {
            throw new NodeException(String.format("Node with the id = %s does not exist", node.getRootId()));
        }

        if (rootNode.get().getParent() != null) {
            throw new NodeException(String.format("Node with the id = %s can not be set as a root node", rootNode.get().getId()));
        }
    }

    private void updateChildrenNodesHeight(Node currentNode, int diffHeight) {
        List<Node> children = getChildren(currentNode);
        children.forEach(child -> {
            int oldHeight = child.getHeight();
            child.setHeight(oldHeight - diffHeight);
            nodeRepository.saveAndFlush(child);
        });
    }

    class NodeException extends RuntimeException {

        NodeException(String message) {
            super(message);
        }

    }
}
