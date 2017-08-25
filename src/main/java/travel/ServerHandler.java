package travel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import travel.model.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static travel.model.Constants.BUF_SIZE;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    private static final List<String> ENTITIES = Arrays.asList("locations", "users", "visits");

    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest)msg;
            //if (HttpUtil.is100ContinueExpected(request)) {
            //    send100Continue(ctx);
            //}
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
        String[] parts = target.split("/");
        if (request.method().equals(HttpMethod.GET)) {
            // users / 1 / visits
            if (parts.length == 4 && parts[1].equals("users") && parts[3].startsWith("visits")) {
                handleVisits(parts[2], request, ctx);
                // /locations/1/avg
            } else if (parts.length == 4 && parts[3].startsWith("avg")) {
                handleAvg(parts[2], request, ctx);
                // /users/1 /locations/1 /visits/1
            } else if (parts.length == 3 && ENTITIES.contains(parts[1])) {
                handleGet(parts[1], parts[2], ctx);
            } else {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        } else {
            // /users/new /locations/new /visits/new
            if (parts.length == 3 && ENTITIES.contains(parts[1]) && parts[2].startsWith("new")) {
                handleNew(parts[1], request, ctx);
                // /users/1 /locations/1 /visits/1
            } else if (parts.length == 3  && ENTITIES.contains(parts[1])) {
                handleUpdate(parts[1], parts[2], request, ctx);
            } else {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        }
    }

    private static final ByteBuf EMPTY_REPLY = Unpooled.copiedBuffer("{}", CharsetUtil.UTF_8);
    private static final ByteBuf EMPTY_BUFF = Unpooled.EMPTY_BUFFER;
    private static final byte[] EMPTY_REPLY_BYTES = "{}".getBytes(CharsetUtil.UTF_8);

    //private ByteBuf getEmptyBuff() {
    //    return Unpooled.copiedBuffer(EMPTY_REPLY_BYTES);
    //}

    private void handleGet(String entity, String id, ChannelHandlerContext ctx) {
        long entityId = 0;
        try {
            entityId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            return;
        }
        if ("users".equals(entity)) {
            ByteBuf userJson = Main.storage.getUserJson(entityId);
            if (userJson != null) {
                userJson.retain();
                writeResult(HttpResponseStatus.OK, userJson.duplicate(), ctx);
            } else {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
                return;
            }
        } else if ("locations".equals(entity)) {
            ByteBuf userJson = Main.storage.getLocationJson(entityId);
            if (userJson != null) {
                userJson.retain();
                writeResult(HttpResponseStatus.OK, userJson.duplicate(), ctx);
            } else {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
                return;
            }
        } else if ("visits".equals(entity)) {
            ByteBuf userJson = Main.storage.getVisitJson(entityId);
            if (userJson != null) {
                userJson.retain();
                writeResult(HttpResponseStatus.OK, userJson.duplicate(), ctx);
            } else {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
                return;
            }
        } else {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            return;
        }
    }

    private void handleUpdate(String entity, String id, FullHttpRequest request, ChannelHandlerContext ctx) {
        String body = request.content().toString(CharsetUtil.UTF_8);
        long entityId;
        try {
            entityId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            return;
        }
        if (StringUtils.isEmpty(body)) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            return;
        }

        Validator validator = new Validator();
        if ("users".equals(entity)) {
            try {
                User user = new User(JsonUtil.fromString(body));
                if (validator.validate(user, true)) {
                    Main.storage.update(entityId, user);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = new Location(JsonUtil.fromString(body));
                if (validator.validate(location, true)) {
                    Main.storage.update(entityId, location);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = new Visit(JsonUtil.fromString(body));
                if (validator.validate(visit, true)) {
                    Main.storage.update(entityId, visit);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        } else {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
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
            long locationId = Long.parseLong(id);
            location = Main.storage.getLocation(locationId);
        } catch (NumberFormatException | StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            return;
        }
        Long fromDate = null;
        Long toDate = null;
        Long fromAge = null;
        Long toAge = null;
        String gender = null;
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        try {
            List<String> param = params.get("fromDate");
            if (param != null) {
                fromDate = Long.valueOf(param.get(0));
            }
            param = params.get("toDate");
            if (param != null) {
                toDate = Long.valueOf(param.get(0));
            }
            param = params.get("fromAge");
            if (param != null) {
                fromAge = Long.valueOf(param.get(0));
            }
            param = params.get("toAge");
            if (param != null) {
                toAge = Long.valueOf(param.get(0));
            }
            param = params.get("gender");
            if (param != null) {
                gender = param.get(0);
                if (!"m".equals(gender) && !"f".equals(gender)) {
                    throw new IllegalArgumentException("wrong gender");
                }
            }
        } catch (IllegalArgumentException e) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            return;
        }
        try {
            Double average = Main.storage.locationAverage(location.id, fromDate, toDate, fromAge, toAge, gender != null ? gender.charAt(0) : null);
            // {"avg": 2.66}
            ByteBuf buf = Unpooled.buffer(50);
            buf.writeBytes("{\"avg\":".getBytes(CharsetUtil.UTF_8));
            buf.writeBytes(AVG_FORMAT.get().format(average).getBytes(CharsetUtil.UTF_8));
            buf.writeBytes("}".getBytes(CharsetUtil.UTF_8));
            writeResult(HttpResponseStatus.OK, buf, ctx);
        } catch (StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            return;
        }
    }

    private void handleVisits(String id, FullHttpRequest request, ChannelHandlerContext ctx) {
        User user = null;
        try {
            long userId = Long.parseLong(id);
            user = Main.storage.getUser(userId);
        } catch (NumberFormatException | StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            return;
        }
        Long fromDate = null;
        Long toDate = null;
        Long toDistance = null;
        String country = null;
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        try {
            List<String> param = params.get("fromDate");
            if (param != null) {
                fromDate = Long.valueOf(param.get(0));
            }
            param = params.get("toDate");
            if (param != null) {
                toDate = Long.valueOf(param.get(0));
            }
            param = params.get("toDistance");
            if (param != null) {
                toDistance = Long.valueOf(param.get(0));
            }
            param = params.get("country");
            if (param != null) {
                country = param.get(0);
            }
        } catch (NumberFormatException e) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            return;
        }
        try {
            List<Visit> visits = Main.storage.userVisits(user.id, fromDate, toDate, toDistance, country);
            ByteBuf buf = Unpooled.buffer(BUF_SIZE * visits.size());
            buf.writeBytes("{\"visits\":[".getBytes(CharsetUtil.UTF_8));
            for (int i = 0; i < visits.size(); i++) {
                Visit visit = visits.get(i);
                writeUserVisit(visit, Main.storage.locations.get(visit.location).place, buf);
                if (i != visits.size() - 1) {
                    buf.writeBytes(",".getBytes(CharsetUtil.UTF_8));
                }
            }
            buf.writeBytes("]}".getBytes(CharsetUtil.UTF_8));
            writeResult(HttpResponseStatus.OK, buf, ctx);

        } catch (StorageNotFoundException e) {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            return;
        }
    }

    private void handleNew(String entity, FullHttpRequest request, ChannelHandlerContext ctx) {
        String body = request.content().toString(CharsetUtil.UTF_8);

        if (StringUtils.isEmpty(body)) {
            writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            return;
        }


        Validator validator = new Validator();
        if ("users".equals(entity)) {
            try {
                User user = new User(JsonUtil.fromString(body));
                if (validator.validate(user, false)) {
                    Main.storage.insert(user);
                    Main.storage.updateUserJson(user.id, body);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        } else if ("locations".equals(entity)) {
            try {
                Location location = new Location(JsonUtil.fromString(body));
                if (validator.validate(location, false)) {
                    Main.storage.insert(location);
                    Main.storage.updateLocationJson(location.id, body);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            } catch (StorageNotFoundException e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        } else if ("visits".equals(entity)) {
            try {
                Visit visit = new Visit(JsonUtil.fromString(body));
                if (validator.validate(visit, false)) {
                    Main.storage.insert(visit);
                    Main.storage.updateVisitJson(visit.id, body);
                    EMPTY_REPLY.retain();
                    writeResult(HttpResponseStatus.OK, EMPTY_REPLY.duplicate(), ctx);
                } else {
                    writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
                }
            } catch (StorageException | IllegalArgumentException e) {
                writeCode(HttpResponseStatus.BAD_REQUEST, ctx);
            } catch (StorageNotFoundException  e) {
                writeCode(HttpResponseStatus.NOT_FOUND, ctx);
            }
        } else {
            writeCode(HttpResponseStatus.NOT_FOUND, ctx);
        }
    }

    private void addHeaders(FullHttpResponse response) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    }

    private void writeCode(HttpResponseStatus status, ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, EMPTY_BUFF, false);
        addHeaders(response);
        ctx.write(response);
    }

    private void writeResult(HttpResponseStatus status, ByteBuf buffer, ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, buffer, false);
        addHeaders(response);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void writeUserVisit(Visit visit, String place, ByteBuf buf) {
        buf.writeBytes(
                ("{\"mark\":" + visit.mark + ",\"visited_at\":" + visit.visited + ",\"place\":\"" + place + "\"}").getBytes(CharsetUtil.UTF_8)
        );
    }

}