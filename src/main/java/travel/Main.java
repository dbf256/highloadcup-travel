package travel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Locale;

public class Main {


    private final int port;

    public Main(int port) {
        this.port = port;
    }

    public static String getOperatingSystemType() {

        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
            return "mac";
        } else if (os.indexOf("win") >= 0) {
            return "win";
        } else if (os.indexOf("nux") >= 0) {
            return "unix";
        } else {
            return "unknown";
        }
    }

    public void run() throws Exception {
        // Configure the server.
        String os = getOperatingSystemType();
        System.out.println("Running at: " + os);
        EventLoopGroup bossGroup = "unix".equals(os) ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup workerGroup = "unix".equals(os) ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel("unix".equals(os) ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            System.out.println("Started");
            Channel ch = b.bind(port).sync().channel();
            new Warmup().warmup();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static final Storage storage = new Storage();

    public static void main(String[] args) throws Exception {

        new Reader().readData(storage);
        //Handler handler = new Handler(storage);
        new Main(80).run();
    }

    /*
    public static void main(String[] args) throws Exception {
        Server server = new Server(80);
        Storage storage = new Storage();
        new Reader().readData(storage);
        Handler handler = new Handler(storage);
        new Warmup(handler).warmup();
        server.setHandler(handler);
        server.start();
        server.join();
    }
    */
}
