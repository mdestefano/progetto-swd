library(plotly)
library(readr)
library(lmtest)
library(dynlm)

#cambiare solo la variabile myPath per lanciare
myPath <- "C:\\Users\\Grazia\\Desktop\\SD\\progetto-swd\\scripts\\datasets\\"
fileArch <- paste(myPath, "designite\\mpandroidchart\\ArchitectureSmells.csv", sep = "")
fileTest <- paste(myPath, "risultatiTest\\mpandroidchart\\resultTest.csv", sep = "")

data_designite <- read.csv(fileArch) 
data_test <- read.csv(fileTest, sep = ";")

versioni_uniq <- unique(data_designite$NameTag)
print(length(versioni_uniq))

dataFrameVers <- c()
dataFrameSmell <- c()

nRowTest <- nrow(data_test)

for(i in 1:nRowTest){
  numSmell <- data_test[i, 10]
  if(numSmell > 0){
    dataFrameVers <- c(dataFrameVers, data_test[i, 1])
    dataFrameSmell <- c(dataFrameSmell, "ar1")
  }
  numSmell <- data_test[i, 11]
  if(numSmell > 0){
    dataFrameVers <- c(dataFrameVers, data_test[i, 1])
    dataFrameSmell <- c(dataFrameSmell, "et1")
  }
  numSmell <- data_test[i, 12]
  if(numSmell > 0){
    dataFrameVers <- c(dataFrameVers, data_test[i, 1])
    dataFrameSmell <- c(dataFrameSmell, "it1")
  }
  numSmell <- data_test[i, 13]
  if(numSmell > 0){
    dataFrameVers <- c(dataFrameVers, data_test[i, 1])
    dataFrameSmell <- c(dataFrameSmell, "gf1")
  }
  numSmell <- data_test[i, 14]
  if(numSmell > 0){
    dataFrameVers <- c(dataFrameVers, data_test[i, 1])
    dataFrameSmell <- c(dataFrameSmell, "se1")
  }
  numSmell <- data_test[i, 15]
  if(numSmell > 0){
    dataFrameVers <- c(dataFrameVers, data_test[i, 1])
    dataFrameSmell <- c(dataFrameSmell, "mg1")
  }
  numSmell <- data_test[i, 16]
  if(numSmell > 0){
    dataFrameVers <- c(dataFrameVers, data_test[i, 1])
    dataFrameSmell <- c(dataFrameSmell, "ro1")
  }
  
}

dfTestSmell <- data.frame(dataFrameVers, dataFrameSmell)


versioni_designite <- data_designite$NameTag

nRowsDesignite <- nrow(data_designite)


architecture_smells <- unique(data_designite$Architecture.Smell)
test_smells <- unique(dfTestSmell$dataFrameSmell)

# for (arch_smell in architecture_smells){
#   listaSmell <- c()
#   print(arch_smell)
#   for(versione in versioni_uniq){
#     rows_filtered <- filter(data_designite, 
#                             Architecture.Smell == arch_smell 
#                             & NameTag == versione)
#     listaSmell <- c(listaSmell, nrow(rows_filtered))
#   }
#   print(listaSmell)
# }
arch <- c()
test <- c()
p_value <- c()

for (arch_smell in architecture_smells){
  listaSmell <- c()
  print(arch_smell)
  for(test_smell in test_smells){
    listaSmell <- c()
    listaSmellTest <- c()
    print(test_smell)
    for(versione in versioni_uniq){
      rows_filtered <- filter(data_designite,
                              Architecture.Smell == arch_smell
                              & NameTag == versione)
                                  listaSmell <- c(listaSmell, nrow(rows_filtered))

        rows_filtered_test <- filter(dfTestSmell,
                                     dataFrameSmell == test_smell
                                & dataFrameVers == versione)
        
        listaSmellTest <- c(listaSmellTest, nrow(rows_filtered_test))

    }
    listaSmell <- c(listaSmell, nrow(rows_filtered))
    listaSmell <- head(listaSmell, -1)  
    
    listaSmell=ts(listaSmell)
    listaSmellTest=ts(listaSmellTest)
    
    # This will run even when the data is multicolinear (but also when it is not)
    # and is functionally the same as running the granger test (which by default uses the waldtest
    # https://stackoverflow.com/questions/65712297/allowing-for-aliased-coefficients-when-running-grangertest-in-r
    m1 = dynlm(listaSmell ~ L(listaSmell, 1:1) + L(listaSmellTest, 1:1))
    m2 = dynlm(listaSmell ~ L(listaSmell, 1:1))
    result <-anova(m1, m2, test="F")


    value <- result$`Pr(>F)`
    print(value)
    arch <- c(arch, arch_smell)
    test <- c(test, test_smell)
    p_value <- c(p_value, result[2,6])
    df <- data.frame(arch, test, p_value)
    #grangertest(listaSmell ~ listaSmellTest, order = 3)
  }
}

write.csv(df,"C:\\Users\\Grazia\\Desktop\\SD\\progetto-swd\\scripts\\resultCausality\\mpandroidchart.csv", row.names = FALSE)





