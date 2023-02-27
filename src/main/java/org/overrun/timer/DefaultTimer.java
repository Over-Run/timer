/*
 * MIT License
 *
 * Copyright (c) 2023 Overrun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package org.overrun.timer;

import java.util.function.IntConsumer;

/**
 * The default timer.
 *
 * @author squid233
 * @since 0.1.0
 */
public abstract class DefaultTimer implements Timer {
    private final double ticksPerSecond;
    private final int defaultMaxTickCount;
    private double lastTime = currentTime();
    private double passedTime = 0.0;
    private double deltaTime = 0.0;
    private double partialTick = 0.0;
    private double timescale = 1.0;
    private int tickCount = 0;
    private int maxTickCount;
    private double accumTime = currentTime();
    private int frames = 0;

    /**
     * Creates the default timer with the given ticks per second.
     *
     * @param ticksPerSecond the ticks per second.
     */
    public DefaultTimer(double ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
        defaultMaxTickCount = (int) (5 * ticksPerSecond);
        maxTickCount = defaultMaxTickCount;
    }

    @Override
    public abstract double currentTime();

    @Override
    public double ticksPerSecond() {
        return ticksPerSecond;
    }

    @Override
    public double deltaTime() {
        return deltaTime;
    }

    @Override
    public int framesPerSecond() {
        return frames;
    }

    @Override
    public double partialTick() {
        return partialTick;
    }

    @Override
    public double timescale() {
        return timescale;
    }

    @Override
    public int tickCount() {
        return tickCount;
    }

    @Override
    public int maxTickCount() {
        return maxTickCount;
    }

    @Override
    public void setTimescale(double timescale) {
        this.timescale = timescale;
    }

    @Override
    public void setMaxTickCount(int maxTickCount) {
        this.maxTickCount = maxTickCount > 0 ? maxTickCount : defaultMaxTickCount;
    }

    @Override
    public void advanceTime() {
        // Update
        double currTime = currentTime();
        double delta = currTime - lastTime;
        lastTime = currTime;

        deltaTime = delta;
        // Clamp delta to [0.0, 1.0]
        if (delta < 0.0) delta = 0.0;
        else if (delta > 1.0) delta = 1.0;
        passedTime += delta * timescale() * ticksPerSecond();
        tickCount = (int) passedTime;
        // Clamp tickCount to [0, maxTickCount]
        if (tickCount < 0) tickCount = 0;
        else {
            final int count = maxTickCount();
            if (tickCount > count) tickCount = count;
        }
        passedTime -= tickCount;
        partialTick = passedTime;
    }

    @Override
    public void calcFPS(IntConsumer action) {
        frames++;
        while (currentTime() >= accumTime + 1.0) {
            action.accept(frames);
            accumTime += 1.0;
            frames = 0;
        }
    }
}
