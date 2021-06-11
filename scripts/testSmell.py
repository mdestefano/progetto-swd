import csv
from _csv import reader
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

path = "C:\\Users\\Armando\\swdProjects\\progetto-swd\\scripts\\datasets\\risultatiTest\\androidannotations\\resultTest.csv"
test_smells_df = pd.read_csv(path, sep=";")
test_suite = pd.unique(test_smells_df['testsuite'])
smells = ["ar1", "et1", "it1", "gf1", "se1", "mg1", "ro1"]
new_df = pd.DataFrame({'Test-suite': test_suite}).set_index('Test-suite')
print(new_df)

fNew = open('newTestSmell_file', 'w', newline='')
writer = csv.writer(fNew)
data = [['testsuite', 'testsmell']]


with open(path, 'r') as read_obj:
    csv_reader = reader(read_obj, delimiter=';')
    next(csv_reader, None)
    for row in csv_reader:
        if float(row[9]) > 0:
            newRow = [row[3], 'ar1']
            data.append(newRow)
        if float(row[10]) > 0:
            newRow = [row[3], 'et1']
            data.append(newRow)
        if float(row[11]) > 0:
            newRow = [row[3], 'it1']
            data.append(newRow)
        if float(row[12]) > 0:
            newRow = [row[3], 'gf1']
            data.append(newRow)
        if float(row[13]) > 0:
            newRow = [row[3], 'se1']
            data.append(newRow)
        if float(row[14]) > 0:
            newRow = [row[3], 'mg1']
            data.append(newRow)
        if float(row[15]) > 0:
            newRow = [row[3], 'ro1']
            data.append(newRow)

writer.writerows(data)
fNew.close()


#da qui calcolo le coppie di co-occorrenze

path = "newTestSmell_file"
test_smells_df = pd.read_csv(path)
packages = pd.unique(test_smells_df['testsuite'])
smells = pd.unique(test_smells_df['testsmell'])
new_df = pd.DataFrame({'Smells': smells},
                      ).set_index('Smells')
vectors_all = []

for package in packages:
    vector = [[0 for x in range(7)] for y in range(7)]
    i = 0
    for smell1 in smells:
        j = 0
        riga1 = test_smells_df.loc[
            (test_smells_df["testsmell"] == smell1) & (
                    test_smells_df["testsuite"] == package)]
        if len(riga1) > 0:
            for smell2 in smells:
                riga2 = test_smells_df.loc[
                    (test_smells_df["testsmell"] == smell2) & (
                            test_smells_df["testsuite"] == package)]
                prova = [len(riga1),len(riga2)]
                vector[i][j] = np.min(np.array(prova))
                j = j + 1
        else:
            for smell2 in smells:
                vector[i][j] = 0
                j = j + 1
        i = i + 1
    vectors_all.append(vector)
print(vectors_all)
vector = []
for smell in smells:
    riga = test_smells_df.loc[
        (test_smells_df["testsmell"] == smell)]
    vector.append(len(riga))
print(vector)
matrix_coocc = np.array(vectors_all)
sum_smell = matrix_coocc.sum(axis=0)
# sum_smell = pd.DataFrame(sum_smell, columns=smells).set_index(smells)
print(pd.DataFrame(sum_smell, columns=smells).set_index(smells))
pd.DataFrame(sum_smell, columns=smells).set_index(smells).to_csv("coocc.csv")
index_rows = range(5)
index_columns = range(5)
cooccurrence_matrix_diagonal = np.diagonal(sum_smell)
print(cooccurrence_matrix_diagonal)
for row in index_rows:
    for column in index_columns:
        if sum_smell[row][column] > vector[row]:
            print("Devo dividere " + str(sum_smell[row][column]) + " per " + str(vector[row]))
cooccurrence_matrix_diagonal = np.diagonal(sum_smell)
print(cooccurrence_matrix_diagonal)
with np.errstate(divide='ignore', invalid='ignore'):
    cooccurrence_matrix_percentage = np.nan_to_num(np.true_divide(sum_smell, cooccurrence_matrix_diagonal[:, None])).round(
        2)
print('\ncooccurrence_matrix_percentage:\n{0}'.format(cooccurrence_matrix_percentage))
# creating random data
data = np.array(sum_smell)
# creating array of text
text = np.array(cooccurrence_matrix_percentage)
# creating subplot
fig, ax = plt.subplots()
# drawing heatmap on current axes
ax = sns.heatmap(data, annot=text, fmt="")
plt.show()