package com.github.burgerguy.recordable.client.score;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FutureScore {

    private final AtomicBoolean requested;
    private final AtomicReference<Score> scoreReference;

    public FutureScore() {
        this.requested = new AtomicBoolean(false);
        this.scoreReference = new AtomicReference<>(null);
    }

    /**
     * If the score hadn't already been requested, it becomes
     * requested after this is called.
     *
     * @return if the score needs to be requested
     */
    public boolean request() {
        return !this.requested.getAndSet(true);
    }

    public void setScore(Score score) {
        this.scoreReference.lazySet(score);
    }

    /**
     * Returns null if the score hasn't been requested, or if
     * the request hasn't finished.
     */
    public Score getScoreOrNull() {
        return this.scoreReference.get();
    }
}
