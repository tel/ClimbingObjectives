import cobjects as c
import numpy as np
import numpy.linalg as l

g = np.genfromtxt

tr = g("train.csv", delimiter=",")[1:, :]
te = g("test.csv", delimiter=",")[1:, :]

trX = tr[:, 1:]
S   = np.cov(trX.transpose())
w, v = l.eigh(S)

wprune = w[-100:]
vprune = v[:, -100:]

trPCA = np.dot(trX, vprune)
tePCA = np.dot(te, vprune)

neigh = c.nn(trPCA, tePCA)

for n in neigh:
    print int(tr[n, 0])
