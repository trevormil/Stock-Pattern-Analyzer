'''
Created on Feb 27, 2021

@author: Trevor
'''

from __future__ import absolute_import, division, print_function, unicode_literals

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from six.moves import urllib

import tensorflow.compat.v2.feature_column as fc
import silence_tensorflow.auto
import os
import tensorflow as tf


# only use first 26 columns
colnums = list(range(0, 25));
dftrain = pd.read_excel('training_data.xlsx', usecols=colnums[0:]) # training data
dfeval = pd.read_excel('evaluation_data.xlsx', usecols=colnums[0:]) # testing data

# get rid of those with blank cells
dftrain = dftrain[dftrain['Sector'].notnull()]
dfeval = dfeval[dfeval['Sector'].notnull()]
dftrain = dftrain[dftrain['Float'].notnull()]
dfeval = dfeval[dfeval['Float'].notnull()]
dftrain = dftrain[dftrain['Market Cap '].notnull()]
dfeval = dfeval[dfeval['Market Cap '].notnull()]

#Set labels
y_train = dftrain.pop('Volume')
y_eval = dfeval.pop('Volume')

#set columns
CATEGORICAL_COLUMNS = ['Index', 'Sector']
NUMERIC_COLUMNS = ['Market Cap ', 'Float', 'Prev Close ', 'Gap % ', 'Open', 'Pre Market High', 'Pre Market Low', 'Pre Market Vol']

#add prediction inputs here
prediction_input = {                
    'Index': ["XNAS", "XNAS", "XNYS"], 
      'Sector': ["Software & IT Services", 
                 "Biotechnology & Medical Research", "Software & IT Services"],
      'Market Cap ': [95, 100, 5],
      'Float': [13.49, 66.35, 12],
      'Prev Close ': [2.45, 3.96, 10.2],
      'Gap % ' : [0.44, 0.15, 0.1],
      'Open': [2.8, 3.5, 10.4],
      'Pre Market High': [3.5, 2.9, 10.8],
      'Pre Market Low': [3.5, 2.9, 10.0],
      'Pre Market Vol': [2075846, 87800, 15000234],
 }
    
feature_columns = []
for feature_name in CATEGORICAL_COLUMNS:
    vocabulary = dftrain[feature_name].unique()  # gets a list of all unique values from given feature column
    feature_columns.append(tf.feature_column.categorical_column_with_vocabulary_list(feature_name, vocabulary))

for feature_name in NUMERIC_COLUMNS:
    feature_columns.append(tf.feature_column.numeric_column(feature_name, dtype=tf.float32))


def make_input_fn(data_df, label_df, num_epochs=10, shuffle=True, batch_size=32):
    def input_function():  # inner function, this will be returned
        ds = tf.data.Dataset.from_tensor_slices((dict(data_df), label_df))  # create tf.data.Dataset object with data and its label
        if shuffle:
            ds = ds.shuffle(1000)  # randomize order of data
        ds = ds.batch(batch_size).repeat(num_epochs)  # split dataset into batches of 32 and repeat process for number of epochs
        return ds  # return a batch of the dataset
    return input_function  # return a function object for use

train_input_fn = make_input_fn(dftrain, y_train)  # here we will call the input_function that was returned to us to get a dataset object we can feed to the model
eval_input_fn = make_input_fn(dfeval, y_eval, num_epochs=1, shuffle=False)

linear_est = tf.estimator.LinearRegressor(feature_columns=feature_columns)

linear_est.train(train_input_fn)  # train

result = linear_est.evaluate(eval_input_fn)  # get model metrics/stats by testing on testing data

# print(result)  # the result variable is simply a dict of stats about our model

def test_input_fn():    
    dataset = tf.data.Dataset.from_tensors(prediction_input)    
    return dataset

pred_results = linear_est.predict(test_input_fn)
os.system('cls')

for x in range(100):
    print("\n")
idx = 0
for pred in enumerate(pred_results):
    print("Volume prediction for the following stock:", pred[1]['predictions'][0])
    for item in prediction_input:
        print("\t", item, ':', prediction_input[item][idx])
    idx = idx + 1;
    
plt.show()