package travel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import travel.model.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpVersion.*;
import static travel.model.Constants.BUF_SIZE;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final List<String> ENTITIES = Arrays.asList("locations", "users", "visits");
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest)msg;
            handleRequest(request, ctx);
        }
    }




    private void handleRequest(FullHttpRequest request, ChannelHandlerContext ctx) {
        String target = null;
        int pos = request.uri().indexOf('?');
        if (pos == -1) {
            target = request.uri();
        } else {
            target = request.uri().substring(0, pos);
        }

        if (request.method().equals(HttpMethod.GET)) {
            // users / 1 / visits
            if (target.startsWith("/users/") && target.endsWith("/visits")) {
                handleVisits(target.substring(7, target.length() - 7), request, ctx);
                // /locations/1/avg
            } else if (target.startsWith("/locations/") && target.endsWith("/avg")) {
                handleAvg(target.substring(11, target.length() - 4), request, ctx);
            // /users/1 /locations/1 /visits/1
            } else if (target.startsWith("/users/")) {
                handleGet("users", target.substring(7, target.length()), ctx);
            } else if (target.startsWith("/locations/")) {
                handleGet("locations", target.substring(11, target.length()), ctx);
            } else if (target.startsWith("/visits/")) {
                handleGet("visits", target.substring(8, target.length()), ctx);
            } else {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
            }
        } else {
            String[] parts = target.split("/");
            // /users/new /locations/new /visits/new
            if (parts.length == 3 && ENTITIES.contains(parts[1]) && parts[2].startsWith("new")) {
                handleNew(parts[1], request, ctx);
                // /users/1 /locations/1 /visits/1
            } else if (parts.length == 3  && ENTITIES.contains(parts[1])) {
                handleUpdate(parts[1], parts[2], request, ctx);
            } else {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
            }
        }
        Main.storage.requestsCount.incrementAndGet();
    }

    private static final ByteBuf EMPTY_REPLY = Unpooled.copiedBuffer("{}", CharsetUtil.UTF_8);
    private static final ByteBuf EMPTY_BUFF = Unpooled.EMPTY_BUFFER;

    private void handleGet(String entity, String id, ChannelHandlerContext ctx) {
        int entityId = 0;
        try {
            entityId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
            return;
        }
        if ("users".equals(entity)) {
            try {
                User user = Main.storage.getUser(entityId);
                ByteBuf buf = ctx.alloc().buffer(BUF_SIZE);
                writeUser(user, buf);
                writeResult(HttpResponseStatus.OK, buf, ctx, false);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
                return;
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = Main.storage.getLocation(entityId);
                ByteBuf buf = ctx.alloc().buffer(BUF_SIZE);
                writeLocation(location, buf);
                writeResult(HttpResponseStatus.OK, buf, ctx, false);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
                return;
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = Main.storage.getVisit(entityId);
                ByteBuf buf = ctx.alloc().buffer(BUF_SIZE);
                writeVisit(visit, buf);
                writeResult(HttpResponseStatus.OK, buf, ctx, false);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
                return;
            }
        } else {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
            return;
        }
    }

    private void handleUpdate(String entity, String id, FullHttpRequest request, ChannelHandlerContext ctx) {
        String body = request.content().toString(CharsetUtil.UTF_8);
        int entityId;
        try {
            entityId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            return;
        }
        if (StringUtils.isEmpty(body)) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            return;
        }

        Validator validator = new Validator();
        if ("users".equals(entity)) {
            try {
                User user = new User(JsonUtil.fromString(body));
                if (validator.validate(user, true)) {
                    Main.storage.update(entityId, user);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx, true);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = new Location(JsonUtil.fromString(body));
                if (validator.validate(location, true)) {
                    Main.storage.update(entityId, location);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx, true);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = new Visit(JsonUtil.fromString(body));
                if (validator.validate(visit, true)) {
                    Main.storage.update(entityId, visit);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx, true);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
            }
        } else {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
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

    private void handleAvg(String id, FullHttpRequest request, ChannelHandlerContext ctx) {
        Location location = null;
        try {
            int locationId = Integer.parseInt(id);
            location = Main.storage.getLocation(locationId);
        } catch (NumberFormatException | StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
            return;
        }
        Integer fromDate = null;
        Integer toDate = null;
        Integer fromAge = null;
        Integer toAge = null;
        String gender = null;
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        try {
            List<String> param = params.get("fromDate");
            if (param != null) {
                fromDate = Integer.valueOf(param.get(0));
            }
            param = params.get("toDate");
            if (param != null) {
                toDate = Integer.valueOf(param.get(0));
            }
            param = params.get("fromAge");
            if (param != null) {
                fromAge = Integer.valueOf(param.get(0));
            }
            param = params.get("toAge");
            if (param != null) {
                toAge = Integer.valueOf(param.get(0));
            }
            param = params.get("gender");
            if (param != null) {
                gender = param.get(0);
                if (!"m".equals(gender) && !"f".equals(gender)) {
                    throw new IllegalArgumentException("wrong gender");
                }
            }
        } catch (IllegalArgumentException e) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx, false);
            return;
        }
        try {
            Double average = Main.storage.locationAverage(location.id, fromDate, toDate, fromAge, toAge, gender != null ? gender.charAt(0) : null);
            // {"avg": 2.66}
            ByteBuf buf = ctx.alloc().buffer(40);
            buf.writeCharSequence("{\"avg\":" + AVG_FORMAT.get().format(average) + "}", CharsetUtil.UTF_8);
            writeResult(HttpResponseStatus.OK, buf, ctx, false);
        } catch (StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
            return;
        }
    }

    private void handleVisits(String id, FullHttpRequest request, ChannelHandlerContext ctx) {
        User user = null;
        try {
            int userId = Integer.parseInt(id);
            user = Main.storage.getUser(userId);
        } catch (NumberFormatException | StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
            return;
        }
        Integer fromDate = null;
        Integer toDate = null;
        Integer toDistance = null;
        String country = null;
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        try {
            List<String> param = params.get("fromDate");
            if (param != null) {
                fromDate = Integer.valueOf(param.get(0));
            }
            param = params.get("toDate");
            if (param != null) {
                toDate = Integer.valueOf(param.get(0));
            }
            param = params.get("toDistance");
            if (param != null) {
                toDistance = Integer.valueOf(param.get(0));
            }
            param = params.get("country");
            if (param != null) {
                country = param.get(0);
            }
        } catch (NumberFormatException e) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx, false);
            return;
        }
        try {
            List<Visit> visits = Main.storage.userVisits(user.id, fromDate, toDate, toDistance, country);
            ByteBuf buf = ctx.alloc().buffer(BUF_SIZE * visits.size());
            buf.writeCharSequence("{\"visits\":[", CharsetUtil.UTF_8);
            for (int i = 0; i < visits.size(); i++) {
                Visit visit = visits.get(i);
                writeUserVisit(visit, ((Location)Main.storage.locations.get(visit.location)).place, buf);
                if (i != visits.size() - 1) {
                    buf.writeCharSequence(",", CharsetUtil.UTF_8);
                }
            }
            buf.writeCharSequence("]}", CharsetUtil.UTF_8);
            writeResult(HttpResponseStatus.OK, buf, ctx, false);
        } catch (StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, false);
            return;
        }
    }

    private void handleNew(String entity, FullHttpRequest request, ChannelHandlerContext ctx) {
        String body = request.content().toString(CharsetUtil.UTF_8);

        if (StringUtils.isEmpty(body)) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            return;
        }


        Validator validator = new Validator();
        if ("users".equals(entity)) {
            try {
                User user = new User(JsonUtil.fromString(body));
                if (validator.validate(user, false)) {
                    Main.storage.insert(user);
                    //Main.storage.updateUserJson(user.id, body);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx, true);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = new Location(JsonUtil.fromString(body));
                if (validator.validate(location, false)) {
                    Main.storage.insert(location);
                    //Main.storage.updateLocationJson(location.id, body);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx, true);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = new Visit(JsonUtil.fromString(body));
                if (validator.validate(visit, false)) {
                    Main.storage.insert(visit);
                    //Main.storage.updateVisitJson(visit.id, body);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx, true);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx, true);
            } catch (StorageNotFoundException  e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
            }
        } else {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx, true);
        }
    }

    private static void addHeaders(FullHttpResponse response, boolean close) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (!close) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
    }

    private static final FullHttpResponse REPLY_NOT_FOUND_CLOSE = buildReply(HttpResponseStatus.NOT_FOUND, true);
    private static final FullHttpResponse REPLY_NOT_FOUND_KEEP_ALIVE = buildReply(HttpResponseStatus.NOT_FOUND, false);
    private static final FullHttpResponse REPLY_BAD_REQUEST_CLOSE = buildReply(HttpResponseStatus.BAD_REQUEST, true);
    private static final FullHttpResponse REPLY_BAD_REQUEST_KEEP_ALIVE = buildReply(HttpResponseStatus.BAD_REQUEST, false);

    private static FullHttpResponse buildReply(HttpResponseStatus status, boolean close) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, EMPTY_BUFF, false);
        addHeaders(response, close);
        return response;
    }
    
    private void writeCode(HttpResponseStatus status, ChannelHandlerContext ctx, boolean close) {
        if (status.code() == HttpResponseStatus.NOT_FOUND.code() && close) {
            ctx.writeAndFlush(REPLY_NOT_FOUND_CLOSE, ctx.voidPromise());
        } else if (status.code() == HttpResponseStatus.NOT_FOUND.code() && !close) {
            ctx.writeAndFlush(REPLY_NOT_FOUND_KEEP_ALIVE, ctx.voidPromise());
        } else if (status.code() == HttpResponseStatus.BAD_REQUEST.code() && close) {
            ctx.writeAndFlush(REPLY_BAD_REQUEST_CLOSE, ctx.voidPromise());
        } else {
            ctx.writeAndFlush(REPLY_BAD_REQUEST_KEEP_ALIVE, ctx.voidPromise());
        }
    }

    private void writeResult(HttpResponseStatus status, ByteBuf buffer, ChannelHandlerContext ctx, boolean close) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, buffer, false);
        addHeaders(response, close);
        ctx.writeAndFlush(response, ctx.voidPromise());
        //if (close) {
        //    ctx.close();
        //}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void writeUserVisit(Visit visit, String place, ByteBuf buf) {
        //buf.writeBytes(BYTES_USER_VISIT_MARK);
        //buf.writeBytes(String.valueOf(visit.mark).getBytes(CharsetUtil.UTF_8));
        //buf.writeBytes(BYTES_USER_VISIT_VISITED_AT);
        //buf.writeBytes(String.valueOf(visit.visited).getBytes(CharsetUtil.UTF_8));
        //buf.writeBytes(BYTES_USER_VISIT_PLACE);
        //buf.writeBytes(visit.place.getBytes(CharsetUtil.UTF_8));
        //buf.writeBytes(BYTES_USER_VISIT_END);
        buf.writeCharSequence(
                ("{\"mark\":" + visit.mark + ",\"visited_at\":" + visit.visited + ",\"place\":\"" + place + "\"}"), CharsetUtil.UTF_8
        );
    }

    private void writeVisit(Visit visit, ByteBuf buf) {
//        buf.writeBytes(BYTES_VISIT_ID);
//        buf.writeBytes(String.valueOf(visit.id).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_VISIT_LOCATION);
//        buf.writeBytes(String.valueOf(visit.location).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_VISIT_MARK);
//        buf.writeBytes(String.valueOf(visit.mark).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_VISIT_USER);
//        buf.writeBytes(String.valueOf(visit.user).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_VISIT_VISITED_AT);
//        buf.writeBytes(String.valueOf(visit.visited).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_VISIT_END);

        buf.writeCharSequence(
                "{\"id\":" + visit.id + ",\"location\":" + visit.location + ",\"mark\":" + visit.mark + ",\"user\":" + visit.user +
                        ",\"visited_at\":" + visit.visited + "}"
               ,CharsetUtil.UTF_8
        );

    }

    private void writeLocation(Location location, ByteBuf buf) {
//        buf.writeBytes(BYTES_LOCATION_ID);
//        buf.writeBytes(String.valueOf(location.id).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_LOCATION_PLACE);
//        buf.writeBytes(String.valueOf(location.place).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_LOCATION_COUNTRY);
//        buf.writeBytes(String.valueOf(location.country).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_LOCATION_CITY);
//        buf.writeBytes(String.valueOf(location.city).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_LOCATION_DISTANCE);
//        buf.writeBytes(String.valueOf(location.distance).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_LOCATION_END);

        buf.writeCharSequence(
                ("{\"id\":" + location.id + ",\"place\":\"" + location.place + "\",\"country\":\"" + location.country + "\",\"city\":\"" + location.city +
                        "\",\"distance\":" + location.distance + "}"
                ), CharsetUtil.UTF_8
        );
    }

    private void writeUser(User user, ByteBuf buf) {
//        buf.writeBytes(BYTES_USER_ID);
//        buf.writeBytes(String.valueOf(user.id).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_USER_EMAIL);
//        buf.writeBytes(String.valueOf(user.email).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_USER_FIRST_NAME);
//        buf.writeBytes(String.valueOf(user.firstName).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_USER_LAST_NAME);
//        buf.writeBytes(String.valueOf(user.lastName).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_USER_GENDER);
//        buf.writeBytes(String.valueOf(user.gender).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_USER_BIRTH_DATE);
//        buf.writeBytes(String.valueOf(user.birthDate).getBytes(CharsetUtil.UTF_8));
//        buf.writeBytes(BYTES_USER_END);

        buf.writeCharSequence(
                "{\"id\":" + user.id + ",\"email\":\"" + user.email + "\",\"first_name\":\"" + user.firstName + "\",\"last_name\":\"" + user.lastName +
                        "\",\"gender\":\"" + user.gender + "\",\"birth_date\":" + user.birthDate + "}",
                CharsetUtil.UTF_8
        );
    }

    public static final byte[] BYTES_USER_VISIT_MARK = "{\"mark\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_VISIT_VISITED_AT = ",\"visited_at\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_VISIT_PLACE = ",\"place\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_VISIT_END = "\"}".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_LOCATION_ID = "{\"id\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_LOCATION_PLACE = ",\"place\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_LOCATION_COUNTRY = "\",\"country\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_LOCATION_CITY = "\",\"city\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_LOCATION_DISTANCE = "\",\"distance\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_LOCATION_END = "}".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_ID = "{\"id\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_EMAIL = ",\"email\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_FIRST_NAME = "\",\"first_name\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_LAST_NAME = "\",\"last_name\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_GENDER = "\",\"gender\":\"".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_BIRTH_DATE = "\",\"birth_date\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_USER_END = "}".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_VISIT_ID = "{\"id\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_VISIT_LOCATION = ",\"location\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_VISIT_MARK = ",\"mark\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_VISIT_USER = ",\"user\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_VISIT_VISITED_AT = ",\"visited_at\":".getBytes(CharsetUtil.UTF_8);
    public static final byte[] BYTES_VISIT_END = "}".getBytes(CharsetUtil.UTF_8);
}