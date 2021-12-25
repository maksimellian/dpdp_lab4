
import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ActorTester extends AbstractActor {
    private ActorRef acStorage;
    private String execute(Test test) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(test.getPackage().getScript());
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction(test.getPackage().getFunctionName(), test.getParams()).toString();
    }

    public ActorTester(){ }

    public ActorTester(ActorRef storActor){
        acStorage = storActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Test.class, test -> acStorage.tell(checkRes(test), ActorRef.noSender()))
                .build();
    }

    private Test checkRes(Test test) throws ScriptException, NoSuchMethodException {
        String getRes = execute(test);
        test.setRealResult(getRes);
        return test;
    }
}