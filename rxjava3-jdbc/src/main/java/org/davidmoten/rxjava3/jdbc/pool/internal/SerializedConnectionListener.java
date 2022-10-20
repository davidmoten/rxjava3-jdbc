package org.davidmoten.rxjava3.jdbc.pool.internal;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.queue.MpscLinkedQueue;
import io.reactivex.rxjava3.operators.SimplePlainQueue;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

@SuppressWarnings("serial")
public final class SerializedConnectionListener extends AtomicInteger
        implements Consumer<Optional<Throwable>> {

    private final Consumer<? super Optional<Throwable>> c;
    private final SimplePlainQueue<Optional<Throwable>> queue = new MpscLinkedQueue<>();

    public SerializedConnectionListener(Consumer<? super Optional<Throwable>> c) {
        this.c = c;
    }

    @Override
    public void accept(Optional<Throwable> error) throws Exception {
        queue.offer(error);
        drain();
    }

    private void drain() {
        if (getAndIncrement() == 0) {
            int missed = 1;
            while (true) {
                Optional<Throwable> o;
                while ((o = queue.poll()) != null) {
                    try {
                        c.accept(o);
                    } catch (Throwable e) {
                        RxJavaPlugins.onError(e);
                    }
                }
                missed = addAndGet(-missed);
                if (missed == 0) {
                    return;
                }
            }
        }
    }
}
