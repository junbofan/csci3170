import java.util.Scanner;
import java.sql.*;
import java.io.*;
public class CSCI3170_Gp15 {
	//public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2312/db00";
	//public static String dbUsername = "Group00";
	//public static String dbPassword = "CSCI3170";
	//public static String dbAddress = "jdbc:mysql://localhost:3306?autoReconnect=true&useSSL=false";
	//public static String dbUsername = "root";
	//public static String dbPassword = "19951215";

	public static String dbAddress = "jdbc:mysql://localhost:3306/space";
	public static String dbUsername = "root";
	public static String dbPassword = "root";
	public static String[] NEAAttr = {"ID", "Distance", "Family", "Duration", "Energy", "Resources"};
	public static String[] scAttr = {"Agency", "MID", "SNum", "Type", "Energy", "T", "Capacity", "Charge"};
	public static String[] certainMissDesignAttr = {"Agency", "MID", "SNum", "Cost", "Benefit"};
	public static String[] mostBenMissDesignAttr = {"NEA ID", "Family", "Agency", "MID", "SNum", "Duration", "Cost", "Benefit"};
	public static String[] rentedSCAttr = {"Agency", "MID", "SNum", "Checkout Date"};
	public static Connection conn;
	public static Scanner choice;
	public static String[] tableNamesAttr = {"NEA", "Contain", "Resource", "Spacecraft_Model", "A_Model", "Rental_Record"};

