package com.amazingco.node;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NodeRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NodeRepository nodeRepository;

    private ResultActions resultActions;

    private Node root;

    private Node nodeToCheck;

    @Test
    public void should_get_root_node_children() throws Exception {

        //given
        givenTreeWithNodes();

        //when
        resultActions = mockMvc.perform(get("/nodes/{nodeId}/children", root.getId()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("children", hasSize(4)))
                .andDo(print());

    }

    @Test
    public void should_get_any_node_children() throws Exception {

        //given
        givenTreeWithNodes();

        //when
        resultActions = mockMvc.perform(get("/nodes/{nodeId}/children", nodeToCheck.getId()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("children", hasSize(4)))
                .andDo(print());

    }

    @Test
    public void should_update_parent_node() throws Exception{

        //given
        givenTreeWithNodes();

        //when
        resultActions = mockMvc.perform(patch("/nodes/{nodeId}/parent/{parentId}", nodeToCheck.getId(), root.getId()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("children", hasSize(4)))
                .andDo(print());

    }

    private void givenTreeWithNodes() {
        root = Node.builder().build();
        nodeRepository.saveAndFlush(root);

        Node node1 = Node.builder().parent(root).root(root).height(1).build();
        nodeRepository.saveAndFlush(node1);

        Node node2 = Node.builder().parent(root).root(root).height(1).build();
        nodeRepository.saveAndFlush(node2);

        nodeToCheck = Node.builder().parent(node1).root(root).height(2).build();
        nodeRepository.saveAndFlush(nodeToCheck);

        Node node4 = Node.builder().parent(nodeToCheck).root(root).height(3).build();
        nodeRepository.saveAndFlush(node4);

        Node node5 = Node.builder().parent(nodeToCheck).root(root).height(3).build();
        nodeRepository.saveAndFlush(node5);

        Node node6 = Node.builder().parent(node2).root(root).height(2).build();
        nodeRepository.saveAndFlush(node6);

        Node node7 = Node.builder().parent(nodeToCheck).root(root).height(3).build();
        nodeRepository.saveAndFlush(node7);

        Node node8 = Node.builder().parent(node7).root(root).height(4).build();
        nodeRepository.saveAndFlush(node8);

    }

}
