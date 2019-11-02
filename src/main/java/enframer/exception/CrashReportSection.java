package enframer.exception;

import java.util.ArrayList;
import java.util.List;

public class CrashReportSection {
    private List<Entry> entries;
    private String name;

    public CrashReportSection(String name) {
        entries = new ArrayList<>();
        this.name = name;
    }

    public void addDetail(ICrashReportDetail<?> detail) {
        addDetail(null, detail);
    }

    public void addDetail(String name, ICrashReportDetail<?> detail) {
        entries.add(new Entry(name, detail));
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("=== " + name + " ===" + (!entries.isEmpty() ? "\n" : ""));
        for (int i = 0; i < entries.size(); i++) {
            if (i != 0) {
                out.append("\n");
            }

            Entry entry = entries.get(i);
            out.append(entry.toString());
        }
        return out.toString();
    }

    private class Entry {
        private String name;
        private ICrashReportDetail<?> detail;

        private Entry(String name, ICrashReportDetail<?> detail) {
            this.name = name;
            this.detail = detail;
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            if (name != null) {
                out.append(name).append(": ");
            }

            String detailStr = detail.call().toString();
            if (!detailStr.contains("\n")) {
                out.append(detailStr);
            } else {
                String[] splitted = detailStr.split("\n");
                for (int i = 0; i < splitted.length; i++) {
                    if (i == 0) {
                        if (name != null) {
                            out.append("\n");
                        }
                    } else {
                        out.append("\n");
                    }

                    out.append(splitted[i]);
                }
            }

            return out.toString();
        }
    }
}
