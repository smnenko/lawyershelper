package lawyer.control;

import java.io.*;
import java.util.Scanner;

public class Lawyers {
	private static int id;
	private int currID;
	private String sur;
	private String spec;
	private int cost;
	private int dayCost;
	private int winProcesses;
	private int loseProcesses;
	private static String filepath = "data/lawyers.txt";
	private static String dirName = "data";

	protected Lawyers() {
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

	public Lawyers(String _sur, String _spec, int _cost) {
		id += 1;
		currID = id;
		this.sur = _sur;
		this.spec = _spec;
		this.cost = _cost;
		this.winProcesses = 0;
		this.loseProcesses = 0;
		switch (_spec) {
			case "Lawyer":
				this.dayCost = _cost * 8;
				break;
			case "Lawyer assistant":
				this.dayCost = _cost * 6;
				break;
			case "Consultant":
				this.dayCost = _cost * 4;
				break;
		}
		try {
			//Создаём каталог, если он не создан
			File dir = new File(dirName);
			if (!dir.exists())
				dir.mkdir();
			//Создаём файл с инфо о юристах
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			showLawyer();
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
	}

	public Lawyers(int _id, String _sur, String _spec, int _cost) {
		id = _id;
		currID = id;
		this.sur = _sur;
		this.spec = _spec;
		this.cost = _cost;
		switch (_spec) {
			case "Lawyer":
				this.dayCost = _cost * 8;
				break;
			case "Lawyer assistant":
				this.dayCost = _cost * 6;
				break;
			case "Consultant":
				this.dayCost = _cost * 4;
				break;
		}
		try {
			FileReader fr = new FileReader("data/processes.txt");
			Scanner scanner = new Scanner(fr);
			String line;
			while (scanner.hasNextLine()){
				line = scanner.nextLine();
				while (line.equals("Lawyer ID: " + id + " ")){
					while (!line.equals("====================")) {
						if (line.startsWith("Status: ")){
							if (line.equals("Status: Win ")){
								this.winProcesses += 1;
							}else if (line.equals("Status: Lose ")){
								this.loseProcesses += 1;
							}
						}
						line = scanner.nextLine();
					}
				}
			}
			//Создаём каталог, если он не создан
			File dir = new File(dirName);
			if (!dir.exists())
				dir.mkdir();
			//Создаём файл с инфо о юристах
			File file = new File(filepath);
			//При этом удалив файл, чтобы не было захламления ВРЕМЕННО!!!
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
	}

	public void showLawyer() {
		try {
			FileWriter fw = new FileWriter(filepath, true);
			if (id == 1) {
				fw.write("====================\n");
			}
			fw.write("Lawyer ID: " + this.id + " \n");
			fw.write("Surname: " + this.sur + " \n");
			fw.write("Specialization: " + this.spec + " \n");
			fw.write("Cost for 1 hour: " + this.cost + "$ \n");
			fw.write("====================\n");
			fw.close();
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
	}

	public void editCost(int findId, int newCost) {
		String _role = "";
		try {
			BufferedReader bf = new BufferedReader(new FileReader(filepath));
			StringBuffer inputBuffer = new StringBuffer();
			String line;
			String findString = "Lawyer ID: " + findId + " ";
			//Пока следующая строка имеет значение
			while ((line = bf.readLine()) != null) {
				//Пока ID искомого объекта равен ID текущего объекта
				while (line.equals(findString)) {
					//И пока строка не равна концу объекта
					while (!line.equals("====================")) {
						if (line.startsWith("Specialization: ")) {
							_role = (_role = line.substring(0, line.length() - 1)).substring(_role.lastIndexOf(':') + 2);
						}
						//Если строка начинается с:
						if (line.startsWith("Cost for 1 hour: ")) {
							//Изменяем значение строке
							line = "Cost for 1 hour: " + newCost + "$ ";
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
		this.cost = newCost;
		switch (_role) {
			case "Lawyer":
				this.dayCost = this.cost * 8;
				break;
			case "Lawyer assistant":
				this.dayCost = this.cost * 6;
				break;
			case "Consultant":
				this.dayCost = this.cost * 4;
				break;
		}
	}

	public String toString() {
		return this.sur;
	}

	public String getSur() {
		return this.sur;
	}

	public String getSpec() {
		return this.spec;
	}

	public int getCost() {
		return this.cost;
	}

	public int getId() {
		return this.currID;
	}

	public int getDayCost() {
		return this.dayCost;
	}

	public void setWinProcesses(){
		this.winProcesses += 1;
	}
	public void setLoseProcesses(){
		this.loseProcesses += 1;
	}

	public int getLoseProcesses() {
		return loseProcesses;
	}
	public int getWinProcesses() {
		return winProcesses;
	}
}
