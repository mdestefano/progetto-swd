package utils;

import java.io.*;

public class UtilsFileDirectory {
    public static boolean deleteDirectory(File f) {
        File[] allContents = f.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return f.delete();
    }


    public static void addColumnsCSV(String line, String baseOutputDirectory) {
        Writer output = null, output1 = null, output2 = null, output3 = null, output4 = null;
        try {
            output = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + UtilsGit.getNameFromGitUrl(line) + "/ArchitectureSmells.csv"));
            output1 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + UtilsGit.getNameFromGitUrl(line) + "/DesignSmells.csv"));
            output2 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + UtilsGit.getNameFromGitUrl(line) + "/ImplementationSmells.csv"));
            output3 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + UtilsGit.getNameFromGitUrl(line) + "/MethodMetrics.csv"));
            output4 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + UtilsGit.getNameFromGitUrl(line) + "/TypeMetrics.csv"));

            final String lineSep = System.getProperty("line.separator");

            output.write("HashCommit, Date, Project Name, Package Name, Architecture Smell, Cause of the Smell" + lineSep);
            output1.write("HashCommit, Date, Project Name, Package Name, Type Name, Design Smell, Cause of the Smell" + lineSep);
            output2.write("HashCommit, Date, Project Name, Package Name, Type Name, Method Name, Implementation Smell, Cause of the Smell" + lineSep);
            output3.write("HashCommit, Date, Project Name, Package Name, Type Name, Method Name, LOC, CC, PC" + lineSep);
            output4.write("HashCommit, Date, Project Name, Package Name, Type Name, NOF, NOPF, NOM, NOPM, LOC, WMC, NC, DIT, LCOM, FANIN, FANOUT, File path" + lineSep);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (output1 != null) {
                    output1.close();
                }
                if (output2 != null) {
                    output2.close();
                }
                if (output3 != null) {
                    output3.close();
                }
                if (output4 != null) {
                    output4.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File createTempFile(String prefix, String suffix) {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, prefix + suffix);

        try {
            boolean newFile = temp.createNewFile();
            if(newFile){
                System.out.println("### File temporaneo creato "+temp);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return temp;
    }


    public static File createTempDirectory(String fileName) {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, fileName);

        boolean mkdir = temp.mkdir();

        if(mkdir){
            System.out.println("## Cartella temporanea creata "+temp);
        }

        return temp;
    }
}
