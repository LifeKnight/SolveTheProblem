package com.lifeknight.solvetheproblem.mod;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class LifeKnightThreadFactory implements ThreadFactory
{
    private final AtomicInteger threadNumber;
    
    public LifeKnightThreadFactory() {
        this.threadNumber = new AtomicInteger(1);
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        return new Thread(r, "LifeKnightThread" + this.threadNumber.getAndIncrement());
    }
}
