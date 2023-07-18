# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

import math
import random

#in n = #Vertices, p = percentage of clique, d = density between C&I
#p_blue = probability that C\I Edge is blue

#generating clique 





def generate_random_graph(n, p, d, p_blue, filename):
    
    clique_size = math.ceil(n * p/100)

    clique_edges = []
    c_i_edges = []

    #vertices 0-clique_size-1 are in C and have blue edge (type 2)
    for i in range(clique_size):
    
        for j in range(i+1, clique_size):
            clique_edges.append([i, j, 2])

    for i in range(clique_size) :
    
       for j in range(clique_size, n):
           rnd = random.random()
           if rnd < d:
               rnd2 = random.random()
               if rnd2 < p_blue:
                   c_i_edges.append([i, j, 2])
               elif rnd2 >= p_blue:
                   c_i_edges.append([i, j, 1])
                

        

    edges = clique_edges + c_i_edges

    random.shuffle(edges)
    #check if file exists
    with open(r'./data/synthetic/splitgraph/random/' + filename, 'w') as file:
        file.write('% n=' + str(n) + " p=" + str(p) + " d=" + str(d) + " p_blue=" + str(p_blue)+ "\n")
        for edge in edges:
            file.write('%s %s %s\n' %(edge[0], edge[1], edge[2]))
    
    return clique_size



