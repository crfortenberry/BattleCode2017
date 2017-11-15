package FortenBot;
import battlecode.common.*;

public class Soldier extends Robot {
    public void onUpdate() {
        boolean settled = false;
        Direction soldierDir = null;

        while (true) {
            try {
                Direction direction = randomDirection();
                if(soldierDir == null) {
                    soldierDir = direction;
                }

                //look for something to shoot
                RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, enemy);
                if (enemyRobots.length > 0) {
                    settled = true;
                    if (robotController.canFireTriadShot()) {
                        robotController.fireTriadShot(robotController.getLocation().directionTo(enemyRobots[0].location));
                    }
                } else if (settled) {
                    TreeInfo[] enemyTrees = robotController.senseNearbyTrees(-1, enemy);
                    if (enemyTrees.length > 0){
                        if (robotController.canFireTriadShot()) {
                            robotController.fireTriadShot(robotController.getLocation().directionTo((enemyTrees[0].location)));
                        }
                    } else {
                        settled = false;
                    }
                }

                //move around
                if (!settled) {
                    if(tryMove(soldierDir)) {
                        System.out.println("Gardener moved");
                    } else {
                        soldierDir = randomDirection();
                        tryMove(soldierDir);
                    }
                }
                Clock.yield();
            }catch (Exception e) {
                System.out.println("Exception in Soldier");
                e.printStackTrace();
            }
        }
    }
}
