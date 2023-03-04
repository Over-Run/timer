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

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;

/**
 * The base timer.
 *
 * <h2>Ticking</h2>
 * The timer supports to perform ticking (a.k.a fixed updating) at a fixed count.
 * <p>
 * You can use {@link #performTicks(Runnable)} to tick your application or game.
 * <pre>{@code
 * Timer timer = ...;
 * while (running) {
 *     timer.advanceTime();
 *     timer.performTicks(this::tick);
 *     update();
 *     render();
 *     timer.calcFPS(this::onFPSChanged);
 * }
 * }</pre>
 *
 * @author squid233
 * @since 0.1.0
 */
public interface Timer {
    /**
     * Creates a default timer with the given ticks per second and time getter.
     *
     * @param ticksPerSecond the ticks per second.
     * @param getter         the time getter that returns a double value in seconds.
     * @return the timer.
     */
    static DefaultTimer ofGetter(double ticksPerSecond, DoubleSupplier getter) {
        Objects.requireNonNull(getter);
        return new DefaultTimer(ticksPerSecond) {
            @Override
            public double currentTime() {
                return getter.getAsDouble();
            }
        };
    }

    /**
     * Creates a default timer with the given ticks per second using {@link System#nanoTime()}.
     *
     * @param ticksPerSecond the ticks per second.
     * @return the timer.
     */
    static DefaultTimer ofSystem(double ticksPerSecond) {
        return ofGetter(ticksPerSecond, () -> System.nanoTime() * 1.0E-9);
    }

    /**
     * Gets the current time in seconds.
     *
     * @return the current time.
     */
    double currentTime();

    /**
     * Gets the ticks per second.
     *
     * @return the ticks per second.
     */
    double ticksPerSecond();

    /**
     * Gets the interval time between two frames.
     *
     * @return the delta time.
     */
    double deltaTime();

    /**
     * Gets the frames per second.
     *
     * @return the frames per second.
     */
    int framesPerSecond();

    /**
     * Gets the partial time between two ticks. This value can be used for linear interpolation.
     *
     * @return the partial tick.
     */
    double partialTick();

    /**
     * Gets the timescale.
     *
     * @return the timescale.
     * @see #setTimescale(double)
     */
    double timescale();

    /**
     * Gets the tick count in the current loop.
     *
     * @return the tick count.
     */
    int tickCount();

    /**
     * Gets the max tick count that can be performed in the current loop.
     *
     * @return the max tick count.
     * @see #setMaxTickCount(int)
     */
    int maxTickCount();

    /**
     * Sets the timescale. You can set it to 0 to pause, 2 to get 2x speed, etc.
     *
     * @param timescale the timescale.
     * @see #timescale()
     */
    void setTimescale(double timescale);

    /**
     * Sets the max tick count that can be performed in the current loop.
     *
     * @param maxTickCount the max tick count.
     * @see #maxTickCount()
     */
    void setMaxTickCount(int maxTickCount);

    /**
     * Advances this timer.
     */
    void advanceTime();

    /**
     * Calculates the FPS and performs the given action.
     *
     * @param action the action to be performed when FPS is updated.
     * @see #calcFPS()
     */
    void calcFPS(@Nullable IntConsumer action);

    /**
     * Calculates the FPS.
     *
     * @see #calcFPS(IntConsumer)
     */
    default void calcFPS() {
        calcFPS(null);
    }

    /**
     * Performs the given action with the {@linkplain #tickCount() tick count}.
     *
     * @param action the action to be performed.
     */
    default void performTicks(Runnable action) {
        if (action != null) {
            for (int i = 0, count = tickCount(); i < count; i++) {
                action.run();
            }
        }
    }
}
