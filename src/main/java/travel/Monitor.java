package travel;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Monitor implements Runnable {

    public final Storage storage;
    public final int firstStageRequests;
    public final int secondStageRequests;
    public final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    public Monitor(Storage storage) {
        this.storage = storage;
        boolean test = true;
        try {
            List<String> lines = Files.readAllLines(Paths.get(System.getenv("DATA_PATH"), "options.txt"), StandardCharsets.UTF_8);
            test = lines.get(1).trim().equals("0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (test) {
            firstStageRequests = 9030;
            secondStageRequests = 3000;
        } else {
            firstStageRequests = 150150;
            secondStageRequests = 40000;
        }
    }

    public void start() {
        storage.gcTracker.set(true);
        storage.requestsCount.set(0);
        ses.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void run() {
        if (storage.gcTracker.get()) {
            if (storage.requestsCount.get() >= firstStageRequests + secondStageRequests && !storage.secondStageGc.get()) {
                storage.secondStageGc.set(true);
                System.out.println("Second stage gc " + storage.requestsCount.get());
                System.gc();
            } else {
                //System.out.println("Skip GC: " + storage.requestsCount.get() + " " + storage.secondStageGc.get());
            }
        }
    }
}