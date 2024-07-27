package hr.algebra.insurancebackend.constants;

public class Constants {

    private Constants(){}
    public static final class ReportControllerEndpoints {
        private ReportControllerEndpoints(){}
        public static final String REPORT = "report";
        public static final String REJECTED = "/rejected";
        public static final String WAITING = "/waiting";
        public static final String ACCEPTED = "/accepted";
        public static final String ACCEPT_ID = "/accept/{id}";
        public static final String REJECT_ID = "/reject/{id}";
    }

    public static final class Methods {
        private Methods(){}
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String DELETE = "DELETE";
    }

    public static final class Metrics {
        private Metrics() {}
        public static final String HTTP_REQUESTS = "http.requests";
        public static final String METHOD = "method";
        public static final String ENDPOINT = "endpoint";
        public static final String ERRORS_SUFFIX = ".errors";
    }
}
