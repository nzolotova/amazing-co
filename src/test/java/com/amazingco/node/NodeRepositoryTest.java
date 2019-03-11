package com.amazingco.node;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NodeRepositoryTest {

    @Autowired
    private NodeRepository nodeRepository;

    private Node parentNode;

    private Node node;

    @Before
    public void setUp(){
        nodeRepository.deleteAll();
    }

    @Test
    public void should_persist_parent_node() {

        //given
        givenParentNode();

        //when
        List<Node> allNodes = nodeRepository.findAll();

        //then
        then(allNodes.size()).isEqualTo(1);
        then(allNodes.get(0).getHeight()).isEqualTo(0);
        then(allNodes.get(0).getId()).isNotNull();
    }

    @Test
    public void should_persist_node_with_parent() {

        //given
        givenSavedNodeWithParent();

        //when
        List<Node> allNodes = nodeRepository.findAll();

        //then
        then(allNodes.size()).isEqualTo(2);
        then(allNodes.get(1).getParent()).isEqualTo(parentNode);
        then(allNodes.get(1).getRoot()).isEqualTo(parentNode);
        then(allNodes.get(1).getId()).isNotNull();
    }

    @Test
    public void should_fail_to_persist_node_with_height_equal_to_parent_height() {

        //given
        givenNodeWithParent(0);

        //when/then
        thenThrownBy(() -> nodeRepository.saveAndFlush(node)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void should_fail_to_persist_node_with_parent_without_root() {

        //given
        givenParentNode();
        node = Node.builder().height(1).parent(parentNode).build();

        //when/then
        thenThrownBy(() -> nodeRepository.saveAndFlush(node)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void should_fail_to_persist_node_with_root_without_parent() {

        //given
        givenParentNode();
        node = Node.builder().height(1).root(parentNode).build();

        //when/then
        thenThrownBy(() -> nodeRepository.saveAndFlush(node)).isInstanceOf(ConstraintViolationException.class);
    }

    private void givenSavedNodeWithParent() {
        givenNodeWithParent(1);
        nodeRepository.saveAndFlush(node);
    }

    private void givenNodeWithParent(int height) {
        givenParentNode();
        node = Node.builder().height(height).parent(parentNode).root(parentNode).build();
    }

    private void givenParentNode() {
        parentNode  = nodeRepository.saveAndFlush(Node.builder().height(0).build());
    }

}
