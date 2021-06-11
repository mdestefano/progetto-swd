import csv
from _csv import reader

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

#qui creo il nuovo file per i test

path = "C:\\Users\\Armando\\swdProjects\\progetto-swd\\scripts\\datasets\\risultatiTest\\androidannotations\\resultTest.csv"
test_smells_df = pd.read_csv(path, sep=";")
test_suite = pd.unique(test_smells_df['testsuite'])
smellTest = ["ar1", "et1", "it1", "gf1", "se1", "mg1", "ro1"]

fNew = open('newSmell_file', 'w', newline='')
writer = csv.writer(fNew)
data = [['package', 'smell']]


with open(path, 'r') as read_obj:
    csv_reader = reader(read_obj, delimiter=';')
    next(csv_reader, None)
    for row in csv_reader:
        numIndex = row[3].rfind('.')
        if float(row[9]) > 0:
            newRow = [row[3][0: numIndex], 'ar1']
            data.append(newRow)
        if float(row[10]) > 0:
            newRow = [row[3][0: numIndex], 'et1']
            data.append(newRow)
        if float(row[11]) > 0:
            newRow = [row[3][0: numIndex], 'it1']
            data.append(newRow)
        if float(row[12]) > 0:
            newRow = [row[3][0: numIndex], 'gf1']
            data.append(newRow)
        if float(row[13]) > 0:
            newRow = [row[3][0: numIndex], 'se1']
            data.append(newRow)
        if float(row[14]) > 0:
            newRow = [row[3][0: numIndex], 'mg1']
            data.append(newRow)
        if float(row[15]) > 0:
            newRow = [row[3][0: numIndex], 'ro1']
            data.append(newRow)


#da qui leggo per il file di design smell
path = "C:\\Users\\Armando\\swdProjects\\progetto-swd\\scripts\\datasets\\designite\\androidannotations\\DesignSmells.csv"
desing_smells_df = pd.read_csv(path)
packagesDesign = pd.unique(desing_smells_df[' Package Name'])
smellsDesign = pd.unique(desing_smells_df[' Design Smell'])

with open(path, 'r') as read_obj:
    csv_reader = reader(read_obj)
    next(csv_reader, None)
    for row in csv_reader:
        newRow = [row[4], row[6]]
        data.append(newRow)

writer.writerows(data)
fNew.close()


#da qui calcolo le coppie di co-occorrenze tra design e test smell


path = "newSmell_file"
smells_df = pd.read_csv(path)
packages = pd.unique(smells_df['package'])
smells = pd.unique(smells_df['smell'])
new_df = pd.DataFrame({'Smells': smells},
                      ).set_index('Smells')
vectors_all = []

for package in packages:
    vector = [[0 for x in range(19)] for y in range(19)]
    i = 0
    for smell1 in smells:
        j = 0
        riga1 = smells_df.loc[
            (smells_df["smell"] == smell1) & (
                    smells_df["package"] == package)]
        if len(riga1) > 0:
            for smell2 in smells:
                riga2 = smells_df.loc[
                    (smells_df["smell"] == smell2) & (
                            smells_df["package"] == package)]
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
    riga = smells_df.loc[
        (smells_df["smell"] == smell)]
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

dfCoocc = pd.DataFrame(cooccurrence_matrix_percentage)
print("stampo cooc")
dfCoocc = dfCoocc.iloc[7:19,0:7]
print(dfCoocc.shape)
dfCoocc.shape

# creating random data
sumSmellMod = pd.DataFrame(sum_smell)
sumSmellMod = sumSmellMod.iloc[7:19,0:7]
data = np.array(sumSmellMod)
# creating array of text
text = np.array(dfCoocc)
# creating subplot
fig, ax = plt.subplots()
# drawing heatmap on current axes
ax = sns.heatmap(data, annot=text, fmt="")


bar = ax.collections[0].colorbar
r = bar.vmax - bar.vmin
bar.set_ticks([bar.vmin + 0.5 * r / (21) + r * i / (21) for i in range(21)]) #21 perchè sono 12 + 7 + 2 (design smell + test smell + 2 titoli nella legenda)

i = 0
arraySmellName = []

arraySmellName.append("-Design smell")
for smell in smellsDesign:
    arraySmellName.append(str(i) + ") " + smell)
    i = i + 1
i = 0
arraySmellName.append("-Test smell")
for smell in smellTest:
    arraySmellName.append(str(i) + ") " + smell)
    i = i + 1

bar.set_ticklabels(list(reversed(arraySmellName)))

ax.set_ylabel('DESIGN SMELL')
ax.set_xlabel('TEST SMELL')

plt.show()