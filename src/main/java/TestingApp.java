import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class TestingApp extends AllDirectives {
    private static final String NAME = "lab4";
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final String OK = "OK\n";
    private static final String PACKAGE_ID = "packageId";
    private static final int TIMEOUT = 3000;
    private static ActorRef router;
    public static void main(String[] args) {
        ActorSystem acSys = ActorSystem.create(NAME);
        router = acSys.actorOf(Props.create(ActorRouter.class, acSys));
        Http http = Http.get(acSys);
        ActorMaterializer acMaterializer = ActorMaterializer.create(acSys);
        TestingApp instance = new TestingApp();
        Flow flow = instance.createRoute().flow(acSys, acMaterializer);
        http.bindAndHandle(flow, ConnectHttp.toHost(HOST, PORT), acMaterializer);
    }

    private Route createRoute() {
        return concat(
                get(() -> parameter(PACKAGE_ID, (id) -> {
                    scala.concurrent.Future<Object> res = Patterns.ask(router, id, TIMEOUT);
                    return completeOKWithFuture(res, Jackson.marshaller());
                })),
                post(() ->
                        entity(Jackson.unmarshaller(TestPackage.class), testPack -> {
                            router.tell(testPack, ActorRef.noSender());
                            return complete(OK);
                        }))
        );
    }

}