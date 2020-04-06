import numpy as np
import pandas as pd
import os
import shutil
import time


def csv_diff(file_name, flag):
    if(flag == 1):  # online
        test_user = pd.read_csv('tests/python_outputs/output_'+file_name+'_user.csv')  # Load results from python code
        test_cep = pd.read_csv('./outputProb.csv')  # Load results from java code
    else:  # flag = 0 - offline
        test_user = pd.read_csv('tests/python_outputs/output_'+file_name+'_user.csv')  # Load results from python code
        test_cep = pd.read_csv('tests/java_outputs/output_'+file_name+'.csv')  # Load results from java code

    output_nfa_rows = int((test_cep.shape[0]-1)/2 + 1)
    py_array = np.zeros(test_user.shape[0])
    java_array = np.zeros(output_nfa_rows)

    py_array = np.sort(np.asarray(test_user['Sequence Prob']))
    java_array = np.sort(np.asarray(test_cep['Prob'][:output_nfa_rows]))

    arrays = np.array([py_array, java_array])
    diff_arr = np.diff(arrays, axis=0)
    diff_arr_abs = np.absolute(diff_arr)
    #print(diff_arr_abs[0])
    # considering the different calculations methods between Python and Java:
    if(np.amax(diff_arr_abs[0]) > 0.0000001):
        print("Failure")
        return 0
    print("SUCCESS: No differences found")
    return 1


def java_diff(file_name, flag):
    if(flag == 1):  # online
        test_cep = pd.read_csv('./outputProb.csv')  # Load results from java code
    else:  # flag = 0 - offline
        test_cep = pd.read_csv('tests/java_outputs/output_'+file_name+'.csv')  # Load results from java code

    output_nfa_rows = int((test_cep.shape[0]-1)/2 + 1)
    # eager_nfa_array = np.zeros(output_nfa_rows)
    # chain_nfa_array = np.zeros(output_nfa_rows)

    eager_nfa_array = np.sort(np.asarray(test_cep['Prob'][:output_nfa_rows]))
    chain_nfa_array = np.sort(np.asarray(test_cep['Prob'][output_nfa_rows:]))

    arrays = np.array([eager_nfa_array, chain_nfa_array])
    diff_arr = np.diff(arrays, axis=0)
    diff_arr_abs = np.absolute(diff_arr)
    #print(diff_arr_abs[0])
    # considering the different calculations methods between Python and Java:
    if(np.amax(diff_arr_abs[0]) > 0.0000000001):  # 10^-9
        print("Failure")
        return 0
    print("SUCCESS: No differences found")
    return 1


def create_java_outputs(file_name):
    folder = './input_dir_path'
    for filename in os.listdir(folder):
        file_path = os.path.join(folder, filename)
        if os.path.isfile(file_path) or os.path.islink(file_path):
            os.unlink(file_path)
    shutil.copy('tests\input_'+file_name+'.txt', folder)  # copy test to input folder
    os.system(".\\jdk-11.0.5\\bin\java.exe -jar .\\cep.jar")
    output_file = './outputProb.csv'
    output_path = 'tests/java_outputs/output_'+file_name+'.csv'
    shutil.move(output_file, output_path)  # move output to folder


def start_again(on_name):
    folder = './input_dir_path'
    for filename in os.listdir(folder):
        file_path = os.path.join(folder, filename)
        if os.path.isfile(file_path) or os.path.islink(file_path):
            os.unlink(file_path)
    shutil.copy('tests\input_'+on_name+'.txt', folder)  # copy test for online to input folder


if __name__ == "__main__":

# ONLINE:

    flag = 1
    on_idx = 1
    on_name = "test" + str(on_idx)
    file_name_on = "input_" + on_name

    '''for i in range(1, 31): #Generate 100 java outputs for 100 tests
        on_name_java = "test" + str(i)
        create_java_outputs(on_name_java)
    start_again(on_name)'''

# Load test - 300K rows
    print("Running test with 300,000 input rows in java:")
    start_time = time.time()
    create_java_outputs('NASDAQ_300K')
    print("Time: --- %s seconds ---" % (time.time() - start_time))

# Prepare for another run from the start
    start_again(on_name)
    print("__________________________________________________________________________________________________________")

    print("Running test online:")
    print(on_name)
    print("Running Python cep:")
    os.system("CEP-correctness.py "+on_name)

    print("Running Java cep")
    os.system(".\\jdk-11.0.5\\bin\java.exe -jar .\\cep.jar")
    # relative path: outputProb.csv (in project root)
    print("Comparing Java and Python outputs:")
    csv_diff(on_name, flag)
    print("Comparing Java mechanisms outputs:")
    java_diff(on_name, flag)
    print("_______________________________________________________________________________________________________")

# OFFLINE:
    flag = 0
    print("Comparing outputs of already generated tests:")
    for off_idx in range(1, 31):
        off_name = "test"+str(off_idx)
        # file_name_off = "input_"+off_name+".txt"
        print(off_name)
        print("Comparing Java and Python outputs:")
        csv_diff(off_name, flag)
        print("Comparing Java mechanisms outputs:")
        java_diff(off_name, flag)
    print("_______________________________________________________________________________________________________")

