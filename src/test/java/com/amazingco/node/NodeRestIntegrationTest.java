package com.amazingco.node;

import com.amazingco.node.NodeController.NodePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions resultActions;

    private Node root;

    private Node nodeToCheck;

    private NodePayload nodePayload;

    @Before
    public void setUp() {
        nodeRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    public void should_create_node() {

        //given
        givenNodePayload();

        //when
        whenCreateNode();

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("parent.id", is(root.getId().toString())))
                .andExpect(jsonPath("root.id", is(root.getId().toString())))
                .andExpect(jsonPath("height", is(1)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    public void should_fail_to_create_a_root_node_with_invalid_payload() {

        //given
        givenInvalidPayloadForRootNode();

        //when
        whenCreateNode();

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    public void should_get_children_of_root_node() {

        //given
        givenTreeWithNodes();

        //when
        whenGetChildren(root.getId());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("children", hasSize(8)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    public void should_get_any_node_children() {

        //given
        givenTreeWithNodes();

        //when
        whenGetChildren(nodeToCheck.getId());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("children", hasSize(4)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    public void should_fail_to_get_children_for_non_existing_node() {

        //given
        givenTreeWithNodes();

        //when
        whenGetChildren(UUID.randomUUID());

        //then
        resultActions.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    public void should_set_a_new_parent_node() {

        //given
        givenTreeWithNodes();

        //when
        whenSetParentAsRootNode(nodeToCheck.getId());

        //then
        thenTheHeightIsUpdatedAndParentNodeIsRoot();
        thenChildrenNodesHaveNewHeight();

    }

    @Test
    @SneakyThrows
    public void should_fail_to_update_non_existing_node() {

        //given
        givenTreeWithNodes();

        //when
        whenSetParentAsRootNode(UUID.randomUUID());

        //then
        resultActions.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    public void should_fail_to_set_parent_with_non_existing_node() {

        //given
        givenTreeWithNodes();

        //when
        resultActions = mockMvc.perform(
                put("/nodes/{nodeId}/parent", nodeToCheck.getId())
                        .contentType(APPLICATION_JSON)
                        .content("{\"parentId\" : \"" + UUID.randomUUID() + "\"}"))
        ;

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    private void givenTreeWithNodes() {
        givenRootNode();

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

    private void givenRootNode() {
        root = Node.builder().build();
        nodeRepository.saveAndFlush(root);
    }

    private void whenGetChildren(UUID nodeId) throws Exception {
        resultActions = mockMvc.perform(get("/nodes/{nodeId}/children", nodeId));
    }

    @SneakyThrows
    private void thenChildrenNodesHaveNewHeight() {
        whenGetChildren(nodeToCheck.getId());

        resultActions
                .andExpect(jsonPath("children[0].height", is(2)))
                .andExpect(jsonPath("children[1].height", is(2)))
                .andExpect(jsonPath("children[2].height", is(2)))
                .andExpect(jsonPath("children[3].height", is(3)));
    }

    private void whenSetParentAsRootNode(UUID nodeId) throws Exception {
        resultActions = mockMvc.perform(
                put("/nodes/{nodeId}/parent", nodeId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"parentId\" : \"" + root.getId().toString() + "\"}"))
        ;
    }

    private void thenTheHeightIsUpdatedAndParentNodeIsRoot() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("height", is(1)))
                .andExpect(jsonPath("parent.id", is(root.getId().toString())))
                .andDo(print());
    }

    @SneakyThrows
    private void whenCreateNode() {
        resultActions = mockMvc.perform(post("/nodes")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodePayload)));

    }

    private void givenNodePayload() {
        givenRootNode();
        nodePayload = NodePayload.builder().parentId(root.getId()).rootId(root.getId()).build();
    }

    private void givenInvalidPayloadForRootNode() {
        nodePayload = NodePayload.builder().parentId(UUID.randomUUID()).build();
    }

}
