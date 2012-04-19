import numpy as np
from scikits.learn import svm

def svm_rbf(train, test):
    svc = svm.SVC(probability=True)
    svc.fit(train[:, 1:], train[:, 0])
    predicted_probs = svc.predict_proba(test)
    return predicted_probs

def svm_linear(train, test):
    svc = svm.SVC(kernel='linear', probability=True)
    svc.fit(train[:, 1:], train[:, 0])
    predicted_probs = svc.predict_proba(test)
    return predicted_probs
