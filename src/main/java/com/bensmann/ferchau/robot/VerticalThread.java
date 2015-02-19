package com.bensmann.ferchau.robot;

public class VerticalThread extends AbstractMoveThread {

    @Override
    public void run() {
        while (work.get()) {
            int r = random.nextInt(100);
            if (r < 50) {
                System.out.println("up");
            } else {
                System.out.println("down");
            }
            Thread.yield();
            try {
                Thread.sleep(random.nextInt(2) * 1000);
            } catch (Exception e) {
                // ignore
            }
        }
    }

}
