package com.bensmann.ferchau.robot;

public class HorizontalThread extends AbstractMoveThread {

    @Override
    public void run() {
        while (work.get()) {
            int r = random.nextInt(100);
            if (r < 50) {
                System.out.println("left");
            } else {
                System.out.println("right");
            }
            Thread.yield();
            try {
                Thread.sleep(random.nextInt(3) * 1000);
            } catch (Exception e) {
                // ignore
            }
        }
    }

}
