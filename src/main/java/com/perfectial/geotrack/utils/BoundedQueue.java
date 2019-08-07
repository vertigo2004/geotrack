package com.perfectial.geotrack.utils;

import java.util.concurrent.ArrayBlockingQueue;

public class BoundedQueue<E> extends ArrayBlockingQueue<E> {
    private static final long serialVersionUID = -1L;

    public BoundedQueue(int limit) {
        super(limit, true);
    }

    @Override
    public boolean add(E object) {

        while (remainingCapacity() <= 0) {
            super.poll();
        }
        return super.add(object);
    }
}
