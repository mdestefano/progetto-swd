library(tidyverse)
library(dplyr)
library(arules)
library(arulesViz)

dirs <- list.dirs(path = "C:\\Users\\Armando\\swdProjects\\progetto-swd\\scripts\\datasets\\designite", full.names = TRUE, recursive = TRUE)
dirs = dirs[-1]
fileDesign <- paste(dirs[1], "\\DesignSmells.csv", sep = "")
df_design <-  read_csv(fileDesign)
for (dir in dirs[-1]){
  fileDesign <- paste(dir, "\\DesignSmells.csv", sep = "")
  design_smells_csv = read_csv(fileDesign)
  df_design <- rbind(df_design, design_smells_csv)  
}
dirs <- list.dirs(path = "C:\\Users\\Armando\\swdProjects\\progetto-swd\\scripts\\datasets\\risultatiTest", full.names = TRUE, recursive = TRUE)
dirs = dirs[-1]
fileTest <- paste(dirs[1], "\\resultTest.csv", sep = "")
df_test <- read.csv(fileTest, sep = ";")
for (dir in dirs[-1]){
  fileTest <- paste(dir, "\\resultTest.csv", sep = "")
  test_smells_csv = read.csv(fileTest, sep = ";")
  df_test <- rbind(df_test, test_smells_csv)  
}

df_design$ClassName <- paste(df_design$`Package Name`, df_design$`Type Name`, sep=".")

# nRowDesign <- nrow(df_design)
# for(i in 1:nRowDesign){
#   name_class <- paste(df_design[i, 5] ,df_design[i, 6], sep=".")
#   row <- filter(df_test, production == name_class)
# }

# df_design %>%
#   select(Package Name,Type Name) %>%
#   mutate(
#     Package Name= NULL,
#     Type Name= NULL,
#     Class Name = "package.class"
#   )



df_join <- inner_join(df_design, df_test, by=c("ClassName" = "production"))

# df_join %>%
#   select(Class Name, `Design Smell`, ar1,et1,it1,gf1,se1,mg1,ro1) %>%
#   mutate(
#     Smells = paste(df_design[i, 5] ,df_design[i, 6], sep=".")
#   )

df_join$`Smells` <- paste(df_join$`Design Smell`, "", sep = "")
nRowJoin <- nrow(df_join)

for(i in 1:nRowJoin){
  print("Riga ")
  print(i)
  ar1 = df_join[i, 18]
  et1 = df_join[i, 19]
  it1 = df_join[i, 20]
  gf1 = df_join[i, 21]
  se1 = df_join[i, 22]
  mg1 = df_join[i, 23]
  ro1 = df_join[i, 24]
  
  if (ar1>0){
    df_join[i, 25] <-  paste(df_join[i, 25], "ar1", sep = " ")
  }
  
  if (et1>0){
    df_join[i, 25] <- paste(df_join[i, 25], "et1", sep = " ")
  }

  if (it1>0){
    df_join[i, 25] <- paste(df_join[i, 25], "it1", sep = " ")
  }

  if (gf1>0){
    df_join[i, 25] <- paste(df_join[i, 25], "gf1", sep = " ")
  }

  if (se1>0){
    df_join[i, 25] <- paste(df_join[i, 25], "se1", sep = " ")
  }

  if (mg1>0){
    df_join[i, 25] <- paste(df_join[i, 25], "mg1", sep = " ")
  }

  if (ro1>0){
    df_join[i, 25] <- paste(df_join[i, 25], "ro1", sep = " ")
  }
  
}

write.csv(df_join, "df_join.csv")

# association rule
# set.seed = 220 # Setting seed
# associa_rules = apriori(data = df_join, 
#                         parameter = list(support = 0.004, 
#                                          confidence = 0.2))
# 
# # Plot
# itemFrequencyPlot(df_join, topN = 10)
# 
# # Visualising the results
# inspect(sort(associa_rules, by = 'lift')[1:10])
# plot(associa_rules, method = "graph", 
#      measure = "confidence", shading = "lift")