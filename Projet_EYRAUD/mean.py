# -*- coding: utf-8 -*-
"""
Created on Fri Nov  1 23:15:21 2019

@author: Matthieu
"""

f = open("reslength.csv", "r")
output = open("reslengthmean.csv", "w")

means = []
for line in f:
    l_s = line.split(" ")
    means.append([int(l_s[0]), int(l_s[2]), int(l_s[4])])

means.sort(key=lambda r:r[0])
print(means)

interval = 100
means_res = []
i=0
j=0
for v in range(500, 1201, interval):
    if i >= len(means):
        break
    cpt=0
    print("v vaut " + str(v))
    means_res.append([v, 0 , 0])
    while i< len(means) and means[i][0] > v - interval/2 and means[i][0] <= v + interval/2:
        means_res[j][1] += means[i][1]
        means_res[j][2] += means[i][2]
        cpt+=1
        i+=1
    means_res[j][1] = means_res[j][1]/cpt
    means_res[j][2] = means_res[j][2]/cpt
    j+=1

print(means_res)
for (l,r1,r2) in means_res:
    output.write(str(l) + " " + str(r1) + " " + str(r2) + "\n")
    
f.close()
output.close()