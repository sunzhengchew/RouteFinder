module com.example.routefinder {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires jmh.core;

    opens com.example.routefinder to javafx.fxml;
    exports com.example.routefinder;
    exports com.example.routefinder.BenchMark;
    opens com.example.routefinder.BenchMark to javafx.fxml;
    opens com.example.routefinder.BenchMark.jmh_generated to jmh.core;
}