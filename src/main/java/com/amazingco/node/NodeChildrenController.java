package com.amazingco.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(path = "/nodes/{nodeId}/children")
public class NodeChildrenController {

    @Autowired
    private NodeService nodeService;

    @GetMapping
    public ResponseEntity<GetChildrenResponse> getChildren(@PathVariable("nodeId")Optional<Node> node) {

        return node.map(node1 -> nodeService.getChildren(node1))
                .map(children -> ResponseEntity.ok().body(new GetChildrenResponse(children)))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class GetChildrenResponse {

        @NonNull
        private List<Node> children;
    }
}
