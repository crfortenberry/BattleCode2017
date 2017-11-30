package FortenBot;
import battlecode.common.*;

public class Soldier extends Robot {
    @Override
    public void onAwake() {
        try {
            robotController.broadcastInt(1,1);
            Clock.yield();
            robotController.broadcastInt(1,0);
        } catch (Exception e) {
            System.out.println("Exception in Soldier");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate() {
        boolean targeting = false;
        Direction soldierMoveDir = null;
        Direction soldierAimDir = null;

        try {
            soldierMoveDir = getEnemyArchonDirection();
        } catch (Exception e) {
            System.out.println("Exception in Soldier");
            e.printStackTrace();
        }

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
                    soldierMoveDir = robotController.getLocation().directionTo(enemyRobots[0].location).rotateLeftRads(1.3f);
                    tryMove(soldierMoveDir);
                    soldierAimDir = robotController.getLocation().directionTo(enemyRobots[0].location);
                } else if (targeting) {
                    TreeInfo[] enemyTrees = robotController.senseNearbyTrees(-1, enemy);
                    if (enemyTrees.length > 0){
                        soldierMoveDir = robotController.getLocation().directionTo((enemyTrees[0].location));
                        if (Math.random() < .1){
                            tryMove(soldierMoveDir);
                        }
                        soldierAimDir = soldierMoveDir;
                    } else {
                        targeting = false;
                        soldierMoveDir = soldierAimDir;
                    }
                }

                //shoot the thing
                if (targeting) {
                    if (robotController.canFireTriadShot()) {
                        robotController.fireTriadShot(soldierAimDir);
                    } else if (robotController.canFireSingleShot()) {
                        robotController.fireSingleShot(soldierAimDir);
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
            } catch (Exception e) {
                System.out.println("Exception in Soldier");
                e.printStackTrace();
            }
        }
    }
}
