import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

path = "C:\\Users\\Armando\\swdProjects\\progetto-swd\\scripts\\datasets\\designite\\androidannotations\\DesignSmells.csv"
desing_smells_df = pd.read_csv(path)
packages = pd.unique(desing_smells_df[' Package Name'])
smells = pd.unique(desing_smells_df[' Design Smell'])
new_df = pd.DataFrame({'Smells': smells},
                      ).set_index('Smells')
vectors_all = []

for package in packages:
    vector = [[0 for x in range(12)] for y in range(12)]
    i = 0
    for smell1 in smells:
        j = 0
        riga1 = desing_smells_df.loc[
            (desing_smells_df[" Design Smell"] == smell1) & (
                    desing_smells_df[" Package Name"] == package)]
        if len(riga1) > 0:
            for smell2 in smells:
                riga2 = desing_smells_df.loc[
                    (desing_smells_df[" Design Smell"] == smell2) & (
                            desing_smells_df[" Package Name"] == package)]
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
    riga = desing_smells_df.loc[
        (desing_smells_df[" Design Smell"] == smell)]
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