import random

filepath = "NASDAQ_20080204_1.txt"
with open(filepath, 'r') as f:     # load file
    lines = f.read().splitlines()  # read lines

with open('new_NASDAQ_20080204_1.txt', 'w') as f:
    f.write('\n'.join([line+',Prob:'+(random.randint(0, 100)/100.0).__str__() for line in lines]))  # write lines with '#' appended