import random
import pandas as pd

def newdelta(x0, x1, y0, y1, z0, z1):
    if round(abs(float(x1 - x0)), 5) <= round(abs(float(y1 - y0)), 5) <= round(abs(float(z1 - z0)), 5):
        return 1
    return 0
	
def init_flags_array(pce, num_of_attr):

    lengths = []
    for i in range(3):
        start = i*num_of_attr
        end = i*num_of_attr+num_of_attr
        for j in range(start, end):
            lengths.append(len(pce[j]))

    participate_array =[]
    t = 0 
    for i in range(3):
        start = i*num_of_attr-1
        end = i*num_of_attr+num_of_attr-1
        for j in range(start, end):
            sub = []
            for s in range(lengths[t]):
                sub.append(0)
            participate_array.append(sub)

            t = t+1

    return participate_array
	
def flag_events_array(pce, inner_match, num_of_attr, participate_array):

    k = 0
    for i in range(3):
        for j in range(i*num_of_attr,i*num_of_attr+num_of_attr):
            for tup in range(len(pce[j])):
                if((pce[j][tup] == inner_match[k]) or (pce[j][tup] == inner_match[k+3])):
                    participate_array[j][tup] = 1
        k = k+1
    return participate_array
	
def update_events_array(pce, participate_array, num_of_attr):

    updated_events = pce.copy()
  
    indices_to_delete = []
    sub = []
    t = 0
    for i in range(3):
        start = i*num_of_attr
        end = i*num_of_attr+num_of_attr
        for j in range(start, end):
            sub = updated_events[j]
            for s in range(len(sub)):
                if(participate_array[j][s] != 1):
                    index = t
                                        
                    if index not in indices_to_delete:
                        indices_to_delete.append(index)
                t = t+1

    relevant = []
    k = 0
    for sub in updated_events:
        new_sub = []
        for ele in sub:
            if(k not in indices_to_delete):
                new_sub.append(ele)
            k = k+1
        relevant.append(new_sub)
    
    return relevant
	
def create_pce(lines, i, j, k):
    event_attr = []
    all_attr = []
    counter = 0
    idx = 0
    for line in lines:
        if((i == idx) or (j == idx) or (k == idx)):
            counter = counter+1
            list_val_prob = []
            cols = line.split(',')
            length = len(cols)

            for t in range(length):
                if 1 < t < 4:
                    values = []
                    probs = []
                    for word in cols[t].replace('[', '').replace(']', '').split(' '):
                        if len(word) <= 1:
                            continue
                        s = 0

                        for zog in word.split(':'):
                            if s % 2 == 0:
                                values.append(zog)
                            else:
                                 probs.append(zog)

                            s = s + 1

                    list_val_prob = list(zip(values, probs))
                    all_attr.append(list_val_prob)
                  
        idx = idx+1

    return(all_attr)
	
def create_dynamic_update_df(events, serials, pce, updated_events, events_prob):

    attr_for_delta_before = []
    attr_for_delta_after = []
    for i in range(0,len(pce)-1,2):
        attr_for_delta_before.append([pce[i], pce[i+1]])
        attr_for_delta_after.append([updated_events[i], updated_events[i+1]])
        
    data_before = {'Stock': events, 'TimeStamp': serials, 'Attributes for delta': attr_for_delta_before, 'Stock Prob': events_prob,
                  'Status': ['Before', 'Before', 'Before']}
    data_after = {'Stock': events, 'TimeStamp': serials, 'Attributes for delta': attr_for_delta_after, 'Stock Prob': events_prob,
                  'Status': ['After', 'After', 'After']}
    df_before = pd.DataFrame(data_before, index = [1,2,3])
    df_after = pd.DataFrame(data_after, index = [1,2,3])
    frames = [df_before, df_after]
    result = pd.concat(frames)
    
    #before and after - sorted togther: 
    events_ba = [data_before['Stock'][0],data_after['Stock'][0], data_before['Stock'][1],data_after['Stock'][1], data_before['Stock'][2],data_after['Stock'][2]]
    serials_ba = [data_before['TimeStamp'][0],data_after['TimeStamp'][0], data_before['TimeStamp'][1],data_after['TimeStamp'][1], data_before['TimeStamp'][2],data_after['TimeStamp'][2]]
    attr_for_delta_ba = [data_before['Attributes for delta'][0],data_after['Attributes for delta'][0], data_before['Attributes for delta'][1],
                         data_after['Attributes for delta'][1], data_before['Attributes for delta'][2],data_after['Attributes for delta'][2]]
    events_prob_ba = [data_before['Stock Prob'][0],data_after['Stock Prob'][0], data_before['Stock Prob'][1],data_after['Stock Prob'][1], data_before['Stock Prob'][2],data_after['Stock Prob'][2]]
    status_ba = [data_before['Status'][0],data_after['Status'][0], data_before['Status'][1],data_after['Status'][1], data_before['Status'][2],data_after['Status'][2]]
    
    data_ba = {'Stock': events_ba, 'TimeStamp': serials_ba, 'Attributes for delta': attr_for_delta_ba, 'Stock Prob': events_prob_ba,
                  'Status': status_ba}
    df_ba = pd.DataFrame(data_ba, index=[1,1,2,2,3,3])
    return result, df_ba
	
