#!/usr/bin/env python3
"""
SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR GPL-2.0-or-later"

from random import uniform, choice
def floatTuple(vector):
	rVal = []
	for e in vector:
		rVal.append(float(e))
	return rVal
def difference(a, b):
	rVal = 0.
	for i in range(len(a)):
		rVal += abs(a[i] - b[i])
	return rVal
def randomVector(length):
	rVal = []
	for i in range(length):
		rVal.append(
			uniform(-1000, 1000) # TODO Random chosen magic constants
		)
	return rVal
def randomZeroOneVector(length):
	rVal = []
	for i in range(length):
		rVal.append(choice([0,1]))
	return rVal
def randomZeroOnePositiveAndNegativeVector(length):
	rVal = []
	for i in range(length):
		rVal.append(choice([-1,0,1]))
	return rVal
def printImage(matrix, length):
	rows=int(len(matrix) / length)
	for row in range(rows):
		rowStr = ""
		for i in range(length):
			rowStr += " " + str(round(matrix[row * length + i], 2))
		print(rowStr)
	print("")
images3x3 = {
	"cross" : (1,0,1, 0,1,0, 1,0,1), # cross
	"vertical" : (0,1,0, 0,1,0, 0,1,0), # vertical
	"horizontal" : (0,0,0, 1,1,1, 0,0,0), # horizontal
	"zero" : (1,1,1, 1,0,1, 1,1,1), # zero
	"one" : (0,0,1, 0,1,1, 0,0,1), # one
	"v" : (1,0,1, 1,0,1, 0,1,0), # v
 	"seven" : (1,1,1, 0,1,0, 1,0,0), # seven
	"t" : (1,1,1, 0,1,0, 0,1,0), # t
	"h" : (1,0,1, 1,1,1, 1,0,1) # h
	}