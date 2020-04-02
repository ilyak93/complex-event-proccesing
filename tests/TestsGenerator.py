import random

# num of tests
for i in range(1, 31):
    filepath = "NASDAQ_200Row.txt"
    with open(filepath, 'r') as f:  # load file
        lines = f.read().splitlines()  # read lines

    with open('200Row_test' + i.__str__() + '.txt', 'w') as f:
        for line in lines:
            cols = line.split(',')
            length = len(cols)

            for i in range(length):  # add attribute uncertainty
                if i < 2:
                    f.write(cols[i] + ',')
                elif i < 22:
                    prob = (random.randint(0, 100) / 100.0)
                    rand = random.randint(0, 100)
                    col = '[ ' + cols[i] + ':' + prob.__str__() + ' ' + random.randint(0, 100).__str__() + ':' + round(
                        1 - prob, 2).__str__() + ' ],'
                    f.write(col)

            f.write('Prob:' + (random.randint(0, 100) / 100.0).__str__())  # add occurrence prob
            f.write('\n')
