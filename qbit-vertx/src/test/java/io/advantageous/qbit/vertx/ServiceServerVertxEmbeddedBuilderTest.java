package io.advantageous.qbit.vertx;

import io.advantageous.qbit.annotation.RequestMapping;
import io.advantageous.qbit.annotation.RequestMethod;
import io.advantageous.qbit.server.ServiceServer;
import io.advantageous.qbit.service.Callback;
import io.advantageous.qbit.service.ServiceBundle;
import org.boon.HTTP;
import org.boon.core.Sys;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.junit.Assert.*;

public class ServiceServerVertxEmbeddedBuilderTest {

    ServiceServerVertxEmbeddedBuilder  builder;

    boolean ok;

    static interface ClientServiceInterface {
        String ping(Callback<String> callback, String ping);
    }

    static class MockService {

        int callCount;

        @RequestMapping(method = RequestMethod.POST)
        public String ping(String ping) {
            callCount++;
            return ping + " pong";
        }
    }


    public static class BeforeHandler implements Consumer<ServiceBundle> {

        @Override
        public void accept(ServiceBundle serviceBundle) {

            serviceBundle.addService(new MockService());
        }
    }

    @Before
    public void setUp() throws Exception {
        builder = new ServiceServerVertxEmbeddedBuilder();

        builder.setPort(4049);

        builder.setBeforeStartHandler(BeforeHandler.class);

        final ServiceServer server = builder.build();

        server.start();
    }

    @Test
    public void test() {

        Sys.sleep(2_000);

        final String ping = HTTP.postJSON("http://localhost:4049/services/mockservice/ping", "\"ping\"");

        ok = ping.equals("\"ping pong\"") || die(ping);

    }
    @After
    public void tearDown() throws Exception {

        Sys.sleep(5_000);

    }
}