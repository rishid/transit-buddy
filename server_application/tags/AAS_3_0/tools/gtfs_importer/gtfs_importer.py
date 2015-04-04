#!/usr/bin/env python

"""
gtfs_importer

Tool to import GTFS CSV data files into a MySQL
database.
"""

import os
import csv
import sys
import optparse

class SpecialHandler(object):
  """
  A SpecialHandler does a little extra special work for a particular
  database table.
  """
  def handleCols(self,columns):
    return columns

  def handleVals(self,row,header):
    return row


class StopTimesHandler(SpecialHandler):
  @staticmethod
  def timeToSeconds(text):
    h,m,s = map(int,text.split(":"))
    return h*60*60 + m*60 + s
  @staticmethod
  def secsToTime(s):
    h,m = divmod(s,60*60)
    m,s = divmod(m,60)
    return "%2.2d:%2.2d:%2.2d" % (h,m,s)

  def handleCols(self,cols):
    return cols+['arrival_time_seconds','departure_time_seconds']

  def handleVals(self,row,cols):
    arrIdx = cols.index('arrival_time')
    depIdx = cols.index('departure_time')

    arr_secs = self.timeToSeconds(row[arrIdx]);
    dep_secs = self.timeToSeconds(row[depIdx]);

    row[arrIdx] = self.secsToTime(arr_secs);
    row[depIdx] = self.secsToTime(dep_secs);

    return row+[str(arr_secs), str(dep_secs)]

def import_file(fname, tablename, handler):
  """Returns SQL statement iterator"""
  try:
    f = open(fname,'r');
  except:
    yield "-- file %s doesn't exist" % fname
    return

  if not handler:
    handler = SpecialHandler()

  reader = csv.reader(f, dialect=csv.excel);
  header = handler.handleCols(reader.next());
  cols = ",".join(header);

  defaultVal = 'NULL';

  delim = ","
  insertSQL = "INSERT INTO " + tablename + " (" + cols + ") VALUES (%s);"
  func = lambda v: ((v and ("'"+v.replace("'","''")+"'")) or defaultVal)

  for row in reader:    
    if (len(cols.split(',')) == len(row)):
      vals = handler.handleVals(row,header);
      yield insertSQL % delim.join(map(func,vals))
    
def main():

  gtfs_file_names = ["agency", "calendar", "calendar_dates", "routes", "stop_times", "stops", "trips"];
  
  #gtfs_file_names = [ "agency" ];

  if len(sys.argv) != 2:
    print "Usage: %s gtfs_data_dir" % sys.argv[0]
    sys.exit(1)
  dirname = sys.argv[1]  
  handlers = dict.fromkeys(gtfs_file_names);
  #handlers['stop_times'] = StopTimesHandler();

  print "begin;"

  # Load GTFS files in loop  
  for fname in gtfs_file_names:
    for statement in import_file(dirname + "/" + fname + ".txt", fname, handlers[fname]):
      print statement;

  print "commit;"  
  
if __name__ == "__main__":
    main()

