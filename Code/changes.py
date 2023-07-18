# -*- coding: utf-8 -*-
"""
Created on Mon Dec 19 16:11:02 2022

@author: Markus
"""

import random

file_name = r'degreeSequence/test_graph.txt'            #TODO

path  = r'./data/synthetic/splitgraph/'



out_name = 'k10_synthetic_001.txt'


def change_graphs (in_name, k, out_name, clique_size):
    
    #add check that only edges in clique or in is are changed!
    
    edges= []
    vertices = []
    

    with open(path + in_name, 'r') as file:
        first = True
        for line in file:
            if line[0] == '%':
                if first:
                    first_line = line
                continue
            first = False
            l = line[:-1]
            edge = l.split(' ')
            u = edge[0]
            v = edge[1]
            if u not in vertices:
                vertices.append(u)
                if v not in vertices:
                    vertices.append(v)
        
            edges.append(edge)


    #add k reduction and non-edge case

    while k > 0 :
        #select edge
        edge_rnd = random.sample(vertices, 2)
        
        #bessere lösung zum sortieren?
        edge_int = [int(edge_rnd[0]), int(edge_rnd[1])]
        edge_int.sort()
        edge = [str(edge_int[0]), str(edge_int[1])]
    
    
        #select change
    
    
        rnd = random.random()
    

    
        red_edge  = list(edge)
        red_edge.append('1')
        if red_edge in edges:
            
            if rnd < 0.5 and k>=1:                                   #50-50 chance to do either change
                edges.remove(red_edge)
                red_edge[2] = '2'                             #recolor
                edges.append(red_edge)
                k = k-1
                print('%s changed' %red_edge)
                continue
        
            elif rnd >= 0.5 and k>=1:
                edges.remove(red_edge)                      #remove
                k = k-1
                print('%s removed' %red_edge)
                continue
    
        blue_edge = list(edge)
        blue_edge.append('2')
        if blue_edge in edges:
        
            if rnd < 0.5 and k>=1:
                edges.remove(blue_edge)
                blue_edge[2] = '1'
                edges.append(blue_edge)
                k = k-1
                print('%s changed' %blue_edge)
                continue
        
            elif rnd >= 0.5 and k>=1 :
                edges.remove(blue_edge)
                k = k-1
                print('%s removed' %blue_edge)
                continue
        else :
        
            new_edge = list(edge)
            if rnd < 0.5 and k>=1:
            
                new_edge.append('1')
                edges.append(new_edge)
                print('%s added red' %new_edge)
                k = k-1
                continue
        
            elif rnd >= 0.5 and k>=1:
            
                new_edge.append('2')
                edges.append(new_edge)
                print('%s added blue' %new_edge)
                k = k-1
                continue
            
    

    with open('./data/synthetic/' + out_name, 'w') as file:
        file.write(first_line + "% k="+ str(k) + '\n')
        for edge in edges:
            file.write('%s %s %s\n' %(edge[0], edge[1], edge[2]))    



    