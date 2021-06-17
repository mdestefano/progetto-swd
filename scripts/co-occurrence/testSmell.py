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
            test_suite = pd.unique(test_smells_df['testsuite'])
            smells = ["ar1", "et1", "it1", "gf1", "se1", "mg1", "ro1"]
            new_df = pd.DataFrame({'Test-suite': test_suite}).set_index('Test-suite')

            fNew = open('newTestSmell_file', 'w', newline='')
            writer = csv.writer(fNew)
            data = [['testsuite', 'testsmell']]

            with open("..\\datasets\\risultatiTest\\" + dir + "\\resultTest.csv", 'r') as read_obj:
                csv_reader = reader(read_obj, delimiter=';')
                next(csv_reader, None)
                for row in csv_reader:
                    if float(row[9]) > 0:
                        newRow = [row[3], "ar1"]
                        data.append(newRow)
                    # else:
                    #     smells = [item for item in smells if item != "ar1"]

                    if float(row[10]) > 0:
                        newRow = [row[3], "et1"]
                        data.append(newRow)
                    # else:
                    #     smells = [item for item in smells if item != "et1"]

                    if float(row[11]) > 0:
                        newRow = [row[3], "it1"]
                        data.append(newRow)
                    # else:
                    #     smells = [item for item in smells if item != "it1"]

                    if float(row[12]) > 0:
                        newRow = [row[3], "gf1"]
                        data.append(newRow)
                    # else:
                    #     smells = [item for item in smells if item != "gf1"]

                    if float(row[13]) > 0:
                        newRow = [row[3], "se1"]
                        data.append(newRow)
                    # else:
                    #     smells = [item for item in smells if item != "se1"]

                    if float(row[14]) > 0:
                        newRow = [row[3], "mg1"]
                        data.append(newRow)
                    # else:
                    #     smells = [item for item in smells if item != "mg1"]

                    if float(row[15]) > 0:
                        newRow = [row[3], "ro1"]
                        data.append(newRow)
                    # else:
                    #     smells = [item for item in smells if item != "ro1"]

            writer.writerows(data)
            fNew.close()

            path = "newTestSmell_file"
            test_smells_df = pd.read_csv(path)
            packages = pd.unique(test_smells_df['testsuite'])
            smells = pd.unique(test_smells_df['testsmell'])
            new_df = pd.DataFrame({'Smells': smells},
                                  ).set_index('Smells')
            vectors_all = []

            if len(smells) > 0:
                for package in packages:
                    vector = [[0 for x in range(len(smells))] for y in range(len(smells))]
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
                                prova = [len(riga1), len(riga2)]
                                vector[i][j] = np.min(np.array(prova))
                                j = j + 1
                        else:
                            for smell2 in smells:
                                vector[i][j] = 0
                                j = j + 1
                        i = i + 1
                    vectors_all.append(vector)

                print("Finito con " + dir)

                matrix_coocc = np.array(vectors_all)
                sum_smell = matrix_coocc.sum(axis=0)

                cooccurrence_matrix_diagonal = np.diagonal(sum_smell)
                print(cooccurrence_matrix_diagonal.shape)
                with np.errstate(divide='ignore', invalid='ignore'):
                    cooccurrence_matrix_percentage = np.nan_to_num(
                        np.true_divide(sum_smell, cooccurrence_matrix_diagonal[:, None])).round(
                        2)

                cooccurrence_matrix_percentage = pd.DataFrame(data=cooccurrence_matrix_percentage,
                                                              columns=smells).set_index(smells)
                cooccurrence_matrix_percentage.to_csv("risultatiTest\\" + dir + ".csv")
