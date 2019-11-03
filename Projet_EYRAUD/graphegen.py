#!/usr/bin/env python
# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
import networkx as nx
import networkx.algorithms.approximation as nxaa
from collections import OrderedDict, deque
import copy
import operator
import sys
import math
from timeit import default_timer as timer



class DominatingSets:
    @classmethod
    def get_dominating_sets(cls, G, weight=None):
        """get a dominating sets 
        """
        dominating_sets = nxaa.min_weighted_dominating_set(G, weight=weight)

        return dominating_sets

    @classmethod
    def min_connected_dominating_sets_non_distributed(cls, G):
        """Compute a CDS, based on algorithm of Butenko, Cheng, Oliveira, Pardalos

            Based on the paper: BUTENKO, Sergiy, CHENG, Xiuzhen, OLIVEIRA, Carlos A., et al. A new heuristic for the minimum connected dominating set problem on ad hoc wireless networks. In : Recent developments in cooperative control and optimization. Springer US, 2004. p. 61-73.
        """
        assert nx.is_connected(G) 

        G2 = copy.deepcopy(G)
        
        # Step 1: initialization
        # take the node with maximum degree as the starting node
        
        starting_node = max(dict(G2.degree()).items(), key=operator.itemgetter(1))[0] 
        fixed_nodes = {starting_node}

        # Enqueue the neighbor nodes of starting node to Q in descending order by their degree
        neighbor_nodes = G2.neighbors(starting_node)
        neighbor_nodes_sorted = OrderedDict(sorted(dict(G2.degree(neighbor_nodes)).items(), key=operator.itemgetter(1), reverse=True)).keys()

        priority_queue = deque(neighbor_nodes_sorted) # a priority queue is maintained centrally to decide whether an element would be a part of CDS.
        inserted_set = set(list(neighbor_nodes_sorted) + [starting_node])

        # Step 2: calculate the cds
        while priority_queue:
            u = priority_queue.pop()

            # check if the graph after removing u is still connected
            rest_graph = copy.deepcopy(G2)
            rest_graph.remove_node(u)

            if nx.is_connected(rest_graph):
                G2.remove_node(u)
            else: # is not connected 
                fixed_nodes.add(u)

                # add neighbors of u to the priority queue, which never are inserted into Q
                inserted_neighbors = set(G2.neighbors(u)) - inserted_set
                inserted_neighbors_sorted = OrderedDict(sorted(dict(G2.degree(inserted_neighbors)).items(),
                                                                key=operator.itemgetter(1), reverse=True)).keys()

                priority_queue.extend(inserted_neighbors_sorted)
                inserted_set.update(inserted_neighbors_sorted)

        # Step 3: verify the result
        assert nx.is_dominating_set(G, fixed_nodes) and nx.is_connected(G.subgraph(fixed_nodes))

        return fixed_nodes

def getDist(p1, p2):
    return math.sqrt( ((p1[0]-p2[0])**2)+((p1[1]-p2[1])**2) )
                     
def main(argv):                         
    filename = argv[0]
    edgeThreshold = int(argv[1])
    f = open(filename, "r")
    #output = open(filename + ".cds", "w")
    
    coords = []
    for line in f:
        coord = line.split(" ")
        coord[0] = int(coord[0])
        coord[1] = int(coord[1])
        coords.append(coord)
    
    G = nx.Graph()
    for n in range(len(coords)):
        G.add_node(n)
    for i in range(len(coords)):
        for j in range(i+1, len(coords)):
            if(getDist(coords[i], coords[j]) < edgeThreshold):
                G.add_edge(i, j)
                
    ds = DominatingSets()
    
    start = timer()
    cds = ds.min_connected_dominating_sets_non_distributed(G)   
    end = timer()
    time_in_ms = int((end-start)*1000)
    print(time_in_ms)
    print(len(cds))
    
    """
    for n in cds:
        output.write(str(coords[n][0]) + " " + str(coords[n][1]) + "\n")
    """
    #output.close()
    f.close()
    

if __name__ == "__main__":
    main(sys.argv[1:])