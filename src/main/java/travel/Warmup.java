package travel;


import java.util.ArrayList;
import java.util.List;

public class Warmup {

    private Storage storage;

    public Warmup(Storage storage) {
        this.storage = storage;
    }

    public void warmup() {

        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            try {
                objects.add(storage.getUser(i));
                objects.add(storage.getLocation(i));
                objects.add(storage.getVisit(i));
                objects.add(storage.locationAverage(i, Long.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, 'm'));
                objects.add(storage.userVisits(i, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, storage.getLocation(i).country));
            } catch (Exception ignore) {}
        }
        System.out.println("Warmup: " + objects.size());
        objects = null;
        System.gc();

    }
}
