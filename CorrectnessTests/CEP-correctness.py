import pandas as pd
import re
import sys


def newdelta(x0, x1, y0, y1, z0, z1):
    if round(abs(float(x1 - x0)), 5) < round(abs(float(y1 - y0)), 5) < round(abs(float(z1 - z0)), 5):
        return 1
    return 0


def init_flags_array(pce, num_of_attr):

    lengths = []
    for i in range(3):
        start = i*num_of_attr
        end = i*num_of_attr+num_of_attr
        for j in range(start, end):
            lengths.append(len(pce[j]))

    participate_array = []
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


def create_dfs(events, serials, pce, updated_events, events_prob, pattern_events_prob, attr_prob, final_prob, i, j, k):
    # attr_before = []
    attr_after = []
    for p in range(0, len(pce) - 1, 2):
        # attr_before.append([pce[p], pce[p + 1]])
        attr_after.append([updated_events[p], updated_events[p + 1]])

    user_data = {'Name1': [str(events[0])], 'SeqNumber1': [str(i+1)], 'TimeStamp1': [str(serials[0])],
                 'Prob1': [str(events_prob[0])],
                 'Name2': [str(events[1])], 'SeqNumber2': [str(j + 1)], 'TimeStamp2': [str(serials[1])],
                 'Prob2': [str(events_prob[1])],
                 'Name3': [str(events[2])], 'SeqNumber3': [str(k + 1)], 'TimeStamp3': [str(serials[2])],
                 'Prob3': [str(events_prob[2])],
                 'Sequence Prob': [final_prob]}
    developer_data = {'Name1': [str(events[0])], 'SeqNumber1': [str(i+1)], 'TimeStamp1': [str(serials[0])],
                      'Prob1': [str(events_prob[0])], 'Attributes1': [str(attr_after[0])],
                      'Name2': [str(events[1])], 'SeqNumber2': [str(j + 1)], 'TimeStamp2': [str(serials[1])],
                      'Prob2': [str(events_prob[1])], 'Attributes2': [str(attr_after[1])],
                      'Name3': [str(events[2])], 'SeqNumber3': [str(k + 1)], 'TimeStamp3': [str(serials[2])],
                      'Prob3': [str(events_prob[2])], 'Attributes3': [str(attr_after[2])],
                      'Events Occurrences Prob': [pattern_events_prob], 'Attributes Prob': [attr_prob],
                      'Sequence Prob': [final_prob]}

    user_df = pd.DataFrame(user_data)
    developer_df = pd.DataFrame(developer_data)
    return user_df, developer_df


def lessThenD(l1, l2, el, sl, pce, epl, num_of_attr, i, j, k, time_window):

    pattern_events_prob = float(epl[i])*float(epl[j])*float(epl[k])
    events = [el[i], el[j], el[k]]
    serials = [sl[i], sl[j], sl[k]]
    serials_num = [int(sl[i]), int(sl[j]), int(sl[k])]
    events_prob = [epl[i], epl[j], epl[k]]
    original_pce = pce
    prob = 0.0
    if ((serials_num[1] - serials_num[0] > time_window) or (serials_num[2] - serials_num[1] > time_window)
            or (serials_num[2] - serials_num[0] > time_window)):  # Expired
        init_user_data = {'Name1': [], 'SeqNumber1': [], 'TimeStamp1': [], 'Prob1': [], 'Name2': [], 'SeqNumber2': [],
                          'TimeStamp2': [], 'Prob2': [], 'Name3': [], 'SeqNumber3': [], 'TimeStamp3': [], 'Prob3': [],
                          'Sequence Prob': []}
        match_user_df = pd.DataFrame(init_user_data)

        init_dev_data = {'Name1': [], 'SeqNumber1': [], 'TimeStamp1': [], 'Prob1': [], 'Attributes1': [],
                          'Name2': [], 'SeqNumber2': [], 'TimeStamp2': [], 'Prob2': [], 'Attributes2': [],
                          'Name3': [], 'SeqNumber3': [], 'TimeStamp3': [], 'Prob3': [], 'Attributes3': [],
                          'Events Occurrences Prob': [], 'Attributes Prob': [], 'Sequence Prob': []}
        match_dev_df = pd.DataFrame(init_dev_data)

        return 0, match_user_df, match_dev_df

    all_inner_matches = []
    all_events = []
    all_serials = []
    all_inner_probs = []
    participate_array = init_flags_array(pce, num_of_attr)

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
                                    all_inner_probs.append(round(inner_prob, 4))
                                    flags = flag_events_array(original_pce, inner_match, num_of_attr, participate_array)
                                    participate_array = flags
                                prob = prob + inner_prob

    final_prob = prob * pattern_events_prob
    #dynamic_updates = list(zip(all_inner_probs, all_events, all_serials, all_inner_matches))
    updated_events = update_events_array(pce, participate_array, num_of_attr)
    dfs = create_dfs(events, serials, pce, updated_events, events_prob, pattern_events_prob, prob, final_prob, i, j, k)
    match_user_df = pd.DataFrame(dfs[0])
    match_developer_df = pd.DataFrame(dfs[1])

    return final_prob, match_user_df, match_developer_df


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

    return all_atr1, all_atr2, event_list, serials_list, event_prob_list


def calculate_match_prob(lines, num_of_attr, i, j, k, time_window):
    inner = innerMatches(lines)
    all_atr1 = inner[0]
    all_atr2 = inner[1]
    event_list = inner[2]
    serials_list = inner[3]
    epl = inner[4]
    pattern_curr_events = []
    pattern_curr_events = create_pce(lines, i, j, k)

    x = lessThenD(all_atr1, all_atr2, event_list, serials_list, pattern_curr_events, epl, num_of_attr, i, j, k, time_window)
    return x


