package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UtilsGit {
    public static String getNameFromGitUrl(String gitUrl){
        int indexFirst = gitUrl.lastIndexOf("/");
        return gitUrl.substring(indexFirst+1,gitUrl.length()-4);
    }

    public static String getUrlTagsFromGitUrl(String gitUrl){
       String firstPart = gitUrl.substring(0, gitUrl.lastIndexOf("/"));
       String secondPart = gitUrl.substring(firstPart.lastIndexOf("/")+1, gitUrl.length()-4);

        return "https://api.github.com/repos/"+secondPart+"/tags";
    }

    public static List<String> getTags(String url, String baseOutputFolder) {
        List<String> arrayReturn = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray a;
        try {
            a = (JSONArray) parser.parse(new FileReader(baseOutputFolder + UtilsGit.getNameFromGitUrl(url) + "/tag.json"));
            for (Object object : a) {
                JSONObject info = (JSONObject) object;
                JSONObject commit = (JSONObject) info.get("commit");
                arrayReturn.add(commit.get("sha").toString());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return arrayReturn;
    }
}
