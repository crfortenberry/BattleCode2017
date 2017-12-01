package FortenBot;
import battlecode.common.*;

public class Gardener extends Robot {
    private int maxSoldiers = 24;
    public void onAwake() {

    }

    public void onUpdate() {
        boolean settled = false;
        Direction gardenerDir = null;

        while (true) {
            try {
                Direction direction = randomDirection();
                if(gardenerDir == null) {
                    gardenerDir = direction;
                }

                //settle down
                if (!settled && !(robotController.isCircleOccupiedExceptByThisRobot(robotController.getLocation(), robotType.bodyRadius * 5.0f))) {
                    settled = true;
                }

                //build a soldier
                if (robotController.readBroadcastInt(2) < maxSoldiers/2 ||
                        (robotController.readBroadcastInt(2) < maxSoldiers*3/4 && Math.random() < .008) ||
                        (robotController.readBroadcastInt(2) < maxSoldiers && Math.random() < .003)) {
                    if (robotController.canBuildRobot(RobotType.SOLDIER, gardenerDir)) {
                        robotController.buildRobot(RobotType.SOLDIER, gardenerDir);
                    } else if (robotController.canBuildRobot(RobotType.SOLDIER, gardenerDir.opposite())) {
                        robotController.buildRobot(RobotType.SOLDIER, gardenerDir.opposite());
                        System.out.println("Made a soldier behind");
                    }
                }

                //plant a tree
                TreeInfo[] trees = robotController.senseNearbyTrees(robotType.bodyRadius * 2, myTeam);
                if (settled) {
                    if (trees.length < 5) {
                        tryFarm(gardenerDir);
                    } else if (robotController.readBroadcastInt(2) >= maxSoldiers && robotController.canPlantTree(gardenerDir.opposite())) {
                        robotController.plantTree(gardenerDir.opposite());
                    }
                }

                //care for trees
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

    /**
     * Attempts to farm in a given direction, while searching in a circle for a suitable place.
     *
     * @param dir The intended direction to farm
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryFarm(Direction dir) throws GameActionException {
        return tryFarm(dir,60,2);
    }

    /**
     * Attempts to farm in a given direction, while searching in a circle for a suitable place.
     *
     * @param dir The intended direction to farm
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryFarm (Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (robotController.canPlantTree(dir)) {
            robotController.plantTree(dir);
            return true;
        }

        // Now try a bunch of similar angles
        //boolean planted = false;
        int currentCheck = 1;

        while (currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if (robotController.canPlantTree(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                robotController.plantTree(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if (robotController.canPlantTree(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                robotController.plantTree(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // Nothing was planted, so return false.
        return false;
    }

    /**
     * Attempts to build a robot in a given direction, while searching in a circle for a suitable place.
     *
     * @param dir The intended direction to build
     * @return true if a build was performed
     * @throws GameActionException
     */
    static boolean tryBuild(Direction dir) throws GameActionException {
        return tryBuild(dir,10,2);
    }

    /**
     * Attempts to build a robot in a given direction, while searching in a circle for a suitable place.
     *
     * @param dir The intended direction to build
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a build was performed
     * @throws GameActionException
     */
    static boolean tryBuild (Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
        System.out.println("Attempting to build a soldier");
        // First, try intended direction
        if (robotController.canBuildRobot(RobotType.SOLDIER, dir)) {
            robotController.buildRobot(RobotType.SOLDIER, dir);
            return true;
        }

        // Now try a bunch of similar angles
        //boolean built = false;
        int currentCheck = 1;

        while (currentCheck<=checksPerSide) {
            System.out.println("Soldier build check " + currentCheck);
            // Try the offset of the left side
            if (robotController.canBuildRobot(RobotType.SOLDIER, dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                robotController.buildRobot(RobotType.SOLDIER, dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if (robotController.canBuildRobot(RobotType.SOLDIER, dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                robotController.buildRobot(RobotType.SOLDIER, dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // Nothing was planted, so return false.
        return false;
    }
}
