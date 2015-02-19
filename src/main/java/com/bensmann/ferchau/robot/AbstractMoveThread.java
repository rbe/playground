package com.bensmann.ferchau.robot;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMoveThread extends Thread {

    protected final AtomicBoolean work = new AtomicBoolean(true);

    protected final Random random = new Random();

    @Override
    public abstract void run();

    public void stopWork() {
        work.getAndSet(false);
    }

}
