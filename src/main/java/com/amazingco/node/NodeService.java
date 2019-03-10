package com.amazingco.node;

import com.amazingco.node.NodeController.NodePayload;
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

    private List<Node> children = new ArrayList<>();

    List<Node> getChildren(Node node) {

        if (node.getRoot() == null) {
            return nodeRepository.findByRootId(node.getId());
        } else {
            findChildren(node.getId());
            return children;
        }
    }

    private void findChildren(UUID id) {
        List<Node> currentChildren = nodeRepository.findByParentId(id);

        children.addAll(currentChildren);

        currentChildren.forEach(child -> findChildren(child.getId()));
    }

    Node createNode(NodePayload node) {
        if(node.getParentId() == null){

        }

        return nodeRepository.saveAndFlush(
                Node.builder().height(0).build());
    }

    Node updateParent(Node currentNode, UUID parentNodeId) {
        Optional<Node> parentNode = nodeRepository.findById(parentNodeId);

        parentNode.map(parent -> {
            int newNodeHeight = parent.getHeight() + 1;
            int diffHeight = currentNode.getHeight() - newNodeHeight;
            currentNode.setHeight(newNodeHeight);
            currentNode.setParent(parent);

            updateChildrenNodesHeight(currentNode, diffHeight);

            return nodeRepository.saveAndFlush(currentNode);
        }).orElseThrow(() -> new NodeException(String.format("Node with the id = %s does not exist", parentNodeId)));

        return nodeRepository.saveAndFlush(currentNode);
    }

    private void updateChildrenNodesHeight(Node currentNode, int diffHeight) {
        List<Node> children = getChildren(currentNode);
        children.forEach(child -> {
            int oldHeight = child.getHeight();
            child.setHeight(oldHeight - diffHeight);
            nodeRepository.saveAndFlush(child);
        });
    }

    private class NodeException extends RuntimeException {

        NodeException(String message) {
            super(message);
        }

    }
}
