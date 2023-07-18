# -*- coding: utf-8 -*-
"""
Created on Sun Jul 16 10:06:45 2023

@author: Markus
"""

import re
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

str = "C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/all_k/random_split_25_8_k12.txt"

m = re.search('.*split_25_.*', str)


def plot_all_k(result_file):
    
    df = pd.read_csv(result_file, delimiter = r'[ ,]')
    
    names = df['name'][:]
    data25 = []
    data50 = []
    data75 = []
    data100 = []
    
    for index, row in df.iterrows():
        name = row[0]
        if(bool(re.search('.*split_25_.*', name))):
            data25.append(row)
            
        if(bool(re.search('.*split_50_.*', name))):
            data50.append(row)
        
        if(bool(re.search('.*split_75_.*', name))):
            data75.append(row)
        
        if(bool(re.search('.*split_100_.*', name))):
            data100.append(row)
        
    df25 = pd.DataFrame(data25)
    df50 = pd.DataFrame(data50)
    df75 = pd.DataFrame(data75)
    df100 = pd.DataFrame(data100)
    
    p25 = np.polyfit(df25['k'][:], np.log(df25['time'][:]), 1)
    a25 = np.exp(p25[1])
    b25 = p25[0]
    
    x_fitted25 = np.linspace(np.min(df25['k'][:]), np.max(df25['k'][:]))
    y_fitted25 = a25 * np.exp(b25 * x_fitted25)
    
    ax = plt.axes()
    ax.plot(x_fitted25, y_fitted25, 'k', label='n = 25', color = 'blue')
    
    
    p50 = np.polyfit(df50['k'][:], np.log(df50['time'][:]), 1)
    a = np.exp(p50[1])
    b = p50[0]
    
    x_fitted50 = np.linspace(np.min(df50['k'][:]), np.max(df50['k'][:]))
    y_fitted50 = a * np.exp(b * x_fitted50)
    
    ax.plot(x_fitted50, y_fitted50, 'k', label='n=50', color = 'red')
    
    
    p75 = np.polyfit(df75['k'][:], np.log(df75['time'][:]), 1)
    a = np.exp(p75[1])
    b = p75[0]
    
    x_fitted75 = np.linspace(np.min(df75['k'][:]), np.max(df75['k'][:]))
    y_fitted75 = a * np.exp(b * x_fitted75)
    
    ax.plot(x_fitted75, y_fitted75, 'k', label='n=75', color = 'green')
    
    p100 = np.polyfit(df100['k'][:], np.log(df100['time'][:]), 1)
    a = np.exp(p100[1])
    b = p100[0]
    
    x_fitted100 = np.linspace(np.min(df100['k'][:]), np.max(df100['k'][:]))
    y_fitted100 = a * np.exp(b * x_fitted100)
    
    ax.plot(x_fitted100, y_fitted100, 'k', label='n=100', color = 'orange')
    
    # if(bool(re.search('.*all_red.*', result_file))): ax.set_title("All reduction rules applied")
    # elif(bool(re.search('.*no_red.*', result_file))): ax.set_title("No reduction rules applied")
    # elif(bool(re.search('.*total_red.*', result_file))): ax.set_title("Total-Minimum-Cost reduction rule applied")
    # elif(bool(re.search('.*total_one_red.*', result_file))): ax.set_title("Total-Minimum & One-Degree-Neighbor applied")
    # elif(bool(re.search('.*total_dom_red.*', result_file))): ax.set_title("Total-Minimum & Dominating-Neighbor applied")    
    
    ax.set_xlabel("k")
    ax.set_ylabel("time in ms")
    ax.set_ylim(0, 2000)
    ax.legend(loc='upper left')
    
    #df25.plot.scatter(x="k", y="time")
    # df50.plot.scatter(x="k", y="time")
    # df75.plot.scatter(x="k", y="time")
    # df100.plot.scatter(x="k", y="time")
    
    plt.show()
    plt.close()
    
    return 0

