package FortenBot;
import battlecode.common.*;

public class Archon extends Robot {
    @Override
    public void onUpdate() {
        int numberOfGardeners = 0;
        int numberOfSoldiers = 0;
        while (true) {
            try{
                Direction direction = randomDirection();
                if (robotController.canHireGardener(direction) && numberOfGardeners < 10) {
                    robotController.hireGardener(direction);
                    System.out.println("number of gardeners: "+ numberOfGardeners);
                    numberOfGardeners++;
                } else if (robotController.canHireGardener(direction) && numberOfGardeners < 16 && Math.random() < .01) {
                    robotController.hireGardener(direction);
                    System.out.println("number of gardeners: "+ numberOfGardeners);
                    numberOfGardeners++;
                }

                numberOfSoldiers = numberOfSoldiers + robotController.readBroadcastInt(1);
                robotController.broadcastInt(2, numberOfSoldiers);

                if (robotController.getTeamBullets() > 1000) {
                    robotController.donate(robotController.getTeamBullets()- 700);
                }
                tryMove(direction);
                Clock.yield();
            } catch (Exception e) {
                System.out.println("Exception in Archon");
                e.printStackTrace();
            }
        }
    }
}
