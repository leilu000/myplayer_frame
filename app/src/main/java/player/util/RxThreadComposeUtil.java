package player.util;


import io.reactivex.Scheduler;

/**
 * Created by LIWUJUN on 2018/1/22.
 */

public class RxThreadComposeUtil {
    /**
     * 主要用来切换线程
     *
     * @param <T>
     * @return
     */
    public static <T> NiMoThreadTransformer<T> applySchedulers() {
        return new NiMoThreadTransformer<>();
    }

    public static <T> NiMoThreadTransformer<T> applySchedulers(final Scheduler subscribeOnScheduler,
                                                               final Scheduler observeOnScheduler) {
        return new NiMoThreadTransformer<>(subscribeOnScheduler, observeOnScheduler);
    }
}
