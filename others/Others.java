package others;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lawyer.Lawyers;
import processes.Processes;

public class Others {
    public static void callError(Stage stage, String text) {
        Label error = new Label(text);
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(error);
        Scene secondScene = new Scene(secondaryLayout, 230, 100);
        Stage newWindow = new Stage();
        newWindow.setTitle("Error!");
        newWindow.setScene(secondScene);
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.initOwner(stage);
        newWindow.setX(stage.getX() + 385);
        newWindow.setY(stage.getY() + 250);
        newWindow.show();
    }

    public static void showLoseProcesses(Stage stage, ObservableList<Lawyers> lawyers, ObservableList<Processes> processesLost) {
        TableView<Processes> lostProc = new TableView();

        lostProc.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Processes, Integer> processID = new TableColumn<>("ID");
        TableColumn<Processes, String> processLawyerSur = new TableColumn<Processes, String>("Lawyers Surname");
        TableColumn<Processes, String> processRole = new TableColumn<Processes, String>("Lawyer Role");
        TableColumn<Processes, String> processDescription = new TableColumn<>("Description");
        TableColumn<Processes, Integer> processCostFor1Day = new TableColumn<>("Cost");
        processLawyerSur.setCellValueFactory(new PropertyValueFactory<Processes, String>("lawyerSur"));
        processRole.setCellValueFactory(new PropertyValueFactory<Processes, String>("lawyerRole"));
        processDescription.setCellValueFactory(new PropertyValueFactory<Processes, String>("description"));
        processCostFor1Day.setCellValueFactory(new PropertyValueFactory<Processes, Integer>("dayCost"));
        processID.setCellValueFactory(new PropertyValueFactory<Processes, Integer>("currID"));
        GridPane.setHgrow(lostProc, Priority.ALWAYS);
        GridPane.setVgrow(lostProc, Priority.ALWAYS);
        GridPane.setMargin(lostProc, new Insets(20));
        lostProc.getColumns().addAll(processID, processLawyerSur, processRole, processDescription, processCostFor1Day);

        GridPane root = new GridPane();
        StackPane hb = new StackPane();

        root.setHgap(20);
        root.setVgap(20);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        root.getColumnConstraints().add(col1);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(80);
        root.getRowConstraints().add(row1);

        RowConstraints row2 = new RowConstraints();
        row1.setPercentHeight(20);
        root.getRowConstraints().add(row2);

        ComboBox<Lawyers> showLawyers = new ComboBox<>(lawyers);
        showLawyers.setPromptText("Choose");
        showLawyers.setMinSize(251, 45);
        showLawyers.setMaxSize(251, 45);

        hb.getChildren().add(showLawyers);
        ObservableList<Processes> lostProcForLaw = FXCollections.observableArrayList();

        showLawyers.setOnAction(e -> {
            lostProcForLaw.clear();
            for (int i = 0; i < processesLost.size(); i++) {
                if (processesLost.get(i).getLawyerSur().equals(showLawyers.getSelectionModel().getSelectedItem().getSur())) {
                    lostProcForLaw.add(processesLost.get(i));
                }
            }
            lostProc.setItems(lostProcForLaw);
        });

        root.add(hb, 0, 0);
        root.add(lostProc, 0, 1);


        Scene secondScene = new Scene(root, 500, 400);

        //secondScene.getStylesheets().add(getClass().getResource("darkStyle.css").toString());
        //secondScene.getStylesheets().add(getClass().getResource("lightStyle.css").toString());

        Stage newWindow = new Stage();
        newWindow.setTitle("Lost processes");
        newWindow.setScene(secondScene);
        newWindow.initModality(Modality.NONE);
        newWindow.initOwner(stage);
        newWindow.setX(stage.getX() + 250);
        newWindow.setY(stage.getY() + 100);
        newWindow.show();
    }

}
