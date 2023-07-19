Due to surprising results during the experiments, the naming of the different algorithms are a bit weird 
but kept this way to keep them consistent with the names of the test files. 

The algorithm called total_red as well as its results etc all include the rules that check if a vertex can only labeled one way. For 
the algorithm that actually only uses total sum of induced costs only_total_red... was used. 

The scripts used to generate synthetic data, as well as the testscripts and the scripts to plot the results use absolut paths from the local machine.
To adapt them to your machine replace:


C://Users/Markus/master-markus/Code/data/ (...) with ../data/ (...) for the generation and test scripts and

C://Users/Markus/master-markus/Code/data/synthetic/ (...) with ./ (...) for the plotting.