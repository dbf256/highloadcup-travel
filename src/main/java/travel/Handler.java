package travel;

public class  Handler {}

/*
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import travel.model.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Handler extends AbstractHandler {

    private final Storage storage;

    public Handler(Storage storage) {
        this.storage = storage;
    }

    public Storage getStorage() {
        return storage;
    }

    private static final List<String> ENTITIES = Arrays.asList("locations", "users", "visits");

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        String[] parts = target.split("/");
        baseRequest.setHandled(false);
        response.setContentType("text/json");
        if (request.getMethod().equals("GET")) {
            // users / 1 / visits
            if (parts.length == 4 && parts[1].equals("users") && parts[3].equals("visits")) {
                handleVisits(parts[2], request, response);
            // /locations/1/avg
            } else if (parts.length == 4 && parts[3].equals("avg")) {
                handleAvg(parts[2], request, response);
            // /users/1 /locations/1 /visits/1
            } else if (parts.length == 3 && ENTITIES.contains(parts[1])) {
                handleGet(parts[1], parts[2], response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            // /users/new /locations/new /visits/new
            if (parts.length == 3 && ENTITIES.contains(parts[1]) && parts[2].equals("new")) {
                handleNew(parts[1], request, response);
            // /users/1 /locations/1 /visits/1
            } else if (parts.length == 3  && ENTITIES.contains(parts[1])) {
                handleUpdate(parts[1], parts[2], request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        baseRequest.setHandled(true);
    }

    private final String EMPTY_REPLY = "{}";

    private void handleUpdate(String entity, String id, HttpServletRequest request, HttpServletResponse response) {
        String body = null;
        try {
            body = request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        long entityId;
        try {
            entityId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (StringUtils.isEmpty(body)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            response.getWriter().write(EMPTY_REPLY);
        }
        catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Validator validator = new Validator();
        if ("users".equals(entity)) {
            try {
                User user = new User(JsonUtil.fromString(body));
                if (validator.validate(user, true)) {
                    storage.update(entityId, user);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (StorageException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (StorageNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = new Location(JsonUtil.fromString(body));
                if (validator.validate(location, true)) {
                    storage.update(entityId, location);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (StorageException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (StorageNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = new Visit(JsonUtil.fromString(body));
                if (validator.validate(visit, true)) {
                    storage.update(entityId, visit);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (StorageException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (StorageNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleNew(String entity, HttpServletRequest request, HttpServletResponse response) {
        String body = null;
        try {
            body = request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (StringUtils.isEmpty(body)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            response.getWriter().write(EMPTY_REPLY);
        }
        catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Validator validator = new Validator();
        if ("users".equals(entity)) {
            try {
                User user = new User(JsonUtil.fromString(body));
                if (validator.validate(user, false)) {
                    storage.insert(user);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (StorageException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (StorageNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = new Location(JsonUtil.fromString(body));
                if (validator.validate(location, false)) {
                    storage.insert(location);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (StorageException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (StorageNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = new Visit(JsonUtil.fromString(body));
                if (validator.validate(visit, false)) {
                    storage.insert(visit);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (StorageException | IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (StorageNotFoundException  e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    private void handleGet(String entity, String id, HttpServletResponse response) {
        long entityId = 0;
        try {
            entityId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if ("users".equals(entity)) {
            try {
                User user = storage.getUser(entityId);
                response.setStatus(HttpServletResponse.SC_OK);
                // {"id":1,"email":"johndoe@gmail.com","first_name":"John","last_name":"Doe","gender":"m","birth_date":-1247184000}
                //response.getWriter().println(user.toJson());
                writeUser(user, response.getWriter());
            } catch (StorageNotFoundException | IOException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = storage.getLocation(entityId);
                response.setStatus(HttpServletResponse.SC_OK);
                //response.getWriter().println(location.toJson());
                PrintWriter writer = response.getWriter();
                writeLocation(location, writer);
            } catch (StorageNotFoundException | IOException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = storage.getVisit(entityId);
                response.setStatus(HttpServletResponse.SC_OK);
                //response.getWriter().println(visit.toJson());
                writeVisit(visit, response.getWriter());
            } catch (StorageNotFoundException | IOException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    private static final ThreadLocal<DecimalFormat> AVG_FORMAT =
            new ThreadLocal<DecimalFormat>() {
                @Override public DecimalFormat initialValue() {
                    DecimalFormat df = new DecimalFormat("#.#####");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    return df;
                }
            };

    private void handleAvg(String id, HttpServletRequest request, HttpServletResponse response) {
        Location location = null;
        try {
            long locationId = Long.parseLong(id);
            location = storage.getLocation(locationId);
        } catch (NumberFormatException | StorageNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Long fromDate = null;
        Long toDate = null;
        Long fromAge = null;
        Long toAge = null;
        String gender = null;
        try {
            String param = request.getParameter("fromDate");
            if (param != null) {
                fromDate = Long.valueOf(param);
            }
            param = request.getParameter("toDate");
            if (param != null) {
                toDate = Long.valueOf(param);
            }
            param = request.getParameter("fromAge");
            if (param != null) {
                fromAge = Long.valueOf(param);
            }
            param = request.getParameter("toAge");
            if (param != null) {
                toAge = Long.valueOf(param);
            }
            gender = request.getParameter("gender");
            if (gender != null && !"m".equals(gender) && !"f".equals(gender)) {
                throw new IllegalArgumentException("wrong gender");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            Double average = storage.locationAverage(location.id, fromDate, toDate, fromAge, toAge, gender != null ? gender.charAt(0) : null);
            response.setStatus(HttpServletResponse.SC_OK);
            // {"avg": 2.66}
            response.getWriter().print("{\"avg\":");
            response.getWriter().print(AVG_FORMAT.get().format(average));
            response.getWriter().print("}");
        } catch (StorageNotFoundException | IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    private void handleVisits(String id, HttpServletRequest request, HttpServletResponse response) {
        User user = null;
        try {
            long userId = Long.parseLong(id);
            user = storage.getUser(userId);
        } catch (NumberFormatException | StorageNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Long fromDate = null;
        Long toDate = null;
        Long toDistance = null;
        String country = null;
        try {
            String param = request.getParameter("fromDate");
            if (param != null) {
                fromDate = Long.valueOf(param);
            }
            param = request.getParameter("toDate");
            if (param != null) {
                toDate = Long.valueOf(param);
            }
            param = request.getParameter("toDistance");
            if (param != null) {
                toDistance = Long.valueOf(param);
            }
            country = request.getParameter("country");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            List<UserVisit> visits = storage.userVisits(user.id, fromDate, toDate, toDistance, country);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("{\"visits\":[");
            for (int i = 0; i < visits.size(); i++) {
                //{"mark":2,"visited_at":1223268286,"place":"Кольский полуостров"}
                UserVisit visit = visits.get(i);
                writeUserVisit(visit, response.getWriter());
                if (i != visits.size() - 1) {
                    response.getWriter().print(",");
                }
            }
            response.getWriter().println("]}");
        } catch (StorageNotFoundException | IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    private void writeUserVisit(UserVisit visit, PrintWriter writer) {
        writer.print("{\"mark\":");
        writer.print(visit.mark);
        writer.print(",\"visited_at\":");
        writer.print(visit.visited);
        writer.print(",\"place\":\"");
        writer.print(visit.place);
        writer.print("\"}");
    }

    private void writeVisit(Visit visit, PrintWriter writer) {
        writer.print("{\"id\":");
        writer.print(visit.id);
        writer.print(",\"location\":");
        writer.print(visit.location);
        writer.print(",\"mark\":");
        writer.print(visit.mark);
        writer.print(",\"user\":");
        writer.print(visit.user);
        writer.print(",\"visited_at\":");
        writer.print(visit.visited);
        writer.print("}");
    }

    private void writeLocation(Location location, PrintWriter writer) {
        writer.print("{\"id\":");
        writer.print(location.id);
        writer.print(",\"place\":\"");
        writer.print(location.place);
        writer.print("\",\"country\":\"");
        writer.print(location.country);
        writer.print("\",\"city\":\"");
        writer.print(location.city);
        writer.print("\",\"distance\":");
        writer.print(location.distance);
        writer.print("}");
    }

    private void writeUser(User user, PrintWriter writer) {
        writer.print("{\"id\":");
        writer.print(user.id);
        writer.print(",\"email\":\"");
        writer.print(user.email);
        writer.print("\",\"first_name\":\"");
        writer.print(user.firstName);
        writer.print("\",\"last_name\":\"");
        writer.print(user.lastName);
        writer.print("\",\"gender\":\"");
        writer.print(user.gender);
        writer.print("\",\"birth_date\":");
        writer.print(user.birthDate);
        writer.print("}");
    }
}

*/