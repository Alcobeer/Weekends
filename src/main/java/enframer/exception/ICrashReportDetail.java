package enframer.exception;

@FunctionalInterface
public interface ICrashReportDetail<T> {
    T call();
}
