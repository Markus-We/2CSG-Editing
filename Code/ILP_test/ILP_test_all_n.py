# -*- coding: utf-8 -*-
#run the experiments for all relevant values of k and one specific choice of algorithm

from __future__ import print_function
from subprocess import call

import sys
import random
import os
import argparse
import glob
import subprocess as sp
import multiprocessing as mp


def work(in_file):
    """Defines the work unit on an input file"""
    # # each line in the file contains graph file, parameter k, the used algorithm, and a unique problem ID
    # split_line = in_file.split("//")
    # # first entry is output
    # data_file = split_line[0]
    # print("data_file: " + str(data_file) )
    # # second entry is k
    # k = split_line[1].lstrip('/')
    # print("k: " + str(k))
    # # third entry is the used heuristic
    # variant = split_line[2].lstrip('/')
    # print("variant: " + str(variant))
    
    in_path = "../data/synthetic/polytime_cost/all_n/" + in_file

    out_path = "../data/synthetic/polytime_cost/all_n_output_ILP.txt"
    
    sp.call(["java","-jar", "../algorithm/TCSG_ILP.jar", in_path, out_path, "1", "2"])
    return 0
 
if __name__ == '__main__':
    files = []
    with open("../data/synthetic/polytime_cost/all_n/filenames.txt", encoding='utf-8', newline='\r\n') as f:
        
        first_line = f.readline()
        for line in f:
            print(line.strip())
            files.append(line.strip())
        
    #Set up the parallel task pool to use all available processors
    count = 12
    # shuffle such that load on each processor is more evenly distributed
    random.shuffle(files)
    pool = mp.Pool(processes=count)

    #Run the jobs
	
    pool.map(work, files)