def create_final_match_df(events, serials,final_prob):
    final_data = {'Stock1': [events[0]], 'TimeStamp1': [serials[0]],'Stock2': [events[1]], 'TimeStamp2': [serials[1]],
                  'Stock3': [events[2]], 'TimeStamp3': [serials[2]],'Pattern Prob':[final_prob]}
    match_df = pd.DataFrame(final_data)
    return match_df
	
def lessThenD(l1, l2, el, sl, pce, epl, num_of_attr, i, j, k):

    pattern_events_prob = float(epl[i])*float(epl[j])*float(epl[k])
    events = [el[i], el[j], el[k]]
    serials = [sl[i], sl[j], sl[k]]
    events_prob = [epl[i], epl[j], epl[k]]
    original_pce = pce
    prob = 0.0
    list_triple = []
    all_inner_matches =[]
    all_events = []
    all_serials = []
    all_inner_probs = []
    participate_array = init_flags_array(pce, num_of_attr)

    print("Calculations of inner matchs probabilities:")
    for tup1 in l1[i]:
        for tup2 in l1[j]:
            for tup3 in l1[k]:
                for tup4 in l2[i]:
                    for tup5 in l2[j]:
                        for tup6 in l2[k]:
                            if (newdelta(float(tup1[0]), float(tup4[0]), float(tup2[0]), float(tup5[0]), float(tup3[0]),
                                         float(tup6[0]))) == 1:

                                inner_match = [tup1, tup2, tup3, tup4, tup5, tup6]
                                all_inner_matches.append(inner_match)
                                all_events.append(events)
                                all_serials.append(serials)
                                inner_prob = float(tup1[1]) * float(tup2[1]) * float(tup3[1]) * float(tup4[1]) * float(
                                    tup5[1]) * float(tup6[1])
                                if(inner_prob != 0):
                                    all_inner_probs.append(round(inner_prob,4))
                                    print(tup1[1]+"*"+tup2[1]+"*"+tup3[1]+"*"+tup4[1]+"*"+tup5[1]+"*"+tup6[1])

                                    flags = flag_events_array(original_pce, inner_match, num_of_attr, participate_array)
                                    participate_array = flags
                                prob = prob + inner_prob
    print("Inner matches final probs:") 
    print(all_inner_probs)
    print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    print("Relevant attributes in each inner match:")
    dynamic_updates = list(zip(all_inner_probs, all_events, all_serials, all_inner_matches))
    for i in range(len(dynamic_updates)):
        print(dynamic_updates[i])
    print("Number of inner matches:", len(dynamic_updates))
    print("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    updated_events = update_events_array(pce, participate_array, num_of_attr)
    print("Updated current events in pattern:")
    print(updated_events)
    print("#############################################################################################################")
    mdf = create_dynamic_update_df(events, serials, pce, updated_events, events_prob)
    match_df = pd.DataFrame(mdf[0])
    match_df_ba = pd.DataFrame(mdf[1])
    print("Match attributes probability:", prob)
    final_prob = prob*pattern_events_prob
    print("Match final probability:", final_prob)
    print("#############################################################################################################")
    final_match_df = create_final_match_df(events, serials,final_prob)
    return final_prob, match_df, final_match_df, match_df_ba
	
def innerMatches(lines):
    all_atr1 = []
    all_atr2 = []
    event_list = []
    serials_list = []
    event_prob_list = []

    for line in lines:
        list_val_prob_first = ()
        list_val_prob_second = ()
        cols = line.split(',')
        length = len(cols)
       
        event_list.append(cols[0])
        serials_list.append(cols[1])
        
        for word in cols[22].replace('[', '').replace(']', '').split(' '):
                    if len(word) <= 1:
                        continue
                    idx = 0
                    for p in word.split(':'):
                        if idx % 2 != 0:
                            event_prob_list.append(p)
                        idx = idx + 1
            
        for i in range(length):
            if 1 < i < 4:
                values = []
                probs = []
                for word in cols[i].replace('[', '').replace(']', '').split(' '):
                    if len(word) <= 1:
                        continue
                    j = 0

                    for zog in word.split(':'):
                        if j % 2 == 0:
                            values.append(zog)
                        else:
                             probs.append(zog)
 
                        j = j + 1

                if i == 3:
                    list_val_prob_first = list(zip(values, probs))
                    all_atr2.append(list_val_prob_first)
                else:
                    list_val_prob_second = list(zip(values, probs))
                    all_atr1.append(list_val_prob_second)
					
    print("*************************************************************************************************************")
    print("Attributes of the third event in the pattern:")
    print(all_atr1)
    print("_____________________________________________________________________________________________________________")
    print("Attributes of the first and second events in the pattern:")
    print(all_atr2)
    print("_____________________________________________________________________________________________________________")
    return all_atr1, all_atr2, event_list, serials_list, event_prob_list
	
def calculate_match_prob(lines, num_of_attr, i, j, k):
    inner = innerMatches(lines)
    all_atr1 = inner[0]
    all_atr2 = inner[1]
    event_list = inner[2]
    serials_list = inner[3]
    epl = inner[4]
    pattern_curr_events = []
    pattern_curr_events = create_pce(lines, i, j, k)
    print("Attributes for conditions checking in current events in pattern:")
    print(pattern_curr_events)

    print("Current events in pattern: ")  
    index = 0
    for line in lines:
        if ((i == index) or (j == index) or (k == index)):
            print(line)
        index = index + 1
   
    print("************************************************************************************************************")     

    x = lessThenD(all_atr1, all_atr2, event_list,serials_list, pattern_curr_events, epl, num_of_attr, i, j, k)
    return x
	
if __name__ == "__main__":
    pattern = ['MSFT', 'CSCO', 'QTWW']
    print("Pattern: ", pattern)
    print("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
    num_of_attr = 2
    filepath = "testing_cases_Python\input\in_ABCC.txt"
    with open(filepath, 'r') as f:  # load file
        lines = f.read().splitlines()  # read lines
        found_pattern_counter = 0
        matches_counter = 0
        i = 0
        j = 0
        k = 0
        all_events = []
		
        all_dfs = [] 
        init_data = {'Stock': [], 'TimeStamp': [], 'Attributes for delta': [], 'Stock Prob': [],'Status': []}
        match_df = pd.DataFrame(init_data)
        all_dfs = match_df
		
        init_final_data = {'Stock1': [], 'TimeStamp1': [],'Stock2': [], 'TimeStamp2': [], 'Stock3': [], 'TimeStamp3': []
                           ,'Pattern Prob':[]}
        final_match_df = pd.DataFrame(init_final_data)
        final_dfs = []
        final_dfs = final_match_df
		
        all_dfs_ba = [] 
        init_data_ba = {'Stock': [], 'TimeStamp': [], 'Attributes for delta': [], 'Stock Prob': [],'Status': []}
        match_df_ba = pd.DataFrame(init_data_ba)
        all_dfs_ba = match_df_ba
        
        for line in lines:
            cols = line.split(',')
            all_events.append(cols[0])
        
        for eidx in range(len(lines)):
            if(all_events[eidx] == pattern[0]):
                #print("found A")
                print("Found ", pattern[0])
                i = eidx
                found_pattern_counter = found_pattern_counter + 1 
                for seidx in range(i,len(lines)):
                    if(all_events[seidx] == pattern[2]): #pattern broke - C after A
                        break
                    elif(all_events[seidx] == pattern[1]):
                        #print("found B")
                        print("Found ", pattern[1])
                        j = seidx
                        found_pattern_counter = found_pattern_counter + 1
                        for teidx in range(j+1,len(lines)):
                            if(all_events[teidx] == pattern[0]): #pattern broke - A after B instead of C -ABA
                                break
                            elif(all_events[teidx] == pattern[2]):
                                #print("found C")
                                print("Found ", pattern[2])
                                k = teidx
                                found_pattern_counter = found_pattern_counter + 1
                                cmp = calculate_match_prob(lines, num_of_attr, i, j, k)
                                if(cmp[0] == 0):
                                    print("Pattern probability is 0 - Not a Match")
                                else:    
                                    all_dfs = pd.concat([match_df, cmp[1]])
                                    match_df = all_dfs
                                    final_dfs = pd.concat([final_match_df, cmp[2]])
                                    final_match_df = final_dfs
                                    all_dfs_ba = pd.concat([match_df_ba, cmp[3]]) 
                                    match_df_ba = all_dfs_ba
                                    matches_counter = matches_counter + 1
                                found_pattern_counter = 0
                if (found_pattern_counter < 3):
                      found_pattern_counter = 0
    
    print("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
    print("Number of matches:", matches_counter)
    print("**********************************************************************************************************")
    print("Table of events showing the difference inside attributes after dynamic updates:")
    print(all_dfs)
    print("**********************************************************************************************************")
    print("Table of pattern matchs and their probabilities:")
    print(final_dfs)
    print("**********************************************************************************************************")
    print("Table of events showing the difference inside attributes after dynamic updates together:")
    print(all_dfs_ba)
	
#Export to Excel To compare results:
#all_dfs.to_excel(r'C:\Users\adizo\Documents\Technion\Semester 13\DataProcessingProject\CEP-correctness\ABCC_1.xlsx', sheet_name='Match Changes', index = True)
#final_dfs.to_excel(r'C:\Users\adizo\Documents\Technion\Semester 13\DataProcessingProject\CEP-correctness\ABCC_2.xlsx', sheet_name='Final Probs', index = False)
#all_dfs_ba.to_excel(r'C:\Users\adizo\Documents\Technion\Semester 13\DataProcessingProject\CEP-correctness\ABCC_3.xlsx', sheet_name='Match Changes', index = True)