def export_csv(all_user_dfs, all_dev_dfs):
    col_names_user_1 = ['Name1', 'SeqNumber1', 'TimeStamp1', 'Prob1']
    col_names_user_2 = ['Name2', 'SeqNumber2', 'TimeStamp2', 'Prob2']
    col_names_user_3 = ['Name3', 'SeqNumber3', 'TimeStamp3', 'Prob3']

    col_names_dev_1 = ['Name1', 'SeqNumber1', 'TimeStamp1', 'Prob1', 'Attributes1']
    col_names_dev_2 = ['Name2', 'SeqNumber2', 'TimeStamp2', 'Prob2', 'Attributes2']
    col_names_dev_3 = ['Name3', 'SeqNumber3', 'TimeStamp3', 'Prob3', 'Attributes3']

    all_user_dfs['result_u1'] = all_user_dfs[col_names_user_1].agg('  '.join, axis=1)
    all_user_dfs['result_u2'] = all_user_dfs[col_names_user_2].agg('  '.join, axis=1)
    all_user_dfs['result_u3'] = all_user_dfs[col_names_user_3].agg('  '.join, axis=1)
    all_user_dfs['result_u'] = all_user_dfs[['result_u1', 'result_u2', 'result_u3']].agg(', '.join, axis=1)
    all_user_dfs['SEQ [Name  sequenceNumber  Timestamp  Prob,...]'] = '[' + all_user_dfs['result_u'] + ']'

    all_dev_dfs['result_d1'] = all_dev_dfs[col_names_dev_1].agg('  '.join, axis=1)
    all_dev_dfs['result_d2'] = all_dev_dfs[col_names_dev_2].agg('  '.join, axis=1)
    all_dev_dfs['result_d3'] = all_dev_dfs[col_names_dev_3].agg('  '.join, axis=1)
    all_dev_dfs['result_d'] = all_dev_dfs[['result_d1', 'result_d2', 'result_d3']].agg(', '.join, axis=1)
    all_dev_dfs['SEQ [Name  sequenceNumber  Timestamp  Prob  Attributes,...]'] = '[' + all_dev_dfs['result_d'] + ']'

    # Export to csv To compare results:
    all_user_dfs[['SEQ [Name  sequenceNumber  Timestamp  Prob,...]', 'Sequence Prob']].to_csv('tests/python_outputs/output_'+file_name+'_user.csv', index=False)
    all_dev_dfs[['SEQ [Name  sequenceNumber  Timestamp  Prob  Attributes,...]',
                 'Events Occurrences Prob', 'Attributes Prob', 'Sequence Prob']].to_csv('tests/python_outputs/output_'+file_name+'_developer.csv', index=False)


if __name__ == "__main__":
    file_name = sys.argv[1]
    pattern = ['MSFT', 'CSCO', 'QTWW']
    print("Pattern: ", pattern)
    num_of_attr = 2
    time_window = 10
    filepath = "tests/"+'input_'+file_name+".txt"
    with open(filepath, 'r') as f:  # load file
        lines = f.read().splitlines()  # read lines
        found_pattern_counter = 0
        matches_counter = 0
        i = 0
        j = 0
        k = 0
        all_events = []

        all_user_dfs = []
        init_u_data = {'Name1': [], 'SeqNumber1': [], 'TimeStamp1': [], 'Prob1': [], 'Name2': [], 'SeqNumber2': [],
                       'TimeStamp2': [], 'Prob2': [], 'Name3': [], 'SeqNumber3': [], 'TimeStamp3': [], 'Prob3': [],
                       'Sequence Prob': []}
        match_u_df = pd.DataFrame(init_u_data)
        all_user_dfs = match_u_df

        all_dev_dfs = []
        init_dev_data = {'Name1': [], 'SeqNumber1': [], 'TimeStamp1': [], 'Prob1': [], 'Attributes1': [],
                          'Name2': [], 'SeqNumber2': [], 'TimeStamp2': [], 'Prob2': [], 'Attributes2': [],
                          'Name3': [], 'SeqNumber3': [], 'TimeStamp3': [], 'Prob3': [], 'Attributes3': [],
                          'Events Occurrences Prob': [], 'Attributes Prob': [], 'Sequence Prob': []}
        match_dev_df = pd.DataFrame(init_dev_data)
        all_dfs_ba = match_dev_df

        for line in lines:
            cols = line.split(',')
            all_events.append(cols[0])

        for eidx in range(len(lines)):
            if(all_events[eidx] == pattern[0]):
                #print("found A")
                #print("Found ", pattern[0])
                i = eidx
                found_pattern_counter = found_pattern_counter + 1 
                for seidx in range(i, len(lines)):
                    if(all_events[seidx] == pattern[1]):
                        #print("found B")
                        #print("Found ", pattern[1])
                        j = seidx
                        found_pattern_counter = found_pattern_counter + 1
                        for teidx in range(j+1, len(lines)):
                            if(all_events[teidx] == pattern[2]):
                                #print("found C")
                                #print("Found ", pattern[2])
                                k = teidx
                                found_pattern_counter = found_pattern_counter + 1
                                cmp = calculate_match_prob(lines, num_of_attr, i, j, k, time_window)
                                if(cmp[0] != 0):
                                    all_user_dfs = pd.concat([match_u_df, cmp[1]])
                                    match_u_df = all_user_dfs
                                    all_dev_dfs = pd.concat([match_dev_df, cmp[2]])
                                    match_dev_df = all_dev_dfs
                                    matches_counter = matches_counter + 1
                                found_pattern_counter = 0
                if (found_pattern_counter < 3):
                      found_pattern_counter = 0

print("Number of matches: ", matches_counter)
export_csv(all_user_dfs, all_dev_dfs)