def plot_all_n(result_file):
    
    df = pd.read_csv(result_file, delimiter = ' ')
    
    data10 = []
    data20 = []
    data30 = []
    data40 = []
    data50 = []
    data60 = []
    data70 = []
    data80 = []
    data90 = []
    data100 = []
    
    for index, row in df.iterrows():
        name = row[0]
        
        
        if(bool(re.search('.*_k10.*', name))):
            data10.append(row)
            
            
        if(bool(re.search('.*_k20.*', name))):
            data20.append(row)
         
        if(bool(re.search('.*_k30.*', name))):
            data30.append(row)
                
        if(bool(re.search('.*_k40.*', name))):
            data40.append(row)
            
        if(bool(re.search('.*_k50.*', name))):
            data50.append(row)

        if(bool(re.search('.*_k60.*', name))):
            data60.append(row)
    
        if(bool(re.search('.*_k70.*', name))):
            data70.append(row)
            
        if(bool(re.search('.*_k80.*', name))):
            data80.append(row)
                
        if(bool(re.search('.*_k90.*', name))):
            data90.append(row)
                
        if(bool(re.search('.*_k100.*', name))):
            data100.append(row)
    
    
    
    df10 = pd.DataFrame(data10)
    df20 = pd.DataFrame(data20)
    df30 = pd.DataFrame(data30)
    df40 = pd.DataFrame(data40)
    df50 = pd.DataFrame(data50)
    df60 = pd.DataFrame(data60)
    df70 = pd.DataFrame(data70)
    df80 = pd.DataFrame(data80)
    df90 = pd.DataFrame(data90)
    df100 = pd.DataFrame(data100)
    
    p20 = np.polyfit(df20['n'][:], np.log(df20['time'][:]), 1)
    a = np.exp(p20[1])
    b = p20[0]
        
    x_fitted20 = np.linspace(np.min(df20['n'][:]), np.max(df20['n'][:]))
    y_fitted20 = a * np.exp(b * x_fitted20)
        
    ax = plt.axes()
    ax.plot(x_fitted20, y_fitted20, label='k=20', color = 'blue')
        
        
    p50 = np.polyfit(df50['n'][:], np.log(df50['time'][:]), 1)
    a = np.exp(p50[1])
    b = p50[0]
        
    x_fitted50 = np.linspace(np.min(df50['n'][:]), np.max(df50['n'][:]))
    y_fitted50 = a * np.exp(b * x_fitted50)
    
    ax.plot(x_fitted50, y_fitted50, label='k=50', color = 'red')
    
    
    p70 = np.polyfit(df70['n'][:], np.log(df70['time'][:]), 1)
    a = np.exp(p70[1])
    b = p70[0]
    
    x_fitted70 = np.linspace(np.min(df70['n'][:]), np.max(df70['n'][:]))
    y_fitted70 = a * np.exp(b * x_fitted70)
    
    ax.plot(x_fitted70, y_fitted70, label='k=70', color = 'green')
        
    p100 = np.polyfit(df100['n'][:], np.log(df100['time'][:]), 1)
    a = np.exp(p100[1])
    b = p100[0]
    
    x_fitted100 = np.linspace(np.min(df100['n'][:]), np.max(df100['n'][:]))
    y_fitted100 = a * np.exp(b * x_fitted100)
    
    ax.plot(x_fitted100, y_fitted100, label='k=100', color = 'orange')
    
    if(bool(re.search('.*all_red.*', result_file))): ax.set_title("All reduction rules applied")
    elif(bool(re.search('.*no_red.*', result_file))): ax.set_title("No reduction rules applied")
    elif(bool(re.search('.*total_red.*', result_file))): ax.set_title("Total-Minimum-Cost reduction rule applied")
    elif(bool(re.search('.*total_one_red.*', result_file))): ax.set_title("Total-Minimum & One-Degree reduction")
    elif(bool(re.search('.*total_dom_red.*', result_file))): ax.set_title("Total-Minimum & Dominating reduction")    
    
    ax.set_xlabel("n")
    ax.set_ylabel("time in ms")
    ax.set_ylim(0, 4000)
    ax.legend(loc='upper left')
    


    # df10.plot.scatter(x="n", y="time")
    # df20.plot.scatter(x="n", y="time")
    # df30.plot.scatter(x="n", y="time")
    # df40.plot.scatter(x="n", y="time")
    # df50.plot.scatter(x="n", y="time")
    # df60.plot.scatter(x="n", y="time")
    # df70.plot.scatter(x="n", y="time")
    # df80.plot.scatter(x="n", y="time")
    # df90.plot.scatter(x="n", y="time")
    # df100.plot.scatter(x="n", y="time")
    
    plt.show()
    plt.close()
    return 0




