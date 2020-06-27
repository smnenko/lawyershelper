package base;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;
import lawyer.control.Lawyers;
import processes.control.Processes;
import others.Others;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;

/**
 * Name of the app: Lawyer Helper v0.1a
 *
 * Task: Create the law office coordination system. All information is stored in two files:
 * lawyers.txt should contains id, surname, specialization and cost per hour of work strings
 * processes.txt should contains id, description, status, cost per day of work and lawyers id's who working on project strings
 * The app should has these features:
 * add lawyer
 * change the cost per hour of work
 * add process (status = "in discussion", one lawyers can't work on 2 or more processes (only 1), calculate cost per day (lawyers works 8, l. assisnant - 6, consultant - 4 hours))
 * change process status to "in process"
 * close process (set status to "win" or "lose")
 * find free lawyer by specialization (with percent of processes won)
 * output the all of current processes with list of lawyers and cost per day
 * find lawyers by surname
 * output the all of lost processes for selected lawyer
 *
 * Made by: Semenenko Stanislav Sergeevich
 * Faculty: Information Systems and Technologies
 * Group: IT-6 (3th semester)
 *
 * Date of beginning (dd:mm:yy): 14.12.2019
 * Date of issue (dd:mm:yy): 12.01.2020
 * Lecturer: Derkachenko Pavel
 *
 * Version: 0.1 alpha
 */


