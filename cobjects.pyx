import numpy as np
cimport numpy as np
from libc.math cimport sqrt

cimport cython

DTYPE = np.float64
ctypedef np.float64_t DTYPE_t



@cython.boundscheck(False)
@cython.wraparound(False)
@cython.cdivision(True)
def nn(np.ndarray[DTYPE_t, ndim=2] train not None, 
       np.ndarray[DTYPE_t, ndim=2] test not None):
    """Fast array access to compute the nearest neighbor of every test
    data point. Pass only the {X}_train and {X}_test --- no labels!
    Return is a 1-d array of the indices of the nearest neighbors of
    each element of `test`."""

    assert train.dtype == DTYPE
    assert test.dtype  == DTYPE

    cdef unsigned int ndim = min(train.shape[1], test.shape[1])
    cdef unsigned int ntrain = train.shape[0]
    cdef unsigned int ntest = test.shape[0]

    cdef np.ndarray out = np.zeros((ntest, ), dtype = np.uint32)
    cdef unsigned int i, j, k
    cdef np.uint32_t idx
    cdef DTYPE_t minv, dist

    for i in range(ntest):
        idx = 0
        minv = 0
        for j in range(ntrain):
            dist = 0
            for k in range(ndim):
                dist += train[j, k]*test[i, k] # dot products
            dist = sqrt(dist)
            if j == 0 or dist < minv:
                minv = dist
                idx = j
        out[i] = idx

    return out
