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

package org.overrun.timer.test;

import org.overrun.timer.Timer;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Test {
    private static int gameTicks;

    private static void tick() {
        System.out.println("Game ticks: " + gameTicks);
        gameTicks++;
    }

    private static void onFPSChanged(int frames) {
        System.out.println("Game fps: " + frames);
    }

    public static void main(String[] args) throws InterruptedException {
        Timer timer = Timer.ofSystem(2.0);
        while (true) {
            timer.advanceTime();
            System.out.println("Game tick");
            timer.performTicks(Test::tick);
            System.out.println("Game update");
            System.out.println("Game render: " + timer.partialTick());
            timer.calcFPS(Test::onFPSChanged);
            // Simulate polling events.
            // 100ms = 0.1s, 1/0.1s = 10fps
            Thread.sleep(100);
        }
    }
}
