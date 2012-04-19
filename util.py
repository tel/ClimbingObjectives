import numpy as np

def objective(labels, probs):

    """ Computes the log-loss utility function. Labels is a vector/list of 1/0
    labels and probs is a vector/list of the posterior probability that the
    label=1. """

    sum = 0
    N   = 0
    for (l, p) in zip(labels, probs):
        sum += l*np.log(p) + (1-l)*np.log(1-p)
        N += 1
    return -sum/N
