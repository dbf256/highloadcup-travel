package travel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import travel.model.Location;
import travel.model.User;
import travel.model.Visit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Reader {
    
    public void readData(Storage storage) throws IOException {
        String path = System.getenv("DATA_PATH");
        File[] filesList = new File(path).listFiles();

        for(File f : filesList){
            if(f.isFile() && f.getAbsolutePath().endsWith("json")){
                String content = Files.readAllLines(Paths.get(f.getAbsolutePath()), StandardCharsets.UTF_8).stream().collect(Collectors.joining(""));
                System.out.println("Reading " + f.getAbsolutePath());
                JsonParser parser = new JsonParser();
                JsonObject o = parser.parse(content).getAsJsonObject();
                if (o.keySet().contains("locations")) {
                    for (JsonElement element : o.get("locations").getAsJsonArray()) {
                        Location location = new Location(element.getAsJsonObject());
                        storage.insert(location);
                        storage.updateLocationJson(location.id, element.toString());
                    }
                } else if (o.keySet().contains("users")) {
                    for (JsonElement element : o.get("users").getAsJsonArray()) {
                        User user = new User(element.getAsJsonObject());
                        storage.insert(user);
                        storage.updateUserJson(user.id, element.toString());
                    }
                } else if (o.keySet().contains("visits")) {
                    for (JsonElement element : o.get("visits").getAsJsonArray()) {
                        Visit visit = new Visit(element.getAsJsonObject());
                        storage.insert(visit);
                        storage.updateVisitJson(visit.id, element.toString());
                    }
                }
                o = null;
            }
        }
        System.out.println("Done");
    }
}
