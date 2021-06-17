import os

import numpy as np
import pandas as pd

list_dir_designite = os.listdir("..\\datasets\\designite\\")

for dir in list_dir_designite:
    with open("..\\datasets\\designite\\" + dir + "\\ArchitectureSmells.csv") as path:
        arch_smells_df = pd.read_csv(path)

        if arch_smells_df.shape[0] > 0:
            packages = pd.unique(arch_smells_df[' Package Name'])
            smells = pd.unique(arch_smells_df[' Architecture Smell'])

            new_df = pd.DataFrame({'Smells': smells}, ).set_index('Smells')
            vector = []
            vectors_all = []
            for package in packages:
                vector = [[0 for x in range(len(smells))] for y in range(len(smells))]
                i = 0
                for smell1 in smells:
                    j = 0

                    riga1 = arch_smells_df.loc[
                        (arch_smells_df[" Architecture Smell"] == smell1) & (
                                arch_smells_df[" Package Name"] == package)]

                    if len(riga1) > 0:
                        for smell2 in smells:
                            riga2 = arch_smells_df.loc[
                                (arch_smells_df[" Architecture Smell"] == smell2) & (
                                        arch_smells_df[" Package Name"] == package)]

                            righe = [len(riga1), len(riga2)]
                            vector[i][j] = np.min(np.array(righe))

                            j = j + 1
                    else:
                        for smell2 in smells:
                            vector[i][j] = 0
                            j = j + 1
                    i = i + 1
                    vectors_all.append(vector)

            print("Finito progetto " + dir + " creo dataframe co-occorrenze")

            matrix_coocc = np.array(vectors_all)
            sum_smell = matrix_coocc.sum(axis=0)

            cooccurrence_matrix_diagonal = np.diagonal(sum_smell)

            with np.errstate(divide='ignore', invalid='ignore'):
                cooccurrence_matrix_percentage = np.nan_to_num(
                    np.true_divide(sum_smell, cooccurrence_matrix_diagonal[:, None])).round(
                    2)

            cooccurrence_matrix_percentage = pd.DataFrame(data=cooccurrence_matrix_percentage,
                                                          columns=smells).set_index(smells)
            cooccurrence_matrix_percentage.to_csv("risultatiArch\\"+dir+".csv")
