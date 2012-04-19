logloss = function(labs, probs) {
    N = length(labs)
    sum = 0
    for (i in 1:N) {
        sum = labs[i]*log(probs[i]) + (1-labs[i])*log(1-probs[i])
    -sum/N
}
