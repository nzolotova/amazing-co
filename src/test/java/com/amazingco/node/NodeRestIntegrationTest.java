package com.amazingco.node;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
        whenGetChildren(root.getId());

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
        whenGetChildren(nodeToCheck.getId());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("children", hasSize(4)))
                .andDo(print());
    }

    @Test
    public void should_fail_to_get_children_for_non_existing_node() throws Exception {

        //given
        givenTreeWithNodes();

        //when
        whenGetChildren(UUID.randomUUID());

        //then
        resultActions.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void should_update_node_with_new_parent_node() throws Exception {

        //given
        givenTreeWithNodes();

        //when
        whenUpdateParentWithRootNode(nodeToCheck.getId());

        //then
        thenTheHeightIsUpdatedAndParentNodeIsRoot();
        thenChildrenNodesHaveNewHeight();

    }

    @Test
    public void should_fail_to_update_non_existing_node() throws Exception {

        //given
        givenTreeWithNodes();

        //when
        whenUpdateParentWithRootNode(UUID.randomUUID());

        //then
        resultActions.andExpect(status().isNotFound())
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

    private void whenUpdateParentWithRootNode(UUID id) throws Exception {
        resultActions = mockMvc.perform(
                patch("/nodes/{nodeId}/parent", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"parentId\" : \"" + root.getId().toString() + "\"}"))
        ;
    }

    private void thenTheHeightIsUpdatedAndParentNodeIsRoot() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("height", is(1)))
                .andExpect(jsonPath("parent.id", is(root.getId().toString())))
                .andDo(print());
    }


}
