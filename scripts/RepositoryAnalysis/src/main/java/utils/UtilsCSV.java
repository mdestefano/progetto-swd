package utils;

import java.io.*;
import java.util.List;

public class UtilsCSV {

    public static void mergeCSV(String pathDestination, String nomeFile, String hashCommit,
                                String pathCommit, String projectName) throws IOException {

        nomeFile = pathCommit + "/" + hashCommit + nomeFile;
        System.out.println("#### merge di " + nomeFile);

        BufferedReader br = null;
        final String lineSep = System.getProperty("line.separator");
        File file = new File(nomeFile);
        Writer output = new BufferedWriter(new FileWriter(pathDestination, true));

        try {
            //names don't conflict or just use different folders
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            int i = 0;
            int numCol = 0;
            for (line = br.readLine(); line != null; line = br.readLine(), i++) {
                if (i != 0) {
                    if (UtilsWord.countOccurences(line, ',') != numCol) {
                        StringBuilder buffer = new StringBuilder(line);
                        line = buffer.reverse().toString().replaceFirst(",", "");
                        line = new StringBuffer(line).reverse().toString();
                    }
                    String[] lineArray = line.split(",");
                    line = line.replaceAll(lineArray[3], projectName);

                    output.append(line).append(lineSep);
                } else {
                    numCol = UtilsWord.countOccurences(line, ',');
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                br.close();
            output.close();
        }
    }

    public static void mergeAll(List<String> hashCommits, String line, String pathCommit, String baseOutputFolder) {
        for (String hashCommit : hashCommits) {
            try {
                String repoDir = UtilsGit.getNameFromGitUrl(line);
                UtilsCSV.mergeCSV(baseOutputFolder + repoDir + "/ArchitectureSmells.csv", "/ArchitectureSmells.csv",
                        hashCommit, pathCommit, repoDir);
                UtilsCSV.mergeCSV(baseOutputFolder + repoDir + "/DesignSmells.csv", "/DesignSmells.csv",
                        hashCommit, pathCommit, repoDir);
                UtilsCSV.mergeCSV(baseOutputFolder + repoDir + "/ImplementationSmells.csv",
                        "/ImplementationSmells.csv", hashCommit, pathCommit, repoDir);
                UtilsCSV.mergeCSV(baseOutputFolder + repoDir + "/MethodMetrics.csv",
                        "/MethodMetrics.csv", hashCommit, pathCommit, repoDir);
                UtilsCSV.mergeCSV(baseOutputFolder + repoDir + "/TypeMetrics.csv", "/TypeMetrics.csv",
                        hashCommit, pathCommit, repoDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addInfoToCSV(String nomeFile, String support) throws IOException {
        BufferedReader br = null;
        BufferedWriter bw = null;
        final String lineSep = System.getProperty("line.separator");
        File file = null, file2 = null;
        try {
            file = new File(nomeFile);
            file2 = new File(nomeFile + "1");//so the
            //names don't conflict or just use different folders
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line;
            String addedColumn;
            int i = 0;
            for (line = br.readLine(); line != null; line = br.readLine(), i++) {
                if (i == 0) {
                    addedColumn = "NameTag ,HashCommit, Date, ";
                } else {
                    addedColumn = support;
                }
                bw.write(addedColumn + line + lineSep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
            if(file != null) {
                boolean delete = file.delete();

                if(delete){
                    System.out.println("### File eliminato "+nomeFile);
                }
            }

            File fBuffer = new File(nomeFile);

            if(file2!=null){
                boolean b = file2.renameTo(fBuffer);

                if(b){
                    System.out.println("### File rinominato "+fBuffer);
                }
            }
        }
    }
}
