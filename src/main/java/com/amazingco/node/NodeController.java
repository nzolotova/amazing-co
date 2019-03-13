package com.amazingco.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(path = "/nodes")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @PostMapping
    public ResponseEntity<Node> createNode(@RequestBody NodePayload nodePayload) {
        Node node = nodeService.createNode(nodePayload);
        return ResponseEntity.ok().body(node);
    }

    @GetMapping("/{nodeId}/children")
    public ResponseEntity<GetChildrenResponse> getChildren(@PathVariable("nodeId") Optional<Node> node) {

        return node.map(node1 -> nodeService.getChildren(node1))
                .map(children -> ResponseEntity.ok(new GetChildrenResponse(children)))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @PutMapping("/{nodeId}/parent")
    public ResponseEntity<Node> updateParent(@PathVariable("nodeId") Optional<Node> node,
                                             @RequestBody UpdateParentRequest updateParentRequest) {

        return node.map(currentNode -> nodeService.updateParent(currentNode, updateParentRequest.getParentId()))
                .map(updatedNode -> ResponseEntity.ok().body(updatedNode))
                .orElse(new ResponseEntity<>(NOT_FOUND));
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

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    static class UpdateParentRequest {

        @NotNull
        private UUID parentId;

    }
}
