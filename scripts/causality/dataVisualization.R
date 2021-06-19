library(plotly)
library(readr)
versioni_designite <-c()
versioni_test <-c()
architecturesmells <- c()
testsmells <- c()
data_designite <- read.csv("C:\\Users\\Grazia\\Desktop\\SD\\progetto-swd\\scripts\\datasets\\designite\\canal\\ArchitectureSmells.csv") 
data_test <- read.csv("C:\\Users\\Grazia\\Desktop\\SD\\progetto-swd\\scripts\\datasets\\risultatiTest\\canal\\resultTest.csv", sep = ";") 


versioni_designite <- data_designite$NameTag
print(versioni_designite)
nRowsDesignite <- nrow(data_designite)
for(i in 1:nRowsDesignite){
  architecturesmells = c(architecturesmells,"1")
}

versioni_test <- data_test$NameTag



nRowTest <- nrow(data_test)
for(i in 1:nRowTest){
  numSmell <- data_test[i, 10]
  totSmell <- 0
  totSmell <- totSmell + numSmell
  numSmell <- data_test[i, 11]
  totSmell <- totSmell + numSmell
  numSmell <- data_test[i, 12]
  totSmell <- totSmell + numSmell
  numSmell <- data_test[i, 13]
  totSmell <- totSmell + numSmell
  numSmell <- data_test[i, 14]
  totSmell <- totSmell + numSmell
  numSmell <- data_test[i, 15]
  totSmell <- totSmell + numSmell
  numSmell <- data_test[i, 16]
  totSmell <- totSmell + numSmell
  
  smellInChar = as.character(totSmell)
  testsmells = c(testsmells, smellInChar)
}

print("Prima")
versioni_uniq_designite <- unique(versioni_designite)
print(length(versioni_uniq_designite))

versioni_uniq_test <- unique(versioni_test)
print(length(versioni_uniq_test))
print("Intersezione")
intersezione <- intersect(versioni_uniq_designite,versioni_uniq_test)
print(length(intersezione))

for(versione in versioni_designite){ #se una versione che sta in designite non sta in test allora non considero
  if (versione %in% versioni_uniq_test){
    #non faccio niente
  } else {
    versioni_designite <- versioni_designite[versioni_designite != versione]; 
    architecturesmells <- architecturesmells[-1]; #without the first element
  }
}

print("Dopo")
versioni_uniq_designite <- unique(versioni_designite)
print(sort(versioni_uniq_designite))
print(length(versioni_uniq_designite))

versioni_uniq_test <- unique(versioni_test)
print(sort(versioni_uniq_test))
print(length(versioni_uniq_test))

print("Le liste sono uguali?")
print(identical(sort(versioni_uniq_designite), sort(versioni_uniq_test)))

print(length(versioni_designite))
print(length(architecturesmells))


print(length(versioni_test))
print(length(testsmells))


fig1 <- plot_ly(y=architecturesmells, x=versioni_designite, name="Architecture smells", histfunc='sum', type = "histogram")
fig1 <- fig1 %>% layout(yaxis=list(type='linear'))
fig2 <- plot_ly(y=testsmells, x=versioni_test, name="Test smells",histfunc='sum', type = "histogram")
fig2 <- fig2 %>% layout(yaxis=list(type='linear'))

fig <- subplot(fig1, fig2)
title_project <- data_designite[1,4]
fig <- fig %>% layout(title = title_project)
fig

fig