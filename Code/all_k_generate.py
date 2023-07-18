# -*- coding: utf-8 -*-
"""
Created on Mon Jan 16 15:48:08 2023

@author: Markus
"""

import random_generation
#import degree_sequence_generation
import changes


#generate randoms
rnd_name = 'random_split_'

n_list = range(25, 101, 25)
p_list = [33, 60]               #clique size
d_list = [0.33, 0.66]          
blue_list = [0.33, 0.66]       #C/I blue probability
random_graph_list = []

for i in n_list:
    cnt = 1
    
    for p in p_list:
        
        for d in d_list:
            
            for b in blue_list:
                
                filename = rnd_name + str(i) + "_" + str(cnt) + ".txt"
                random_graph_list.append(filename)
                random_generation.generate_random_graph(i, p, d, b, filename)
                cnt += 1



#generate sequences
# sequence_name = 'sequence_split_'
# sequence_graph_list = []

# for i in range(25):
#     cnt = 1
    
        
#     for b in blue_list:
#         filename = sequence_name + str(i) + "_" + str(cnt)+".txt"
#         sequence_graph_list.append(filename)
#         degree_sequence_generation.generate_sequence_graph(n_list[i], b, filename)
#         cnt += 1
        
        
#changes

for f in random_graph_list:
    
    
    for k in range(5, 101, 5):
        changes.change_graphs("random/" + f, k, f[:-4] + "_k" + str(k) + ".txt")

# for f in sequence_graph_list:
    
    
#     for k in range(5, 30):
#         changes.change_graphs("degreeSequence/" + f, k, f[:-4] + "_k" + str(k) + "_1.txt")
#         changes.change_graphs("degreeSequence/" + f, k, f[:-4] + "_k" + str(k) + "_2.txt")            