	public static String dateFormatter(String date) {
		if (date.equals("null")) {
			return "null";
		}
		String [] dateArray = date.split("-");
		return (dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0]);
	}

	public static void connectToDatabase(){
		conn = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
		} catch (ClassNotFoundException e){
			System.out.println("[Error]: Java MySQL DB Driver not found!!");
			System.exit(0);
		} catch (SQLException e){
			printException((Exception)e);
		}
	}
	
	public static void createTables(){
		try{
			String NeaSQL = 
						"CREATE TABLE IF NOT EXISTS NEA (" + 
						"NID VARCHAR(25) NOT NULL," +
						"Distance DOUBLE NULL," + 
						"Family VARCHAR(25) NULL," + 
						"Duration DOUBLE NULL," + 
						"Energy DOUBLE NULL," + 
						"PRIMARY KEY (NID));";
			String ContainSQL = 
						"CREATE TABLE IF NOT EXISTS Contain (" +
						"NID VARCHAR(25) NOT NULL," + 
						"Rtype VARCHAR(25) NULL," +
						"PRIMARY KEY (NID));";
			String ResourceSQL = 
						"CREATE TABLE IF NOT EXISTS Resource (" +
						"RType VARCHAR(25) NOT NULL," +
						"Density DOUBLE NULL," +
						"Value DOUBLE NULL," + 
						"PRIMARY KEY (RType));";
			String spacecraftModelSQL = 
						"CREATE TABLE IF NOT EXISTS Spacecraft_Model (" +
						"Agency VARCHAR(25) NOT NULL," +
						"MID VARCHAR(45) NOT NULL," +
						"Num INT NULL," +
						"Type VARCHAR(25) NOT NULL," +
						"Energy DOUBLE NULL," +
						"Duration INT NULL," + 
						"Charge INT NULL," +
						"PRIMARY KEY (Agency, MID));";
			String AModelSQL = 
						"CREATE TABLE IF NOT EXISTS A_Model (" +
						"Agency VARCHAR(25) NOT NULL," +
						"MID VARCHAR(45) NOT NULL," +
						"Num INT NULL," +
						"Type VARCHAR(25) NOT NULL," +
						"Energy DOUBLE NULL," +
						"Duration INT NULL," +
						"Charge INT NULL," +
						"Capacity INT NULL," + 
						"PRIMARY KEY (Agency, MID));";
			String RentalRecordSQL = 
						"CREATE TABLE IF NOT EXISTS Rental_Record (" +
						"Agency VARCHAR(25) NOT NULL," +
						"MID VARCHAR(25) NOT NULL," +
						"SNum INT NOT NULL," +
						"CheckoutDate DATE NULL," +
						"ReturnDate DATE NULL," +
						"PRIMARY KEY (Agency, SNum, MID));";

			Statement stmt  = conn.createStatement();
			stmt.execute(NeaSQL);
			stmt.execute(ContainSQL);
			stmt.execute(ResourceSQL);
			stmt.execute(spacecraftModelSQL);
			stmt.execute(AModelSQL);
			stmt.execute(RentalRecordSQL);
			System.out.println("Processing...Done! Database is initialized!");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void deleteTables(){
		try{
			Statement stmt = conn.createStatement();
			//System.out.print("Processing...");
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
			stmt.execute("DROP TABLE IF EXISTS NEA");
			stmt.execute("DROP TABLE IF EXISTS Contain");
			stmt.execute("DROP TABLE IF EXISTS Resource");
			stmt.execute("DROP TABLE IF EXISTS Spacecraft_Model");
			stmt.execute("DROP TABLE IF EXISTS A_Model");
			stmt.execute("DROP TABLE IF EXISTS Rental_Record");
			//stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
			System.out.println("Processing...Done! Database is removed!");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void loadDataFromFile(String filePath){
		try{
			String line;
			String insertSQL;
			String [] splitDataArray = null;
			Statement stmt = conn.createStatement();
			BufferedReader bufferreader = null;
			// load NEA tanle and Contain table
	    	try {
	    		stmt.execute("TRUNCATE TABLE NEA");
	    		stmt.execute("TRUNCATE TABLE Contain");
		        bufferreader = new BufferedReader(new FileReader(filePath + "/neas.txt"));
		        line = bufferreader.readLine();
		        while (line != null) {     
		        	line = bufferreader.readLine();
		        	if (line == null) {
		        		break;
		        	}
		        	splitDataArray = line.split("\t");
		        	insertSQL = "(";
		        	for (int i = 0; i != splitDataArray.length - 1; ++i) {
		        		insertSQL += ("\"" + splitDataArray[i] + "\",");
		        	}
		        	insertSQL = "INSERT INTO NEA (NID, Distance, Family, Duration, Energy) VALUES " + insertSQL.substring(0, insertSQL.length() - 1) + ");";
					stmt.execute(insertSQL);
					insertSQL = "INSERT INTO Contain (NID, RType) VALUES (\"" + splitDataArray[0] + "\",\"" + splitDataArray[splitDataArray.length - 1] + "\");";	           
		        	stmt.execute(insertSQL);
		        }
		    } catch (FileNotFoundException ex) {
		        ex.printStackTrace();
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		    // load Resource table
		   	try {
		   		stmt.execute("TRUNCATE TABLE Resource");
		   		bufferreader = new BufferedReader(new FileReader(filePath + "/resources.txt"));
		   		line = bufferreader.readLine();
		   		while (line != null) {     
		        	line = bufferreader.readLine();
		        	if (line == null) {
		        		break;
		        	}
		        	splitDataArray = line.split("\t");
		        	insertSQL = "(";
		        	for (int i = 0; i != splitDataArray.length; ++i) {
		        		insertSQL += ("\"" + splitDataArray[i] + "\",");
		        	}
		        	insertSQL = "INSERT INTO Resource (RType, Density, Value) VALUES " + insertSQL.substring(0, insertSQL.length() - 1) + ");";
					stmt.execute(insertSQL);           
		        }
		   	} catch (FileNotFoundException ex) {
		        ex.printStackTrace();
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		    // load Spacecraft_Model table and A_Model table
		    try {
		   		stmt.execute("TRUNCATE TABLE Spacecraft_Model");
		   		stmt.execute("TRUNCATE TABLE A_Model");
		   		bufferreader = new BufferedReader(new FileReader(filePath + "/spacecrafts.txt"));
		   		line = bufferreader.readLine();
		   		while (line != null) {     
		        	line = bufferreader.readLine();
		        	if (line == null) {
		        		break;
		        	}
		        	splitDataArray = line.split("\t");
		        	insertSQL = "(";
		        	for (int i = 0; i != splitDataArray.length; ++i) {
		        		if (i == 6) {
		        			continue;
		        		}
		        		insertSQL += ("\"" + splitDataArray[i] + "\",");
		        	}
					stmt.execute("INSERT INTO Spacecraft_Model (Agency, MID, Num, Type, Energy, Duration, Charge) VALUES " + insertSQL.substring(0, insertSQL.length() - 1) + ");");
					if (splitDataArray[3].equals("A")) {
						stmt.execute("INSERT INTO A_Model (Agency, MID, Num, Type, Energy, Duration, Charge, Capacity) VALUES " + insertSQL + "\"" + splitDataArray[6] + "\");");
					}  
		        }
		   	} catch (FileNotFoundException ex) {
		        ex.printStackTrace();
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
			// load Rental_Record table
			try {
		   		stmt.execute("TRUNCATE TABLE Rental_Record");
		   		bufferreader = new BufferedReader(new FileReader(filePath + "/rentalrecords.txt"));
		   		line = bufferreader.readLine();
		   		while (line != null) {     
		        	line = bufferreader.readLine();
		        	if (line == null) {
		        		break;
		        	}
		        	splitDataArray = line.split("\t");
		        	insertSQL = "(";
		        	for (int i = 0; i != splitDataArray.length; ++i) {
		        		if (i == splitDataArray.length - 1 || i == splitDataArray.length - 2) {
		        			if (splitDataArray[i].equals("null")) {
		        				insertSQL += "NULL,";
		        			} else {
		        				insertSQL += ("\"" + dateFormatter(splitDataArray[i]) + "\",");
		        			}
		        		} else {
		        			insertSQL += ("\"" + splitDataArray[i] + "\",");
		        		}
		        	}
					insertSQL = "INSERT INTO Rental_Record (Agency, MID, SNum, CheckoutDate, ReturnDate) VALUES " + insertSQL.substring(0, insertSQL.length() - 1) + ");";
					stmt.execute(insertSQL);
		        }
		   	} catch (FileNotFoundException ex) {
		        ex.printStackTrace();
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void countRecords(){
		try{
			System.out.println("Number of records in each table:");
			Statement stmt = conn.createStatement();
			for (int i = 0; i != tableNamesAttr.length; ++i) {
				ResultSet queryResult = stmt.executeQuery("SELECT COUNT(*) AS RowNum FROM " + tableNamesAttr[i]);
				while(queryResult.next()) {
					System.out.printf("Table %-16s: %-10s\n", tableNamesAttr[i], queryResult.getInt("RowNum"));
				}
			}
			//System.out.printf("%-25s : %25s%n", "left justified", "right justified");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static String searchNEA(int choiceNum, String keyword){
		String result = "searchNEAFromDB:";
		try{
			result += "\nchoiceNum: "+choiceNum+"\nkeyword: "+keyword;
		} catch(Exception e){
			printException(e);
		}
		return result;
	}
	
	public static String searchSC(int choiceNum, String keyword){
		String result = "searchSCFromDB:";
		try{
			result += "\nchoiceNum: "+choiceNum+"\nkeyword: "+keyword;
		} catch(Exception e){
			printException(e);
		}
		return result;
	}
	
	public static String certMissionDesign(String NEAID){
		String result = "certMissionDesign";
		try{
			result += "\nNEAID: "+NEAID;
		} catch(Exception e){
			printException(e);
		}
		return result;
	}
	
	public static String mostBenMissionDesign(int budget, String resourceType){
		String result = "mostBenMissionDesign";
		try{
			result += "\nbudget: "+budget+"\nresourceType: "+resourceType;
		} catch(Exception e){
			printException(e);
		}
		return result;
	}
	
	public static void rentSC(String agency, String MID, int SNum){
		System.out.println("rentSC");
		try{
			System.out.println("agency: "+agency+"\nMID: "+MID+"\nSNum: "+SNum);
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void returnSC(String agency, String MID, int SNum){
		System.out.println("returnSC");
		try{
			System.out.println("agency: "+agency+"\nMID: "+MID+"\nSNum: "+SNum);
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static String listRentedSC(String startDate, String endDate){
		String result = "listRentedSC:";
		try{
			result += "\nstartDate: "+startDate+"\nendDate: "+endDate;
		} catch(Exception e){
			printException(e);
		}
		return result;
	}
	
	public static void staffListAgencyRentNum(){
		System.out.println("staffListAgencyRentNum");
		try{
			System.out.println("|Agency|Number|");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void printException(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString(); 
		System.out.println("[Error]: "+stackTrace);
	}

	public static void main(String[] args) {
		System.out.println("Welcome to the spacecraft system!");
		choice = new Scanner(System.in);
		try{
			connectToDatabase();
			while(true){
				SystemMenu.mainMenu();
			}
		} catch(Exception e){
			printException(e);
		}
		choice.close();
	}
}
