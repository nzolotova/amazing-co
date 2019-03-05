package com.amazingco.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Table(name = "NODE")
@Getter
@Setter
class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "BINARY(16) NOT NULL", unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID", nullable = true)
    private Node parent;

    @OneToOne
    @JoinColumn(name = "ROOT_ID", nullable = false)
    private Node root;

    @Column(name = "HEIGHT", nullable = false)
    private int height;


}
