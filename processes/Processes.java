package processes;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import lawyer.Lawyers;

import java.io.*;

final public class Processes {
    private static int id;
    private int currID;
    private int lawyerCost;
    private String lawyerSur;
    private int lawyerId;
    private String lawyerRole;
    private String description;
    private String status;
    private int dayCost;
    private static String filepath = "data/processes.txt";
    private static String dirName = "data";

    public Processes(Lawyers name, TableView.TableViewSelectionModel<Processes> proc) {
        this.currID = proc.getSelectedItem().getID();
        this.lawyerId = name.getId();
        this.lawyerSur = name.getSur();
        this.lawyerRole = name.getSpec();
        this.lawyerCost = name.getCost();
        this.dayCost = name.getDayCost();
        this.description = "";
		try {
			//Создаём каталог, если он не создан
			File dir = new File(dirName);
			if (!dir.exists())
				dir.mkdir();
			//Создаём файл с инфо о юристах
			File file = new File(filepath);
			//При этом удалив файл, чтобы не было захламления ВРЕМЕННО!!!
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
    }

    public Processes(int _id, Lawyers name, int _dayCost){
        this.currID = _id;
        this.lawyerId = name.getId();
        this.lawyerSur = name.getSur();
        this.lawyerRole = name.getSpec();
        this.lawyerCost = name.getCost();
        this.dayCost = _dayCost;
    }

    public Processes(String _description) {
        id += 1;
        this.currID = id;
        this.description = _description;
        this.status = "In discussion";
        try {
            //Создаём каталог, если он не создан
            File dir = new File(dirName);
            if (!dir.exists())
                dir.mkdir();
            //Создаём файл с инфо о юристах
            File file = new File(filepath);
            //При этом удалив файл, чтобы не было захламления ВРЕМЕННО!!!
            if (!file.exists())
                file.createNewFile();
            showProcesses();
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    public Processes(int _id, String _description, String status, int _dayCost) {
        this.id = _id;
        this.currID = id;
        this.description = _description;
        this.status = status;
        this.dayCost = _dayCost;
        try {
            //Создаём каталог, если он не создан
            File dir = new File(dirName);
            if (!dir.exists())
                dir.mkdir();
            //Создаём файл с инфо о юристах
            File file = new File(filepath);
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    public Processes(Processes proc, String _description, String _status){
        id = proc.getID();
        this.currID = id;
        this.lawyerSur = proc.getLawyerSur();
        this.lawyerId = proc.getLawyerId();
        this.lawyerRole = proc.getLawyerRole();
        this.description = _description;
        this.status = _status;
        this.dayCost = proc.getDayCost();
    }

    public void showProcesses() {
        try {
            FileWriter fr = new FileWriter(filepath, true);
            if (id == 1) {
                fr.write("====================\n");
            }
            fr.write("Process ID: " + id + " \n");
			fr.write("Description: " + this.description + " \n");
			fr.write("Status: " + status + " \n");
			fr.write("Day cost: " + this.dayCost + " \n");
			fr.write("-Lawyers-\n");
            fr.write("====================\n");
            fr.close();
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    public void setStatus(String findId, String newStatus) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filepath));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            String findString = "Process ID: " + findId + " ";
            //Пока следующая строка имеет значение
            while ((line = bf.readLine()) != null) {
                //Пока ID искомого объекта равен ID текущего объекта
                while (line.equals(findString)) {
                    //И пока строка не равна концу объекта
                    while (!line.equals("====================")) {
                        //Если строка начинается с:
                        if (line.startsWith("Status: ")) {
                            //Изменяем значение строке
                            line = "Status: " + newStatus + " ";
                        }
                        //Записываем в buffer
                        inputBuffer.append(line);
                        inputBuffer.append("\n");
                        //Меняем строку на следующую
                        line = bf.readLine();
                    }
                }
                //Всё, кроме объекта с искомым iD тоже записываем в buffer
                inputBuffer.append(line);
                inputBuffer.append("\n");
            }
            bf.close();
            FileOutputStream fileOut = new FileOutputStream(filepath);
            //Выводим строки, сохранённые в buffer в файл
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
            status = newStatus;
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    public void setLawyer(Lawyers name, TableView<Processes> table) {
        ObservableList<Processes> proc = table.getItems();
        Processes pr = new Processes(name, table.getSelectionModel());
        for (int i = 0; i < proc.size(); i++) {
            if (proc.get(i).getID() == pr.getID()) {
                if (!proc.get(i).getDescription().equals("")) {
                    proc.get(i).setDayCost(pr.getDayCost() + proc.get(i).getDayCost());
                }
            }
        }
        proc.add(table.getSelectionModel().getSelectedIndex(), pr);
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filepath));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            String buff;
            int dayCOST;
            String findString = "Process ID: " + table.getSelectionModel().getSelectedItem().getID() + " ";
            //Пока следующая строка имеет значение
            while ((line = bf.readLine()) != null) {
                //Пока ID искомого объекта равен ID текущего объекта
                while (line.equals(findString)) {
                    //И пока строка не равна концу объекта
                    while (!line.equals("====================")) {
                        //Если строка начинается с:
                        if (line.startsWith("Day cost: ")) {
                            //Изменяем значение строке
                            buff = (buff = line.substring(0, line.length() - 1)).substring(buff.lastIndexOf(':') + 2);
                            dayCOST = Integer.parseInt(buff);
                            dayCOST += name.getDayCost();
                            this.dayCost = dayCOST;
                            line = "Day cost: " + dayCOST + " ";
                        }
                        if (line.startsWith("-Lawyers-")) {
                            inputBuffer.append(line);
                            //Изменяем значение строке
                            line = "\nLawyer ID: " + name.getId() + " \n" +
                                    "Lawyer Day Cost: " + name.getDayCost() + " ";
                        }
                        //Записываем в buffer
                        inputBuffer.append(line);
                        inputBuffer.append("\n");
                        //Меняем строку на следующую
                        line = bf.readLine();
                    }
                }
                //Всё, кроме объекта с искомым iD тоже записываем в buffer
                inputBuffer.append(line);
                inputBuffer.append("\n");
            }
            bf.close();
            FileOutputStream fileOut = new FileOutputStream(filepath);
            //Выводим строки, сохранённые в buffer в файл
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    public int getID() {
        return currID;
    }

    public int getCurrID() {
        return currID;
    }

    public String getLawyerRole() {
        return lawyerRole;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getLawyerSur() {
        return lawyerSur;
    }

    public int getLawyerId() {
        return this.lawyerId;
    }

    public int getDayCost() {
        return this.dayCost;
    }

    public void setWin(Lawyers name) {
        name.setWinProcesses();
    }

    public void setLose(Lawyers name) {
        name.setLoseProcesses();
    }

    private void setDayCost(int newCost) {
        this.dayCost = newCost;
    }
}
