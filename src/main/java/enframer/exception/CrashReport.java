package enframer.exception;

import enframer.Enframer;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CrashReport {
    private Throwable cause;
    @Nullable
    private String description;

    private CrashReportSection causeInfo;
    private CrashReportSection systemInfo;

    private CrashReport(Throwable cause, @Nullable String description) {
        this.cause = cause;
        this.description = description;

        causeInfo = new CrashReportSection("Информация об ошибке");
        systemInfo = new CrashReportSection("Информация о системе");
        genCauseInfo();
        genSystemInfo();
    }

    public static CrashReport makeCrashReport(Throwable cause) {
        return makeCrashReport(cause, null);
    }

    public static CrashReport makeCrashReport(Throwable cause, @Nullable String description) {
        if (cause instanceof ReportedException) {
            return ((ReportedException) cause).getCrashReport();
        } else {
            return new CrashReport(cause, description);
        }
    }

    public Throwable getCause() {
        return cause;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    private void genCauseInfo() {
        causeInfo.addDetail("Время", () -> new SimpleDateFormat().format(new Date()));
        causeInfo.addDetail(description, (ICrashReportDetail<String>) () -> {
            StringBuilder out = new StringBuilder();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            getCause().printStackTrace(pw);
            String sStackTrace = sw.toString();
            out.append(sStackTrace);

            return out.toString();
        });
    }

    private void genSystemInfo() {
        systemInfo.addDetail("Версия", () -> Enframer.VERSION);
        systemInfo.addDetail("Операционная система", (ICrashReportDetail<String>) () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + "), версия - " + System.getProperty("os.version"));
        systemInfo.addDetail("Версия Java", (ICrashReportDetail<String>) () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        systemInfo.addDetail("Версия Java VM", (ICrashReportDetail<String>) () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        systemInfo.addDetail("Память", (ICrashReportDetail<String>) () -> {
            Runtime runtime = Runtime.getRuntime();
            long i = runtime.maxMemory();
            long j = runtime.totalMemory();
            long k = runtime.freeMemory();
            long l = i / 1024L / 1024L;
            long i1 = j / 1024L / 1024L;
            long j1 = k / 1024L / 1024L;
            return k + " байт (" + j1 + " MB) / " + j + " байт (" + i1 + " MB), макс.: " + i + " байт (" + l + " MB)";
        });
        systemInfo.addDetail("Флаги JVM", (ICrashReportDetail<String>) () -> {
            RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
            List<String> list = runtimemxbean.getInputArguments();
            int i = 0;
            StringBuilder stringbuilder = new StringBuilder();

            for (String s : list) {
                if (s.startsWith("-X")) {
                    if (i++ > 0) {
                        stringbuilder.append(" ");
                    }

                    stringbuilder.append(s);
                }
            }

            return String.format("%d всего; %s", i, stringbuilder.toString());
        });
    }

    @Override
    public String toString() {
        return causeInfo + "\n\n" + systemInfo;
    }
}
