package com.amazingco.node;

import com.amazingco.node.NodeController.NodePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        currentChildren.forEach(child -> {
            findChildren(child.getId());
        });
    }

    Node createNode(NodePayload node) {
        return nodeRepository.saveAndFlush(
                Node.builder().height(0).build());
    }

    Node updateParentNode(Node currentNode, UUID parentNodeId) {
        return null;
    }

}
