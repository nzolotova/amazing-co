package com.amazingco.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<Node, UUID> {

    List<Node> findByRootId(UUID id);

    List<Node> findByParentId(UUID id);

}
