# amazing-co

## Task description

We in Amazing Co need to model how our company is structured so we can do awesome stuff.

We have a root node (only one) and several children nodes,each one with its own children as well. It's a tree-based structure.

Something like:

         root

        /    \

       a      b

       |

       c

We need two HTTP APIs that will serve the two basic operations:
1) Get all children nodes of a given node (the given node can be anyone in the tree structure).
2) Change the parent node of a given node (the given node can be anyone in the tree structure).

They need to answer quickly, even with tons of nodes. Also,we can't afford to lose this information, so some sort of persistence is required.

Each node should have the following info:
1) node identification
2) who is the parent node
3) who is the root node
4) the height of the node. In the above example,height(root) = 0 and height(a) == 1.

Our boss is evil and we can only have docker and docker-compose on our machines. So your server needs to be run using them.

## API Documentation to the amzingco project

This is a Spring Boot Microservices with REST API.

It has three endpoints:
1. Create node:
    * Request: POST /nodes
    * Payload example for the root node:
    ```json
    {
        "parentId" : null,
        "rootId" : null
    }
    ```
    * Payload example for the child node:
    ```json
    {
        "parentId" : "5043675a-1792-4ad1-9a97-0d17aa0382c2",
        "rootId" : "5043675a-1792-4ad1-9a97-0d17aa0382c2"
    }
    ```

    Note: parentId and rootId should be in UUID format.

    * Respose payload is a Node resourse:
    ```json
    {
        "id": "aa11325a-d707-468c-8ece-146e317ec2df",
        "parent": {
            "id": "5043675a-1792-4ad1-9a97-0d17aa0382c2",
            "parent": null,
            "root": null,
            "height": 0
        },
        "root": {
            "id": "5043675a-1792-4ad1-9a97-0d17aa0382c2",
            "parent": null,
            "root": null,
            "height": 0
        },
        "height": 1
    }
    ```

    * Constraints for the payload (Can be changed accroding to business requirements):
        * Root node can not be created twice
        * rootId and parentId can be null or set at the same time. It's forbidden to have only rootId or only parentId.
    * Height of the node is calculated at the creation step depending on the height of the parentnode.
2. Get children of the node
    * Request: GET /nodes/{nodeId}/children
    * Responce payload is a list of children nodes:
    ```json
    {
        "children": [
            {
                "id": "5043675a-1792-4ad1-9a97-0d17aa0382c2",
                "parent": {
                    "id": "aa11325a-d707-468c-8ece-146e317ec2df",
                    "parent": {
                        "id": "50e8493a-4fa1-47ea-b255-5f682dc75d87",
                        "parent": null,
                        "root": null,
                        "height": 0
                    },
                    "root": {
                        "id": "50e8493a-4fa1-47ea-b255-5f682dc75d87",
                        "parent": null,
                        "root": null,
                        "height": 0
                    },
                    "height": 1
                },
                "root": {
                    "id": "aa11325a-d707-468c-8ece-146e317ec2df",
                    "parent": {
                        "id": "50e8493a-4fa1-47ea-b255-5f682dc75d87",
                        "parent": null,
                        "root": null,
                        "height": 0
                    },
                    "root": {
                        "id": "50e8493a-4fa1-47ea-b255-5f682dc75d87",
                        "parent": null,
                        "root": null,
                        "height": 0
                    },
                    "height": 1
                },
                "height": 2
            }
        ]
    }
    ```
3. Update parent of the node
    * Request: PUT /nodes/{nodeId}/parent
    * Request payload contains parentId:
    ```json
    {
        "parentId" : "aa11325a-d707-468c-8ece-146e317ec2df"
    }
    ```
    * Responce payload contains a Node resource with the new parentId as in the create endpoint.
    * Height of the node and its children is recalculated according to the height of the new parent.

## How to use?
1. Build a gradle jar file in the terminal: ``` gw build ```
2. Run ``` docker-compose up ```
3. Image should be created and the service is up in the docker container.
4. Make requests via terminal or some Rest Client to the port 8080.
