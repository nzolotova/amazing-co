package com.amazingco.node;

import com.amazingco.node.NodeController.NodePayload;
import com.amazingco.node.NodeService.NodeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Java6BDDAssertions.then;
import static org.assertj.core.api.Java6BDDAssertions.thenThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NodeServiceIntegrationTest {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private NodeService nodeService;

    private NodePayload rootNodePayload;
    private NodePayload nodePayload;

    private Node root;
    private Node node;

    @Before
    public void setUp() {
        nodeRepository.deleteAll();
    }

    @Test
    public void should_create_root_node() {

        //given
        givenRootNodePayload();

        //when
        whenCreateRootNode();

        //then
        then(root).isNotNull();
        then(root.getHeight()).isEqualTo(0);
    }

    @Test
    public void should_create_node() {

        //given
        givenNodePayload();

        //when
        whenCreateNode();

        //then
        then(nodeRepository.findAll()).hasSize(2);
        then(nodeRepository.findAll().get(1).getHeight()).isEqualTo(1);
    }


    @Test
    public void should_fail_to_create_node_with_parent_without_root() {

        //given
        nodePayload = NodePayload.builder().parentId(UUID.randomUUID()).build();

        //when / then
        thenThrownBy(this::whenCreateNode).isInstanceOf(NodeException.class);
    }

    @Test
    public void should_fail_to_create_node_with_root_without_parent() {

        //given
        nodePayload = NodePayload.builder().rootId(UUID.randomUUID()).build();

        //when / then
        thenThrownBy(this::whenCreateNode).isInstanceOf(NodeException.class);
    }

    @Test
    public void should_fail_to_create_node_with_non_existing_parent() {

        //given
        givenRootNode();
        nodePayload = NodePayload.builder().parentId(UUID.randomUUID()).rootId(root.getId()).build();

        //when / then
        thenThrownBy(this::whenCreateNode).isInstanceOf(NodeException.class);
    }

    @Test
    public void should_fail_to_create_node_with_non_existing_root() {

        //given
        givenRootNode();
        nodePayload = NodePayload.builder().parentId(root.getId()).rootId(UUID.randomUUID()).build();

        //when / then
        thenThrownBy(this::whenCreateNode).isInstanceOf(NodeException.class);
    }

    @Test
    public void should_fail_to_create_node_with_root_which_has_parent() {

        //given
        givenNode();
        nodePayload = NodePayload.builder().parentId(root.getId()).rootId(node.getId()).build();

        //when / then
        thenThrownBy(this::whenCreateNode).isInstanceOf(NodeException.class);
    }

    private void whenCreateNode() {
        node = nodeService.createNode(nodePayload);
    }

    private void whenCreateRootNode() {
        root = nodeService.createNode(rootNodePayload);
    }

    private void givenNodePayload() {
        givenRootNode();
        nodePayload = NodePayload.builder().parentId(root.getId()).rootId(root.getId()).build();
    }

    private void givenRootNode() {
        givenRootNodePayload();
        whenCreateRootNode();
    }

    private void givenRootNodePayload() {
        rootNodePayload = NodePayload.builder().build();
    }

    private void givenNode() {
        givenNodePayload();
        whenCreateNode();
    }

}
