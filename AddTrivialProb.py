import random

filepath = "NASDAQ_20080204_1.txt"
with open(filepath, 'r') as f:  # load file
    lines = f.read().splitlines()  # read lines

with open('NASDAQ_With_Trivial_Prob.txt', 'w') as f:
    for line in lines:
        cols = line.split(',')
        length = len(cols)

        for i in range(length):  # add attribute uncertainty
            if i < 2:
                f.write(cols[i] + ',')
            elif i<22:
                col = '[ ' + cols[i] + ':1.0 ],'
                f.write(col)

        f.write('Prob:1.0')  # add prob
        f.write('\n')

