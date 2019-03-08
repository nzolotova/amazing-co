package com.amazingco.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/nodes")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @PostMapping()
    public ResponseEntity<Node> createNode(@RequestBody NodePayload nodePayload) {
        Node node = nodeService.createNode(nodePayload);

        return ResponseEntity.ok().body(node);
    }

    @GetMapping("/{nodeId}/children")
    public ResponseEntity<GetChildrenResponse> getChildren(@PathVariable("nodeId") Optional<Node> node) {

        return node.map(node1 -> nodeService.getChildren(node1))
                .map(children -> ResponseEntity.ok(new GetChildrenResponse(children)))
                .get();
    }

    @PatchMapping("/{nodeId}/parent")
    public ResponseEntity<Node> updateParent(@PathVariable("nodeId") Optional<Node> node,
                                             @RequestBody UUID parentId) {

        return node.map(currentNode -> nodeService.updateParentNode(currentNode, parentId))
                .map(updatedNode -> ResponseEntity.ok().body(updatedNode))
                .get();
    }

    @Getter
    static class GetChildrenResponse {

        private List<Node> children;

        GetChildrenResponse(List<Node> children) {
            this.children = children;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    static class NodePayload {

        private UUID parentId;

        private UUID rootId;

    }
}
