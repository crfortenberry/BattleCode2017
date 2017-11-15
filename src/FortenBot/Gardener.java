package FortenBot;
import battlecode.common.*;

public class Gardener extends Robot {
    public void onUpdate() {
        boolean settled = false;
        Direction gardenerDir = null;
        int numberOfTrees = 0;

        while (true) {
            try {
                Direction direction = randomDirection();
                if(gardenerDir == null) {
                    gardenerDir = direction;
                }

                //settle down
                if (!(robotController.isCircleOccupiedExceptByThisRobot(robotController.getLocation(), robotType.bodyRadius * 5.0f)) && !settled) {
                    settled = true;
                    if (robotController.canBuildRobot(RobotType.SOLDIER, gardenerDir)) {
                        robotController.buildRobot(RobotType.SOLDIER, gardenerDir);
                        robotController.broadcastInt(1, 1);
                    } else if (robotController.canPlantTree(gardenerDir)) {
                        robotController.plantTree(gardenerDir);
                        numberOfTrees++;
                    }
                }

                //build a soldier or plant a tree
                if (settled) {
                    if (robotController.canBuildRobot(RobotType.SOLDIER, gardenerDir) && robotController.readBroadcastInt(2) < 16) {
                        robotController.buildRobot(RobotType.SOLDIER, gardenerDir);
                        System.out.println("Made a soldier");
                    } else if (robotController.canPlantTree(direction) && numberOfTrees < 3) {
                        robotController.plantTree(direction);
                        numberOfTrees++;
                        System.out.println("Number of trees: " + numberOfTrees);
                    }
                }

                //care for trees
                TreeInfo[] trees = robotController.senseNearbyTrees(robotType.bodyRadius * 2, myTeam);
                TreeInfo minHealthTree = null;
                for (TreeInfo tree : trees) {
                    if (tree.health < 70) {
                        if (minHealthTree == null || tree.health < minHealthTree.health) {
                            minHealthTree = tree;
                        }
                    }
                }
                if (minHealthTree != null) {
                    robotController.water(minHealthTree.ID);
                }

                //find a place to settle
                if (!settled) {
                    if(tryMove(gardenerDir)) {
                        System.out.println("Gardener moved");
                    } else {
                        gardenerDir = randomDirection();
                        tryMove(gardenerDir);
                    }
                }
                Clock.yield();
            }catch (Exception e) {
                System.out.println("Exception in Gardener");
                e.printStackTrace();
            }
        }
    }
}
