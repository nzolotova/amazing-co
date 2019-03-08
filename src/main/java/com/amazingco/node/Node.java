package com.amazingco.node;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NODE")
@Builder
@Getter
@Setter
@NodeConstraint
class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Node parent;

    @OneToOne
    @JoinColumn(name = "ROOT_ID")
    private Node root;

    @Column(name = "HEIGHT")
    private int height;

}