def plot_real_k(result_file):
    df = pd.read_csv(result_file, delimiter = ' ')
    
    data50 = []
    data100 = []
    data150 = []
    data200 = []
        
    for index, row in df.iterrows():
        name = row[0]
        if(bool(re.search('.*split_50_.*', name))):
            data50.append(row)
            
        if(bool(re.search('.*split_100_.*', name))):
            data100.append(row)
        
        if(bool(re.search('.*split_150_.*', name))):
            data150.append(row)
        
        if(bool(re.search('.*split_200_.*', name))):
            data200.append(row)
    
    df50 = pd.DataFrame(data50)
    df100 = pd.DataFrame(data100)
    df150 = pd.DataFrame(data150)
    df200 = pd.DataFrame(data200)
    
    p50 = np.polyfit(df50['k'][:], np.log(df50['time'][:]), 1)
    a = np.exp(p50[1])
    b = p50[0]
     
    x_fitted50 = np.linspace(np.min(df50['k'][:]), np.max(df50['k'][:]))
    y_fitted50 = a * np.exp(b * x_fitted50)
    
    ax = plt.axes()
    ax.plot(x_fitted50, y_fitted50, 'k', label='n = 50', color = 'blue')
    
    
    p100 = np.polyfit(df100['k'][:], np.log(df100['time'][:]), 1)
    a = np.exp(p100[1])
    b = p100[0]
    
    x_fitted100 = np.linspace(np.min(df100['k'][:]), np.max(df100['k'][:]))
    y_fitted100 = a * np.exp(b * x_fitted100)
    
    ax.plot(x_fitted100, y_fitted100, 'k', label='n=100', color = 'red')
    
    
    p150 = np.polyfit(df150['k'][:], np.log(df150['time'][:]), 1)
    a = np.exp(p150[1])
    b = p150[0]
    
    x_fitted150 = np.linspace(np.min(df150['k'][:]), np.max(df150['k'][:]))
    y_fitted150 = a * np.exp(b * x_fitted150)
    
    ax.plot(x_fitted150, y_fitted150, 'k', label='n=150', color = 'green')
     
    p200 = np.polyfit(df200['k'][:], np.log(df200['time'][:]), 1)
    a = np.exp(p200[1])
    b = p200[0]
    
    x_fitted200 = np.linspace(np.min(df200['k'][:]), np.max(df200['k'][:]))
    y_fitted200 = a * np.exp(b * x_fitted200)
     
    ax.plot(x_fitted200, y_fitted200, 'k', label='n=200', color = 'orange')
     
    ax.set_xlabel("k")
    ax.set_ylabel("time in ms")
    ax.set_ylim(0, 10000)
    ax.legend(loc='upper left')
    
    return 0


if __name__ == '__main__':
    
    
    plot_all_k("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_k_output_only_total_red.txt")
    plot_all_k("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_k_output_ILP.txt")
    
    # plot_real_k("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_n_output_total_red.txt")
    # plot_real_k("C://Users/Markus/master-markus/Code/data/synthetic/np_cost/all_n_output_total_red.txt")
    
    # plot_all_k("C://Users/Markus/master-markus/Code/data/synthetic/np_cost/all_k_output_total_red.txt")
    # plot_all_n("C://Users/Markus/master-markus/Code/data/synthetic/np_cost/all_n_output_total_red.txt")
    
    # plot_all_k("C://Users/Markus/master-markus/Code/data/synthetic/np_cost/all_k_output_all_red.txt")
    # plot_all_n("C://Users/Markus/master-markus/Code/data/synthetic/np_cost/all_n_output_all_red.txt")
    
    #plot_all_k("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_k_output_all_red_avg.txt")
    #plot_all_k("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_k_output_all_red_max.txt")
    #plot_all_k("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_k_output_all_red_rnd.txt")

    # plot_all_n("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_n_output_total_red.txt")
    # plot_all_n("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_n_output_total_one_red.txt")
    # plot_all_n("C://Users/Markus/master-markus/Code/data/synthetic/polytime_cost/results/all_n_output_total_dom_red.txt")
    
    
    
    
    
    
    
    
    
    
    
    
    
    