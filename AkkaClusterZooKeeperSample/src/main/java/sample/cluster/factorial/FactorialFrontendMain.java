package sample.cluster.factorial;

import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.seed.ZookeeperClusterSeed;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class FactorialFrontendMain {

    public static void main(String[] args) {
        final int upToN = 200;

        final Config config = ConfigFactory.parseString(
                "akka.cluster.roles = [frontend]").withFallback(
                ConfigFactory.load("factorial"));

        final ActorSystem system = ActorSystem.create("ClusterSystem", config);
        new ZookeeperClusterSeed((ExtendedActorSystem)system).join();
        system.log().info(
                "Factorials will start when 2 backend members in the cluster.");
        //#registerOnUp
        Cluster.get(system).registerOnMemberUp(new Runnable() {
            @Override
            public void run() {
                system.actorOf(Props.create(FactorialFrontend.class, upToN, true),
                        "factorialFrontend");
            }
        });
        //#registerOnUp
    }

}