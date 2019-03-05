package com.amazingco.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    public List<Node> getChildren(Node node){
        return Collections.emptyList();
    }

}
