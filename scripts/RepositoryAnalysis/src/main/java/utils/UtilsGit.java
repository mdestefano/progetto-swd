package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static HashMap<String,String> getTags(String url, String baseOutputFolder) {
        HashMap<String,String> hashMap = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONArray a;
        try {
            a = (JSONArray) parser.parse(new FileReader(baseOutputFolder + UtilsGit.getNameFromGitUrl(url) + "/tag.json"));
            for (Object o : a) {
                JSONObject info = (JSONObject) o;
                JSONObject commit = (JSONObject) info.get("commit");

                hashMap.put(commit.get("sha").toString(),info.get("name").toString());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
    public static List<String> getHashTag(HashMap<String,String> hashTag){
        List<String> arrayReturn = new ArrayList<>();
        for (String s : hashTag.keySet()){
            arrayReturn.add(s);
        }
        return arrayReturn;
    }

}