// main class of application
public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    // default method in javafx app
    @Override
    public void start(Stage stage) throws Exception {

        System.out.println("Application starts");
        setUserAgentStylesheet(STYLESHEET_CASPIAN);

        // import information from lawyers.txt and processes.txt
        inputFromFile();

        // create the table of lawyers
        tableLawyers.setEditable(true);
        tableLawyers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Lawyers, String> lawyersSur = new TableColumn<Lawyers, String>("Surname");
        TableColumn<Lawyers, String> lawyersSpec = new TableColumn<Lawyers, String>("Specialization");
        TableColumn<Lawyers, Integer> lawyersCost = new TableColumn<Lawyers, Integer>("Cost for 1 hour");

        lawyersCost.setMinWidth(60);
        lawyersSur.setMinWidth(60);
        lawyersSpec.setMinWidth(60);

        GridPane.setHgrow(tableLawyers, Priority.ALWAYS);
        GridPane.setVgrow(tableLawyers, Priority.ALWAYS);
        GridPane.setMargin(tableLawyers, new Insets(20));

        lawyersSur.setCellValueFactory(new PropertyValueFactory<Lawyers, String>("sur"));
        lawyersSpec.setCellValueFactory(new PropertyValueFactory<Lawyers, String>("spec"));
        lawyersCost.setCellValueFactory(new PropertyValueFactory<Lawyers, Integer>("cost"));
        lawyersCost.setCellFactory(TextFieldTableCell.<Lawyers, Integer>forTableColumn(new IntegerStringConverter()));
        tableLawyers.getColumns().addAll(lawyersSur, lawyersSpec, lawyersCost);

        controlLawyers.setSpacing(5);
        controlLawyers.getChildren().add(lawyerAdd);
        controlLawyers.getChildren().add(lawyerAddBox);
        controlLawyers.getChildren().add(findLawyerSpecializationBox);
        controlLawyers.getChildren().add(findLawyerSpecializationLabel);
        controlLawyers.getChildren().add(lostProc);

        lawyerAdd.setMinSize(251, 45);
        lawyerAdd.setMaxSize(251, 45);

        lawyerAddSur.setPromptText("Surname");
        lawyerAddSur.setMinSize(100, 30);
        lawyerAddSur.setMaxSize(100, 30);

        lawyerAddSpec.setPromptText("Specialize");
        lawyerAddSpec.setMinSize(106, 30);
        lawyerAddSpec.setMaxSize(106, 30);

        lawyerAddCost.setMinSize(38, 30);
        lawyerAddCost.setMaxSize(38, 30);
        lawyerAddCost.setPromptText("$");

        lawyerAddSpec.getItems().addAll("Lawyer", "Lawyer assistant", "Consultant");
        lawyerAddBox.getChildren().addAll(lawyerAddSur, lawyerAddSpec, lawyerAddCost);
        lawyerAddBox.setMaxSize(250, 38);
        lawyerAddBox.setSpacing(5);

        editCostField.setPromptText("Enter cost for 1 hour");
        editCostField.setMinSize(176, 20);
        editCostBox.getChildren().addAll(editCostField, editCost);
        editCostBox.setMaxSize(250, 38);
        editCostBox.setSpacing(5);

        findLawyerSpecializationBox.getChildren().addAll(findLawyerSpecializationCombo, findLawyerSpecializationLabel);
        findLawyerSpecializationBox.setMaxSize(250, 38);
        findLawyerSpecializationBox.setSpacing(5);

        findLawyerSpecializationCombo.getItems().addAll("Lawyer", "Lawyer assistant", "Consultant");
        findLawyerSpecializationCombo.setPromptText("Select...");

        lostProc.setMinSize(251, 45);
        lostProc.setMaxSize(251, 45);

        GridPane.setHgrow(controlLawyers, Priority.ALWAYS);
        GridPane.setVgrow(controlLawyers, Priority.ALWAYS);
        //GridPane.setMargin(controlLawyers, new Insets(20, 130, 20, 130));
        controlLawyers.setAlignment(Pos.CENTER_LEFT);

        // action on "Add lawyer" button
        lawyerAdd.setOnAction(e -> {
            if (!lawyerAddSur.getText().isEmpty() && !lawyerAddSpec.getSelectionModel().isEmpty() && !lawyerAddCost.getText().isEmpty()) {
                Lawyers l = new Lawyers(lawyerAddSur.getText(), lawyerAddSpec.getSelectionModel().getSelectedItem().toString(), Integer.parseInt(lawyerAddCost.getText()));
                lawyers.add(l);
                processAddLawyersList.add(l);
                lawyerAddSur.clear();
                lawyerAddSpec.getSelectionModel().clearSelection();
                lawyerAddCost.clear();
            } else {
                Others.callError(stage, "You must fill in all fields");
            }
        });

        // edit cost per hour in the table
        lawyersCost.setOnEditCommit((TableColumn.CellEditEvent<Lawyers, Integer> event) -> {
            TablePosition<Lawyers, Integer> pos = event.getTablePosition();

            int newCost = event.getNewValue();

            int row = pos.getRow();
            Lawyers l = event.getTableView().getItems().get(row);

            l.editCost(tableLawyers.getSelectionModel().getSelectedItem().getId(), newCost);
        });

        // action on find lawyer by specialization button
        findLawyerSpecializationCombo.setOnAction(e -> {
            findLawyerSpecializationLabel.setText("");
            for (int i = 0; i < processAddLawyersList.size(); i++) {
                double win = processAddLawyersList.get(i).getWinProcesses();
                double lose = processAddLawyersList.get(i).getLoseProcesses();
                double percent = Math.round((win / (win + lose)) * 100);
                if (processAddLawyersList.get(i).getSpec().equals(findLawyerSpecializationCombo.getValue())) {
                    if (Double.isNaN(percent)) {
                        percent = 0;
                    }
                    findLawyerSpecializationLabel.setText(findLawyerSpecializationLabel.getText() + processAddLawyersList.get(i).getSur() + " " + percent + "%\n");
                }
            }
        });

        // action on show lost processes button
        lostProc.setOnAction(e -> {
            Others.showLoseProcesses(stage, lawyers, lostProcesses);
        });

        // create the table of processes
        tableProcesses.setEditable(true);
        tableProcesses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Processes, Integer> processID = new TableColumn<>("ID");
        TableColumn<Processes, String> processLawyerSur = new TableColumn<Processes, String>("Surname");
        TableColumn<Processes, String> processRole = new TableColumn<Processes, String>("Role");
        TableColumn<Processes, String> processDescription = new TableColumn<>("Description");
        TableColumn<Processes, String> processStatus = new TableColumn<>("Status");
        TableColumn<Processes, String> processCostFor1Day = new TableColumn<>("Cost");
        GridPane.setHgrow(tableProcesses, Priority.ALWAYS);
        GridPane.setVgrow(tableProcesses, Priority.ALWAYS);
        GridPane.setMargin(tableProcesses, new Insets(20));

        processLawyerSur.setCellValueFactory(new PropertyValueFactory<Processes, String>("lawyerSur"));
        processRole.setCellValueFactory(new PropertyValueFactory<Processes, String>("lawyerRole"));
        processDescription.setCellValueFactory(new PropertyValueFactory<Processes, String>("description"));
        processStatus.setCellValueFactory(new PropertyValueFactory<Processes, String>("status"));
        processCostFor1Day.setCellValueFactory(new PropertyValueFactory<Processes, String>("dayCost"));
        processID.setCellValueFactory(new PropertyValueFactory<Processes, Integer>("currID"));

        editStatusCombo.add("In process");
        processStatus.setCellFactory(ComboBoxTableCell.forTableColumn(editStatusCombo));

        // change process status in the table
        processStatus.setOnEditCommit((TableColumn.CellEditEvent<Processes, String> event) -> {
            TablePosition<Processes, String> pos = event.getTablePosition();

            String status = event.getNewValue();

            int row = pos.getRow();
            Processes p = event.getTableView().getItems().get(row);

            p.setStatus(Integer.toString(tableProcesses.getSelectionModel().getSelectedItem().getID()), status);
        });


        tableProcesses.getColumns().addAll(processID, processLawyerSur, processRole, processDescription, processStatus, processCostFor1Day);

        controlProcess.setSpacing(5);
        controlProcess.getChildren().add(processAddButton);
        controlProcess.getChildren().add(processAdd);
        controlProcess.getChildren().add(processAddCostForLawyers);
        controlProcess.getChildren().add(addLawyerForProcess);
        controlProcess.getChildren().add(processCloseBox);

        processAddButton.setMinSize(251, 45);
        addLawyerForProcess.setMinSize(251, 45);

        processAdd.setSpacing(5);
        processAdd.getChildren().addAll(processAddLawyersCombo, processAddDescription);
        processAdd.setAlignment(Pos.CENTER_LEFT);

        processAddLawyersCombo.setItems(processAddLawyersList);
        processAddLawyersCombo.setPromptText("Surname");
        processAddLawyersCombo.setMinSize(131, 30);
        processAddLawyersCombo.setMaxSize(131, 30);

        processAddDescription.setPromptText("Description");
        processAddDescription.setMinSize(120, 30);
        processAddDescription.setMaxSize(120, 30);

        processCloseBox.getChildren().addAll(processCloseCombo, processCloseButton);
        processCloseBox.setSpacing(5);
        processCloseBox.setAlignment(Pos.CENTER_LEFT);

        processCloseCombo.getItems().addAll("Win", "Lose");
        processCloseCombo.setPromptText("Closing status");
        processCloseCombo.setMinSize(131, 30);
        processCloseCombo.setMaxSize(131, 30);

        processCloseButton.setMinSize(120, 30);
        processCloseButton.setMaxSize(120, 30);

        GridPane.setHgrow(controlProcess, Priority.ALWAYS);
        GridPane.setVgrow(controlProcess, Priority.ALWAYS);
        //GridPane.setMargin(controlProcess, new Insets(20, 130, 20, 130));
        controlProcess.setAlignment(Pos.CENTER_LEFT);

        // choose the lawyer for process
        processAddLawyersCombo.setOnAction(e -> {
            int cost = processAddLawyersCombo.getSelectionModel().getSelectedItem().getCost();
            String spec = processAddLawyersCombo.getSelectionModel().getSelectedItem().getSpec();
            switch (spec) {
                case "Lawyer":
                    processAddCostForLawyers.setText("This lawyer will cost you " + cost * 8 + "$" + " per day");
                    break;
                case "Lawyer assistant":
                    processAddCostForLawyers.setText("This lawyer will cost you " + cost * 6 + "$" + " per day");
                    break;
                case "Consultant":
                    processAddCostForLawyers.setText("This lawyer will cost you " + cost * 4 + "$" + " per day");
                    break;
            }
        });

        // add process
        processAddButton.setOnAction(e -> {
            if (!processAddDescription.getText().isEmpty()) {
                processes.add(new Processes(processAddDescription.getText()));
                processAddCostForLawyers.setText("");
                processAddCostForLawyers.setText("This lawyer will cost you ...");
            } else {
                Others.callError(stage, "You must fill in all fields");
            }
        });

        // close the process
        processCloseButton.setOnAction(e -> {
            if (!tableProcesses.getSelectionModel().isEmpty() && !processCloseCombo.getValue().isEmpty() && tableProcesses.getSelectionModel().getSelectedItem().getStatus().equals("In process")) {
                switch (processCloseCombo.getValue()) {
                    case "Win": {
                            for (int j = 0; j < lawyers.size(); j++) {
                                for (int i = 0; i < processes.size(); i++) {
                                    if (processes.get(i).getID() == tableProcesses.getSelectionModel().getSelectedItem().getID() && processes.get(i).getLawyerId() == lawyers.get(j).getId()) {
                                        processes.get(i).setWin(lawyers.get(j));
                                        processes.get(i).setStatus(Integer.toString(tableProcesses.getSelectionModel().getSelectedItem().getID()), processCloseCombo.getValue());
                                        tableProcesses.getItems().remove(processes.get(i));
                                        processAddLawyersCombo.getItems().add(lawyers.get(j));
                                    }
                                }
                            }
                        tableProcesses.getSelectionModel().getSelectedItem().setStatus(Integer.toString(tableProcesses.getSelectionModel().getSelectedItem().getID()), processCloseCombo.getValue());
                        tableProcesses.getItems().remove(tableProcesses.getSelectionModel().getSelectedItem());
                        processCloseCombo.getSelectionModel().clearSelection();
                    }
                    break;
                    case "Lose": {
                        for (int i = 0; i < processes.size(); i++) {
                            if (processes.get(i).getStatus() == null) {
                                if (processes.get(i).getID() == tableProcesses.getSelectionModel().getSelectedItem().getID()) {
                                    String description = tableProcesses.getSelectionModel().getSelectedItem().getDescription();
                                    String status = tableProcesses.getSelectionModel().getSelectedItem().getStatus();
                                    lostProcesses.add(new Processes(processes.get(i), description, status));
                                }
                            }
                        }
                        for (int l = 0; l < lawyers.size(); l++) {
                            for (int i = 0; i < processes.size(); i++) {
                                if (processes.get(i).getID() == tableProcesses.getSelectionModel().getSelectedItem().getID() && processes.get(i).getLawyerId() == lawyers.get(l).getId()) {
                                    processes.get(i).setLose(lawyers.get(l));
                                    tableProcesses.getItems().remove(processes.get(i));
                                    processAddLawyersCombo.getItems().add(lawyers.get(l));
                                }
                            }
                        }
                        tableProcesses.getSelectionModel().getSelectedItem().setStatus(Integer.toString(tableProcesses.getSelectionModel().getSelectedItem().getID()), processCloseCombo.getValue());
                        tableProcesses.getItems().remove(tableProcesses.getSelectionModel().getSelectedItem());
                        processCloseCombo.getSelectionModel().clearSelection();

                    }
                    break;
                }
            } else {
                Others.callError(stage, "This feature is available for process\n with status 'In process'");
            }
        });

        // add the lawyer to process
        addLawyerForProcess.setOnAction(e -> {
            if (!tableProcesses.getSelectionModel().isEmpty() && !processAddLawyersCombo.getSelectionModel().isEmpty()) {
                Processes pr = tableProcesses.getSelectionModel().getSelectedItem();
                pr.setLawyer(processAddLawyersCombo.getSelectionModel().getSelectedItem(), tableProcesses);
                processAddLawyersList.remove(processAddLawyersCombo.getSelectionModel().getSelectedItem());
                processAddCostForLawyers.setText("");
                processAddCostForLawyers.setText("This lawyer will cost you ...");
            } else Others.callError(stage, "You must choose process\nand free Lawyer");
        });

        // visuality
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(80);
        root.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        root.getColumnConstraints().add(column2);

        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(5);
        root.getRowConstraints().add(row0);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        root.getRowConstraints().add(row1);

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        root.getRowConstraints().add(row2);

        //root.setGridLinesVisible(true);
        root.add(tableLawyers, 0, 1);
        root.add(tableProcesses, 0, 2);
        root.add(controlLawyers, 1, 1);
        root.add(controlProcess, 1, 2);

        Scene scene = new Scene(root);   //Новая сцена
        //scene.getStylesheets().add(getClass().getResource("darkStyle.css").toString());
        scene.getStylesheets().add(getClass().getResource("lightStyle.css").toString());

        stage.setScene(scene);
        stage.setTitle("Lawyer Helper v0.1a");   //Задаём название окна
        stage.setWidth(WIDTH);   //Ширину
        stage.setHeight(HEIGHT);   //И высоту окна
        stage.getIcons().add(new Image("ico.jpg"));
        stage.show();   //Отображаем окно
    }

    // variables
    final private int HEIGHT = 640;
    final private int WIDTH = 760;
    final private String lawyerFilepath = "data/lawyers.txt";
    final private String processesFilepath = "data/processes.txt";
    ObservableList<Lawyers> lawyers = FXCollections.observableArrayList();
    ObservableList<Processes> processes = FXCollections.observableArrayList();

    GridPane root = new GridPane();

    VBox controlLawyers = new VBox();
    TableView<Lawyers> tableLawyers = new TableView<Lawyers>(lawyers);
    Button lawyerAdd = new Button("Add new lawyer...");
    HBox lawyerAddBox = new HBox();
    TextField lawyerAddSur = new TextField();
    ComboBox lawyerAddSpec = new ComboBox();
    TextField lawyerAddCost = new TextField();
    HBox editCostBox = new HBox();
    TextField editCostField = new TextField();
    Button editCost = new Button("Edit cost...");
    HBox findLawyerSpecializationBox = new HBox();
    ComboBox<String> findLawyerSpecializationCombo = new ComboBox<>();
    Text findLawyerSpecializationLabel = new Text();
    Button lostProc = new Button("Show lost processes...");
    ObservableList<Processes> lostProcesses = FXCollections.observableArrayList();

    TableView<Processes> tableProcesses = new TableView<>(processes);
    VBox controlProcess = new VBox();
    Button processAddButton = new Button("Add new process...");
    HBox processAdd = new HBox();
    ComboBox<Lawyers> processAddLawyersCombo = new ComboBox<Lawyers>();
    TextField processAddDescription = new TextField();
    ObservableList<Lawyers> processAddLawyersList = FXCollections.observableArrayList();
    Label processAddCostForLawyers = new Label("This lawyer will cost you ...");
    HBox processCloseBox = new HBox();
    ComboBox<String> processCloseCombo = new ComboBox<>();
    Button processCloseButton = new Button("Close process");
    ObservableList<String> editStatusCombo = FXCollections.observableArrayList();
    Button addLawyerForProcess = new Button("Add lawyer for process");

    // input information from lawyers.txt and processes.txt
    private void inputFromFile() {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(lawyerFilepath));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            String findString = "Lawyer ID: ";
            String buffId;
            int id = 0;
            String surname = "";
            String specialization = "";
            String buffCost;
            int cost = 0;
            //Пока следующая строка имеет значение
            while ((line = bf.readLine()) != null) {
                if (line.startsWith(findString)) {
                    while (!line.equals("====================")) {
                        if (line.startsWith("Lawyer ID: ")) {
                            buffId = (buffId = line.substring(0, line.length() - 1)).substring(buffId.lastIndexOf(':') + 2);
                            id = Integer.parseInt(buffId);
                        }
                        //Если строка начинается с:
                        if (line.startsWith("Surname: ")) {
                            surname = (surname = line.substring(0, line.length() - 1)).substring(surname.lastIndexOf(':') + 2);
                        }
                        if (line.startsWith("Specialization: ")) {
                            specialization = (specialization = line.substring(0, line.length() - 1)).substring(specialization.lastIndexOf(':') + 2);
                        }
                        if (line.startsWith("Cost for 1 hour: ")) {
                            buffCost = (buffCost = line.substring(0, line.length() - 2)).substring(buffCost.lastIndexOf(':') + 2);
                            cost = Integer.parseInt(buffCost);
                        }
                        //Записываем в buffer
                        inputBuffer.append(line);
                        inputBuffer.append("\n");
                        //Меняем строку на следующую
                        line = bf.readLine();
                    }
                    Lawyers l = new Lawyers(id, surname, specialization, cost);
                    lawyers.add(l);
                    processAddLawyersList.add(l);
                }

                //Всё, кроме объекта с искомым iD тоже записываем в buffer
                inputBuffer.append(line);
                inputBuffer.append("\n");
            }

            bf.close();

            File file = new File(lawyerFilepath);
            file.delete();
            file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(lawyerFilepath);
            //Выводим строки, сохранённые в buffer в файл
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

            BufferedReader bfP = new BufferedReader(new FileReader(processesFilepath));
            StringBuffer inputBufferP = new StringBuffer();
            String lineP;
            String findStringP = "Process ID: ";

            String buffIdP;
            int idP = 0;
            int lawyerId = 0;
            int lawyerDayCost = 0;
            int dayCost = 0;
            String lawyersSur = "";
            String description = "";
            String status = "";
            //Пока следующая строка имеет значение
            while ((lineP = bfP.readLine()) != null) {
                if (lineP.startsWith(findStringP)) {
                    while (!lineP.equals("====================")) {
                        while (!lineP.startsWith("-Lawyers-")) {
                            if (lineP.startsWith("Process ID: ")) {
                                buffIdP = (buffIdP = lineP.substring(0, lineP.length() - 1)).substring(buffIdP.lastIndexOf(':') + 2);
                                idP = Integer.parseInt(buffIdP);
                            }
                            if (lineP.startsWith("Description: ")) {
                                description = (description = lineP.substring(0, lineP.length() - 1)).substring(description.lastIndexOf(':') + 2);
                            }
                            if (lineP.startsWith("Status: ")) {
                                status = (status = lineP.substring(0, lineP.length() - 1)).substring(status.lastIndexOf(':') + 2);
                            }
                            if (lineP.startsWith("Day cost: ")) {
                                buffIdP = (buffIdP = lineP.substring(0, lineP.length() - 1)).substring(buffIdP.lastIndexOf(':') + 2);
                                dayCost = Integer.parseInt(buffIdP);
                            }
                            inputBufferP.append(lineP);
                            inputBufferP.append("\n");
                            //Меняем строку на следующую
                            lineP = bfP.readLine();
                        }
                        while (!lineP.equals("====================")) {
                            //Если строка начинается с:
                            if (lineP.startsWith("Lawyer ID: ")) {
                                buffIdP = (buffIdP = lineP.substring(0, lineP.length() - 1)).substring(buffIdP.lastIndexOf(':') + 2);
                                lawyerId = Integer.parseInt(buffIdP);
                            }
                            if (lineP.startsWith("Lawyer Day Cost: ")) {
                                buffIdP = (buffIdP = lineP.substring(0, lineP.length() - 1)).substring(buffIdP.lastIndexOf(':') + 2);
                                lawyerDayCost = Integer.parseInt(buffIdP);
                                for (int l = 0; l < lawyers.size(); l++) {
                                    if (lawyers.get(l).getId() == lawyerId) {
                                        Processes proc = new Processes(idP, lawyers.get(l), lawyerDayCost);
                                        if (!status.equals("Win") && !status.equals("Lose")) {
                                            processes.add(proc);
                                        } else if (status.equals("Lose")) {
                                            Processes pr1 = new Processes(proc, description, status);
                                            lostProcesses.add(pr1);
                                            pr1.setLose(lawyers.get(l));
                                        } else if (status.equals("Win")) {
                                            Processes pr1 = new Processes(proc, description, status);
                                            pr1.setWin(lawyers.get(l));
                                        }
                                    }
                                }
                            }
                            inputBufferP.append(lineP);
                            inputBufferP.append("\n");
                            //Меняем строку на следующую
                            lineP = bfP.readLine();
                        }
                        Processes pr = new Processes(idP, description, status, dayCost);
                        if (!pr.getStatus().equals("Win") && !pr.getStatus().equals("Lose")) {
                            processes.add(pr);
                        }
                    }
                }
                //Всё, кроме объекта с искомым iD тоже записываем в buffer
                inputBufferP.append(lineP);
                inputBufferP.append("\n");
            }

            for (int p = 0; p < processes.size(); p++) {
                for (int i = 0; i < processAddLawyersList.size(); i++) {
                    if (processes.get(p).getLawyerId() == processAddLawyersList.get(i).getId()) {
                        processAddLawyersList.remove(i);
                    }
                }
            }

            bfP.close();
            File fileP = new File(processesFilepath);
            fileP.delete();
            fileP.createNewFile();
            FileOutputStream fileOutP = new FileOutputStream(processesFilepath);
            //Выводим строки, сохранённые в buffer в файл
            fileOutP.write(inputBufferP.toString().getBytes());
            fileOutP.close();

        } catch (IOException e) {
            System.err.println("Error: " + e);
        }

    }

    // default method in javafx app
    @Override
    public void init() throws Exception {

        System.out.println("Application inits");
        super.init();
    }

    // default method in javafx app
    @Override
    public void stop() throws Exception {

        System.out.println("Application stops");
        super.stop();
    }
}