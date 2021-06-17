import os
import csv
from _csv import reader
import numpy as np
import pandas as pd

list_dir_test = os.listdir("..\\datasets\\risultatiTest\\")

for dir in list_dir_test:
    with open("..\\datasets\\risultatiTest\\" + dir + "\\resultTest.csv") as path:
        test_smells_df = pd.read_csv(path, sep=";")

        if test_smells_df.shape[0] > 0:
            test_suite = pd.unique(test_smells_df['production'])
            smells_test = ["ar1", "et1", "it1", "gf1", "se1", "mg1", "ro1"]
            new_df = pd.DataFrame({'Test-suite': test_suite}).set_index('Test-suite')

            fNew = open('newSmell_file', 'w', newline='')
            writer = csv.writer(fNew)
            data = [['package', 'smell']]

            with open("..\\datasets\\risultatiTest\\" + dir + "\\resultTest.csv", 'r') as read_obj:
                csv_reader = reader(read_obj, delimiter=';')
                next(csv_reader, None)
                for row in csv_reader:
                    numIndex = row[4].rfind('.')
                    if float(row[9]) > 0:
                        newRow = [row[4][0: numIndex], 'ar1']
                        data.append(newRow)
                    if float(row[10]) > 0:
                        newRow = [row[4][0: numIndex], 'et1']
                        data.append(newRow)
                    if float(row[11]) > 0:
                        newRow = [row[4][0: numIndex], 'it1']
                        data.append(newRow)
                    if float(row[12]) > 0:
                        newRow = [row[4][0: numIndex], 'gf1']
                        data.append(newRow)
                    if float(row[13]) > 0:
                        newRow = [row[4][0: numIndex], 'se1']
                        data.append(newRow)
                    if float(row[14]) > 0:
                        newRow = [row[4][0: numIndex], 'mg1']
                        data.append(newRow)
                    if float(row[15]) > 0:
                        newRow = [row[4][0: numIndex], 'ro1']
                        data.append(newRow)

            with open("..\\datasets\\designite\\" + dir + "\\DesignSmells.csv") as path:
                design_smells_df = pd.read_csv(path)
                packagesDesign = pd.unique(design_smells_df[' Package Name'])
                smellsDesign = pd.unique(design_smells_df[' Design Smell'])

                with open("..\\datasets\\designite\\" + dir + "\\DesignSmells.csv", 'r') as read_obj:
                    csv_reader = reader(read_obj)
                    next(csv_reader, None)
                    for row in csv_reader:
                        newRow = [row[4], row[6]]
                        data.append(newRow)

                writer.writerows(data)
                fNew.close()

                path = "newSmell_file"
                smells_df = pd.read_csv(path)
                packages = pd.unique(smells_df['package'])
                smells = pd.unique(smells_df['smell'])
                new_df = pd.DataFrame({'Smells': smells},
                                      ).set_index('Smells')
                dimension = len(smellsDesign) + len(smells_test)

                vectors_all = []
                if design_smells_df.shape[0] > 0:
                    for package in packages:
                        vector = [[0 for x in range(dimension)] for y in range(dimension)]
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

                                    minimo = [len(riga1), len(riga2)]
                                    vector[i][j] = np.min(np.array(minimo))
                                    j = j + 1
                            else:
                                for smell2 in smells:
                                    vector[i][j] = 0
                                    j = j + 1
                            i = i + 1
                        vectors_all.append(vector)

                    print("Finito " + dir);

                    matrix_coocc = np.array(vectors_all)
                    sum_smell = matrix_coocc.sum(axis=0)
                    cooccurrence_matrix_diagonal = np.diagonal(sum_smell)

                    with np.errstate(divide='ignore', invalid='ignore'):
                        cooccurrence_matrix_percentage = np.nan_to_num(
                            np.true_divide(sum_smell, cooccurrence_matrix_diagonal[:, None])).round(
                            2)

                    cooccurrence_matrix_percentage = pd.DataFrame(cooccurrence_matrix_percentage)
                    cooccurrence_matrix_percentage = cooccurrence_matrix_percentage.iloc[7:19, 0:7]
                    cooccurrence_matrix_percentage = cooccurrence_matrix_percentage.rename(
                        columns={0: "ar1", 1: "et1", 2: "it1", 3: "gf1", 4: "se1", 5: "mg1", 6: "ro1"})

                    cooccurrence_matrix_percentage = cooccurrence_matrix_percentage.rename \
                        (index={7: 'Deficient Encapsulation', 8: 'Unutilized Abstraction', 9: 'Feature Envy',
                                10: 'Broken Hierarchy', 11: 'Broken Modularization', 12: 'Insufficient Modularization',
                                13: 'Wide Hierarchy', 14: 'Unnecessary Abstraction', 15: 'Multifaceted Abstraction',
                                16: 'Cyclically-dependent Modularization',
                                17: 'Cyclic Hierarchy', 18: 'Rebellious Hierarchy'})

                    cooccurrence_matrix_percentage.to_csv("risultatiDesTest\\" + dir + ".csv")
