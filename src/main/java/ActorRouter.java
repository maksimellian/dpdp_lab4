import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

public class ActorRouter extends AbstractActor {
    private ActorRef acStorage;
    private ActorRef acPool;
    private final int WORKERS_NUM = 5;

    public ActorRouter(ActorSystem acSys) {
        acStorage = acSys.actorOf(Props.create(ActorStorage.class));
        acPool = acSys.actorOf(new RoundRobinPool(WORKERS_NUM).props(Props.create(ActorTester.class, acStorage)));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestPackage.class, test -> runTests(test))
                .match(String.class, test -> acStorage.forward(test, getContext()))
                .build();
    }

    private void runTests(TestPackage testPack) {
        for (Test test : testPack.getTests()){
            test.setTestPackage(testPack);
            acPool.tell(test, ActorRef.noSender());
        }
    }
}