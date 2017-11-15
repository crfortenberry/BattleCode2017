package FortenBot;
import battlecode.common.*;

public class Soldier extends Robot {
    public void onUpdate() {
        boolean targeting = false;
        Direction soldierMoveDir = null;
        Direction soldierAimDir = null;

        while (true) {
            try {
                Direction direction = randomDirection();
                if(soldierMoveDir == null) {
                    soldierMoveDir = direction;
                }

                //look for something to shoot
                RobotInfo[] enemyRobots = robotController.senseNearbyRobots(-1, enemy);
                if (enemyRobots.length > 0) {
                    targeting = true;
                    soldierAimDir = robotController.getLocation().directionTo(enemyRobots[0].location);
                    soldierMoveDir = soldierAimDir.rotateLeftRads(1.3f);
                    tryMove(soldierMoveDir);
                    if (robotController.canFireTriadShot()) {
                        robotController.fireTriadShot(soldierAimDir);
                    } else if (robotController.canFireSingleShot()) {
                        robotController.fireSingleShot(soldierAimDir);
                    }
                } else if (targeting) {
                    TreeInfo[] enemyTrees = robotController.senseNearbyTrees(-1, enemy);
                    if (enemyTrees.length > 0){
                        soldierAimDir = robotController.getLocation().directionTo((enemyTrees[0].location));
                        if (robotController.canFireTriadShot()) {
                            robotController.fireTriadShot(soldierAimDir);
                        } else if (robotController.canFireSingleShot()) {
                            robotController.fireSingleShot(soldierAimDir);
                        }
                    } else {
                        targeting = false;
                        soldierMoveDir = soldierAimDir;
                    }
                }

                //move around
                if (!targeting) {
                    if(tryMove(soldierMoveDir)) {
                        System.out.println("Soldier moved");
                    } else {
                        soldierMoveDir = randomDirection();
                        tryMove(soldierMoveDir);
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
