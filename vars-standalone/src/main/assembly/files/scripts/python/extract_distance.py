#!/usr/bin/env python

import math
import os
import sys


def show_help():
    print("""
This is a script that parses the output of a VARS query and extracts the 
distance measurement values and writes them to a new file

Usage: 
    extract_distance.py <input_file> <output_file>

Arguments:
    input_file = The name of the file saved from the VARS query
    output_file = The new file to write the results to
        """)

def process_file(input_file, output_file):
    # -- setup
    line_number = 0
    columns = ['TapeTimeCode', 'RecordedDate', 'DiveNumber', 'RovName', 
        'ConceptName']
    ass_column = 'Associations'
    ass_idx = -1
    new_columns = ['dX', 'dY', "Distance", "Comments"]
    column_names = None

    # -- Build list to hold each row of output as a string
    rows = []

    # -- parse the file 
    for line in open(input_file):
        if (line.startswith('#')):
            continue
        parts = line.split('\t')
        #print(line)
        if (line_number == 0):
            # parse header
            column_names = parts
            ass_idx = column_names.index(ass_column)
        else:
            n = 0
            s = ""
            for c in columns:
                idx = column_names.index(c)
                val = parts[idx]
                if n == 0:
                    s = val
                else:
                    s = s + "\t" + val
                n += 1
            # TODO parse Associations
            ass = parts[ass_idx]
            ass_list = ass.split(',')
            #print(ass_list)
            for a in ass_list:
                a = a.strip()
                if (a.startswith('measurement in pixels')):
                    pixels = a.split('|')[2].strip().split(' ')
                    # print(pixels)
                    x0 = int(pixels[0])
                    y0 = int(pixels[1])
                    x1 = int(pixels[2])
                    y1 = int(pixels[3])
                    try:
                        cmt = pixels[4]
                    except:
                        cmt = ""
                    dx = abs(x0 - x1)
                    dy = abs(y0 - y1)
                    d = math.sqrt(dx * dx + dy * dy)
                    ss = s + "\t" + str(dx) + "\t" + str(dy) + "\t" + str(d) + "\t" + cmt
                    rows.append(ss)

        line_number += 1

    # -- write results
    with open(output_file, 'w') as f:
        columns.extend(new_columns)
        f.write('\t'.join(columns))
        f.write(os.linesep)
        for r in rows:
            f.write(r)
            f.write(os.linesep)


if (len(sys.argv) != 3):
    show_help()
else:
    input_file = sys.argv[1]
    output_file = sys.argv[2]
    process_file(input_file, output_file)




