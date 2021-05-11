package utils;

import java.io.*;
import java.nio.file.Paths;

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
            String repoDir = UtilsGit.getNameFromGitUrl(line);
            output = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + repoDir + "/ArchitectureSmells.csv"));
            output1 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + repoDir + "/DesignSmells.csv"));
            output2 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + repoDir + "/ImplementationSmells.csv"));
            output3 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + repoDir + "/MethodMetrics.csv"));
            output4 = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + repoDir + "/TypeMetrics.csv"));

            final String lineSep = System.getProperty("line.separator");

            output.write("NameTag ,HashCommit, Date, Project Name, Package Name, Architecture Smell, Cause of the Smell" + lineSep);
            output1.write("NameTag ,HashCommit, Date, Project Name, Package Name, Type Name, Design Smell, Cause of the Smell" + lineSep);
            output2.write("NameTag ,HashCommit, Date, Project Name, Package Name, Type Name, Method Name, Implementation Smell, Cause of the Smell" + lineSep);
            output3.write("NameTag ,HashCommit, Date, Project Name, Package Name, Type Name, Method Name, LOC, CC, PC" + lineSep);
            output4.write("NameTag ,HashCommit, Date, Project Name, Package Name, Type Name, NOF, NOPF, NOM, NOPM, LOC, WMC, NC, DIT, LCOM, FANIN, FANOUT, File path" + lineSep);
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

    public static void addColumnsCSVTest(String line, String baseOutputDirectory) {
        Writer output = null;
        try {
            String repoDir = UtilsGit.getNameFromGitUrl(line);
            output = new BufferedWriter(new FileWriter(baseOutputDirectory
                    + repoDir + "/resultTest.csv"));

            final String lineSep = System.getProperty("line.separator");

            output.write("NameTag;HashCommit;Date;testsuite;production;loc;nom;wmc;rfc;ar1;et1;it1;gf1;se1;mg1;ro1" + lineSep);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File createTempFile(String prefix, String suffix) {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, Paths.get(prefix,suffix).toString());

        try {
            boolean newFile = temp.createNewFile();
            if(newFile){
                System.out.println("### File temporaneo creato "+temp.getAbsolutePath());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return temp;
    }


    public static File createTempDirectory(String dirname) {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, dirname);

        boolean mkdir = temp.mkdir();

        if(mkdir){
            System.out.println("### Cartella temporanea creata "+temp);
        }

        return temp;
    }
}
