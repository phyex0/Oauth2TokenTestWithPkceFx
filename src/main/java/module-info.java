module com.phyex.oauth2tokentestwithpkcefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires oauth2.oidc.sdk;
    requires java.net.http;
    requires javafx.swing;

    opens com.phyex.oauth2tokentestwithpkcefx to javafx.fxml;
    exports com.phyex.oauth2tokentestwithpkcefx;
    exports com.phyex.oauth2tokentestwithpkcefx.pages;
    opens com.phyex.oauth2tokentestwithpkcefx.pages to javafx.fxml;
}