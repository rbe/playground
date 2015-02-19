package com.bensmann.ferchau.robot;

public class Robot {

    public static void main(String[] args) {
        AbstractMoveThread hor = new HorizontalThread();
        hor.start();
        AbstractMoveThread ver = new VerticalThread();
        ver.start();
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            // ignore
        }
        hor.stopWork();
        ver.stopWork();
    }

}
