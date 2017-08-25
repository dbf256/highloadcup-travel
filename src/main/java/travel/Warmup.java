package travel;


import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.util.ArrayList;
import java.util.List;

public class Warmup {

    public Warmup() {

    }

    public void warmup() {

        List<Object> objects = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            try {
                objects.add(Request.Get("http://localhost/users/" + i).execute().returnContent());
                objects.add(Request.Get("http://localhost/locations/" + i).execute().returnContent());
                objects.add(Request.Get("http://localhost/visits/" + i).execute().returnContent());
                objects.add(Request.Get("http://localhost/locations/" + i + "/avg").execute().returnContent());
                objects.add(Request.Get("http://localhost/users/" + i + "/visits").execute().returnContent());
                objects.add(Request.Get("http://localhost/users/" + i + "/visits").execute().returnContent());
                objects.add(Request.Post("http://localhost/users/" + i).bodyString("", ContentType.APPLICATION_JSON).execute().returnContent());
                objects.add(Request.Post("http://localhost/users/new").bodyString("", ContentType.APPLICATION_JSON).execute().returnContent());
                objects.add(Request.Post("http://localhost/visits/" + i).bodyString("", ContentType.APPLICATION_JSON).execute().returnContent());
                objects.add(Request.Post("http://localhost/visits/new").bodyString("", ContentType.APPLICATION_JSON).execute().returnContent());
                objects.add(Request.Post("http://localhost/locations/" + i).bodyString("", ContentType.APPLICATION_JSON).execute().returnContent());
                objects.add(Request.Post("http://localhost/locations/new").bodyString("", ContentType.APPLICATION_JSON).execute().returnContent());
            } catch (Exception ignore) {}
        }
        System.out.println("Warmup: " + objects.size() + " in " + (System.currentTimeMillis() - start));
        objects = null;
        System.gc();

    }
}
