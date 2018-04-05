import java.util.Scanner;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;

//Finished: 1, 2, 3, 4, 5, 6, 9, 10, 11, 12
//Unfinished: 7, 8
public class CSCI3170_Gp15 {
	/*public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2312/db00";
	public static String dbUsername = "Group00";
	public static String dbPassword = "CSCI3170";*/
	public static String dbAddress = "jdbc:mysql://localhost:3306/space?autoReconnect=true&useSSL=false";
	public static String dbUsername = "root";
	public static String dbPassword = "19951215";
	/*public static String dbAddress = "jdbc:mysql://localhost:3306/space";
	public static String dbUsername = "root";
	public static String dbPassword = "root";*/
	public static String[] NEAAttr = {"ID", "Distance", "Family", "Duration", "Energy", "Resources"};
	public static String[] scAttr = {"Agency", "MID", "Num", "Type", "Energy", "T", "Capacity", "Charge"};
	public static String[] certainMissDesignAttr = {"Agency", "MID", "SNum", "Cost", "Benefit"};
	public static String[] mostBenMissDesignAttr = {"NEA ID", "Family", "Agency", "MID", "SNum", "Duration", "Cost", "Benefit"};
	public static String[] rentedSCAttr = {"Agency", "MID", "SNum", "Checkout Date"};
	public static String[] tableNamesAttr = {"NEA", "Contain", "Resource", "Spacecraft_Model", "A_Model", "Rental_Record"};
	public static String[] NEASearchCriteria = {"PlaceHolder", "NID", "Family", "RType"};
	public static String[] scSearchCriteria = {"PlaceHolder", "Agency", "Type", "Energy", "Duration", "Capacity"};
	public static Connection conn;
	public static Scanner choice;
	

	public static String dateFormatter(String date) {
		if (date.equals("null")) {
			return "null";
		}
		String [] dateArray = date.split("-");
		return (dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0]);
	}

	public static void errorMsgPrinter(String msg) {
		System.out.println("[Error]: " + msg);
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
			System.out.print("Processing...");
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
			System.out.println("Done! Database is initialized!");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void deleteTables(){
		try{
			Statement stmt = conn.createStatement();
			System.out.print("Processing...");
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
			for (int i = 0; i != tableNamesAttr.length; ++i) {
				stmt.execute("DROP TABLE IF EXISTS " + tableNamesAttr[i]);
			}
			stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
			System.out.println("Done! Database is removed!");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void loadDataFromFile(String filePath){
		try{
			System.out.print("Processing...");
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
			System.out.println("Data are successfully loaded!");
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
	
	public static void searchNEA(int choiceNum, String keyword){
		String result = "|";
		for(String attr: NEAAttr)
			result += attr+"|";
		String query = "SELECT * FROM nea NATURAL JOIN contain";
		/*if(keyword.length()>0){*/
			query += " WHERE "+NEASearchCriteria[choiceNum]+(choiceNum==1? "='"+keyword+"'": " LIKE '%"+keyword+"%'");
			if(choiceNum==3)
				query += " AND NOT "+NEASearchCriteria[choiceNum]+"='null'";
		/*}*/
		query += ";";
		try{
			Statement stmt = conn.createStatement();
			// check whether the spacecraft exists
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				result += "\n|"+rs.getString("NID")+"|"+rs.getDouble("Distance")+"|"+rs.getString("Family")+"|"+rs.getInt("Duration")+"|"+rs.getDouble("Energy")+"|"+rs.getString("Rtype")+"|";
			}
			System.out.println(result);
			System.out.println("End of Query");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void searchSC(int choiceNum, String keyword){
		String result = "|";
		for(String attr: scAttr)
			result += attr+"|";
		//JOIN a_model and spacecraft_model
		String query = "CREATE OR REPLACE VIEW temp AS (SELECT COALESCE(a_model.Agency, spacecraft_model.Agency) AS Agency, COALESCE(a_model.MID, spacecraft_model.MID) AS MID, COALESCE(a_model.NUM, spacecraft_model.NUM) AS NUM, COALESCE(a_model.Type, spacecraft_model.Type) AS Type, COALESCE(a_model.Energy, spacecraft_model.Energy) AS Energy, COALESCE(a_model.Duration, spacecraft_model.Duration) AS Duration, COALESCE(a_model.Charge, spacecraft_model.Charge) AS Charge, COALESCE(a_model.Capacity, -1) AS Capacity FROM a_model RIGHT JOIN spacecraft_model ON a_model.MID=spacecraft_model.MID AND a_model.Agency=spacecraft_model.Agency);";
		try{
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			query = "SELECT * FROM temp";
			//if(keyword.length()>0)
				query += " WHERE "+scSearchCriteria[choiceNum]+(choiceNum<=2? "='"+keyword+"'": ">="+keyword);
			query += ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				result += "\n|"+rs.getString("Agency")+"|"+rs.getString("MID")+"|"+rs.getInt("Num")+"|"+rs.getString("Type")+"|"+rs.getDouble("Energy")+"|"+rs.getInt("Duration")+"|"+(rs.getInt("Capacity")<0?"null": rs.getInt("Capacity"))+"|"+rs.getInt("Charge");
			}
			System.out.println(result);
			System.out.println("End of Query");
			stmt.executeUpdate("DROP VIEW temp;");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void certMissionDesign(String NEAID){
		String result = "|";
		for(String attr: certainMissDesignAttr)
			result += attr+"|";
		try{
			Statement stmt = conn.createStatement();
			String resultString = "";
			// check whether NEA exists
			ResultSet existNEA = stmt.executeQuery("SELECT EXISTS(SELECT * FROM nea WHERE NID='"+NEAID+"');");
			existNEA.next();
			if (existNEA.getInt(1) == 1) {
				double NEAEnergy = 0;
				int NEADuration = 0;
				int maxSCNum = 0;
				double resourceValue = 0;
				boolean haveResource = true;
				stmt.executeUpdate("CREATE OR REPLACE VIEW NeaCon AS SELECT * FROM nea NATURAL JOIN contain;");
				ResultSet queryResult = stmt.executeQuery("SELECT * FROM NeaCon WHERE NID='"+NEAID+"';");
				while(queryResult.next()){
					NEAEnergy = queryResult.getDouble("Energy");
					NEADuration = queryResult.getInt("Duration");
					if(queryResult.getString("Rtype").equals("null"))
						haveResource = false;
				}
				if(haveResource){
					stmt.executeUpdate("CREATE OR REPLACE VIEW NeaConRes As SELECT * FROM NeaCon NATURAL JOIN resource;");
					queryResult = stmt.executeQuery("SELECT * FROM NeaConRes WHERE NID='"+NEAID+"';");
					while(queryResult.next()){
						resourceValue = queryResult.getDouble("Value")*queryResult.getDouble("Density");
					}
				}
				stmt.executeUpdate("CREATE OR REPLACE VIEW qualified_SC AS SELECT Agency, MID, Num, Energy, Duration, Charge, Capacity FROM a_model WHERE Energy>="+NEAEnergy+" AND Duration>="+NEADuration+";");
				queryResult = stmt.executeQuery("SELECT MAX(Num) AS maxSCNum FROM qualified_SC;");
				while(queryResult.next())
					maxSCNum = queryResult.getInt("maxSCNum");
				stmt.executeUpdate("DROP TABLE IF EXISTS temp1;");
				stmt.executeUpdate("CREATE TABLE temp1(SNum INT);");
				for(int i=1; i<=maxSCNum; i++)
					stmt.executeUpdate("INSERT INTO temp1 VALUES ("+i+");");
				stmt.executeUpdate("CREATE OR REPLACE VIEW temp2 AS SELECT * FROM qualified_SC INNER JOIN temp1;");
				stmt.executeUpdate("CREATE OR REPLACE VIEW expanded_qualified_SC AS SELECT * FROM temp2 WHERE SNum<=Num;");
				stmt.executeUpdate("CREATE OR REPLACE VIEW rented_SC AS SELECT Agency, MID, SNum, Charge FROM rental_record NATURAL JOIN spacecraft_model WHERE ReturnDate IS NULL AND Type='A';");
				stmt.executeUpdate("CREATE OR REPLACE VIEW expanded_rented_SC AS SELECT * FROM expanded_qualified_SC NATURAL JOIN rented_SC;");
				queryResult = stmt.executeQuery("SELECT Agency, MID, Num, Energy, Duration, SNum, Charge*"+NEADuration+" AS Cost, Capacity FROM expanded_qualified_SC WHERE NOT EXISTS (SELECT Agency, MID, SNum FROM expanded_rented_SC WHERE Agency=expanded_qualified_SC.Agency AND MID=expanded_qualified_SC.MID AND  SNum=expanded_qualified_SC.SNum) ORDER BY Agency, MID, SNum;");
				while(queryResult.next()){
					int capacity = queryResult.getInt("Capacity");
					double totalValue = resourceValue*100*100*100*capacity;
					long cost = queryResult.getLong("Cost");
					result += "\n|"+queryResult.getString("Agency")+"|"+queryResult.getString("MID")+"|"+queryResult.getInt("SNum")+"|"+cost+"|"+((long)(totalValue-cost));
				}
				stmt.executeUpdate("DROP TABLE IF EXISTS temp1;");
				stmt.executeUpdate("DROP VIEW expanded_rented_SC;");
				stmt.executeUpdate("DROP VIEW rented_SC;");
				stmt.executeUpdate("DROP VIEW expanded_qualified_SC;");
				stmt.executeUpdate("DROP VIEW temp2;");
				stmt.executeUpdate("DROP VIEW qualified_SC;");
				if(haveResource)
					stmt.executeUpdate("DROP VIEW NeaConRes;");
				stmt.executeUpdate("DROP VIEW NeaCon;");
				System.out.println(result);
				System.out.println("End of Query");
			} else {
				errorMsgPrinter("The NEA does not exist.");
			}		
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void mostBenMissionDesign(int budget, String resourceType){
		String result = "|";
		String planResult = "";
		for(String attr: mostBenMissDesignAttr)
			result += attr+"|";
		try{
			long benefit = 0;
			long cost = 0;
			int NEACounter = 0;
			ArrayList<String> NIDList = new ArrayList<String>();
			ArrayList<String> famList = new ArrayList<String>();
			ArrayList<Integer> durationList = new ArrayList<Integer>();
			ArrayList<Double> energyList = new ArrayList<Double>();
			Statement stmt = conn.createStatement();
			String resultString = "";
			stmt.executeUpdate("CREATE OR REPLACE VIEW NeaConRes As SELECT * FROM nea NATURAL JOIN contain NATURAL JOIN resource;");
			ResultSet queryResult = stmt.executeQuery("SELECT * FROM NeaConRes WHERE Rtype='"+resourceType+"';");
			while(queryResult.next()){
				NIDList.add(queryResult.getString("NID"));
				famList.add(queryResult.getString("Family"));
				durationList.add(queryResult.getInt("Duration"));
				energyList.add(queryResult.getDouble("Energy"));
				NEACounter++;
			}
			if(NEACounter>0) {
				for(int i=0; i<NEACounter; i++){
					String NEAID = NIDList.get(i);
					String family = famList.get(i);
					double NEAEnergy = energyList.get(i);
					int NEADuration = durationList.get(i);
					int maxSCNum = 0;
					double resourceValue = 0;
					queryResult = stmt.executeQuery("SELECT * FROM NeaConRes WHERE NID='"+NEAID+"';");
					while(queryResult.next()){
						resourceValue = queryResult.getDouble("Value")*queryResult.getDouble("Density");
					}
					stmt.executeUpdate("CREATE OR REPLACE VIEW qualified_SC AS SELECT Agency, MID, Num, Energy, Duration, Charge, Capacity FROM a_model WHERE Energy>="+NEAEnergy+" AND Duration>="+NEADuration+";");
					queryResult = stmt.executeQuery("SELECT MAX(Num) AS maxSCNum FROM qualified_SC;");
					while(queryResult.next())
						maxSCNum = queryResult.getInt("maxSCNum");
					stmt.executeUpdate("DROP TABLE IF EXISTS temp1;");
					stmt.executeUpdate("CREATE TABLE temp1(SNum INT);");
					for(int j=1; j<=maxSCNum; j++)
						stmt.executeUpdate("INSERT INTO temp1 VALUES ("+j+");");
					stmt.executeUpdate("CREATE OR REPLACE VIEW temp2 AS SELECT * FROM qualified_SC INNER JOIN temp1;");
					stmt.executeUpdate("CREATE OR REPLACE VIEW expanded_qualified_SC AS SELECT * FROM temp2 WHERE SNum<=Num;");
					stmt.executeUpdate("CREATE OR REPLACE VIEW rented_SC AS SELECT Agency, MID, SNum, Charge FROM rental_record NATURAL JOIN spacecraft_model WHERE ReturnDate IS NULL AND Type='A';");
					stmt.executeUpdate("CREATE OR REPLACE VIEW expanded_rented_SC AS SELECT * FROM expanded_qualified_SC NATURAL JOIN rented_SC;");
					queryResult = stmt.executeQuery("SELECT Agency, MID, Num, Energy, Duration, SNum, Charge*"+NEADuration+" AS Cost, Capacity FROM expanded_qualified_SC WHERE NOT EXISTS (SELECT Agency, MID, SNum FROM expanded_rented_SC WHERE Agency=expanded_qualified_SC.Agency AND MID=expanded_qualified_SC.MID AND  SNum=expanded_qualified_SC.SNum) ORDER BY Cost ASC;");
					while(queryResult.next()){
						int capacity = queryResult.getInt("Capacity");
						cost = queryResult.getInt("Cost");
						long tempBenefit = (long)(resourceValue*100*100*100*capacity)-cost;
						if(cost>budget)
							break;
						if(tempBenefit>benefit){
							benefit = tempBenefit;
							planResult = "|"+NEAID+"|"+family+"|"+queryResult.getString("Agency")+"|"+queryResult.getString("MID")+"|"+queryResult.getInt("SNum")+"|"+NEADuration+"|"+cost+"|"+tempBenefit+"|";
						}
						break;
					}
				}
				stmt.executeUpdate("DROP TABLE IF EXISTS temp1;");
				stmt.executeUpdate("DROP VIEW expanded_rented_SC;");
				stmt.executeUpdate("DROP VIEW rented_SC;");
				stmt.executeUpdate("DROP VIEW expanded_qualified_SC;");
				stmt.executeUpdate("DROP VIEW temp2;");
				stmt.executeUpdate("DROP VIEW qualified_SC;");
				stmt.executeUpdate("DROP VIEW NeaConRes;");
				System.out.println(result);
				System.out.println(planResult);
				System.out.println("End of Query");
			} else {
				errorMsgPrinter("No nea contains this resource.");
			}		
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void rentSC(String agency, String MID, int SNum){
		try{
			Statement stmt = conn.createStatement();
			// check whether the spacecraft exists
			ResultSet existSC = stmt.executeQuery("SELECT EXISTS(SELECT * FROM Spacecraft_Model WHERE Agency=\"" + agency + "\" AND MID=\"" + MID + "\" AND Num>=\"" + String.valueOf(SNum) + "\")");
			existSC.next();
			if (existSC.getInt(1) == 1) {
				// check whether the spacecraft has been rent out
				existSC = stmt.executeQuery("SELECT EXISTS(SELECT * FROM Rental_Record WHERE ReturnDate IS NULL AND Agency=\"" + agency + "\" AND MID=\"" + MID + "\" AND SNum=\"" + String.valueOf(SNum) + "\")");
				existSC.next();
				if (existSC.getInt(1) == 1) {
					errorMsgPrinter("Rental not possible because the spacecraft has not yet been returned.");
				} else {
					java.util.Date date = new java.util.Date();
					String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					stmt.execute("INSERT INTO Rental_Record (Agency, MID, SNum, CheckoutDate, ReturnDate)" +
									"VALUES (\"" + agency + "\", \"" + MID + "\", \"" + String.valueOf(SNum) + "\", \"" + modifiedDate + "\", NULL)" +
									"ON DUPLICATE KEY UPDATE CheckoutDate=\"" + modifiedDate + "\", ReturnDate=NULL");
					System.out.println("Spacecraft rented successfully!");
				}
			} else {
				errorMsgPrinter("The spacecraft does not exist.");
			}		
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void returnSC(String agency, String MID, int SNum){
		try{
			Statement stmt = conn.createStatement();
			// check whether the spacecraft exists
			ResultSet existSC = stmt.executeQuery("SELECT EXISTS(SELECT * FROM Spacecraft_Model WHERE Agency=\"" + agency + "\" AND MID=\"" + MID + "\" AND Num>=\"" + String.valueOf(SNum) + "\")");
			existSC.next();
			if (existSC.getInt(1) == 1) {
				// check whether the spacecraft has been rent out
				existSC = stmt.executeQuery("SELECT EXISTS(SELECT * FROM Rental_Record WHERE ReturnDate IS NULL AND Agency=\"" + agency + "\" AND MID=\"" + MID + "\" AND SNum=\"" + String.valueOf(SNum) + "\")");
				existSC.next();
				if (existSC.getInt(1) == 1) {
					java.util.Date date = new java.util.Date();
					String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					stmt.execute("UPDATE Rental_Record SET ReturnDate=\"" + modifiedDate + "\" WHERE Agency=\"" + agency + "\" AND MID=\"" + MID + "\" AND SNum=\"" + String.valueOf(SNum) + "\"");
					System.out.println("Spacecraft returned successfully!");
				} else {
					errorMsgPrinter("Return is not possible because the spacecraft has not yet been rented.");
				}
			} else {
				errorMsgPrinter("The spacecraft does not exist.");
			}
			
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static String listRentedSC(String startDate, String endDate){
		String result = "";
		try{
			Statement stmt = conn.createStatement();
			ResultSet rentCraft = stmt.executeQuery("SELECT Agency, MID, SNum, CheckoutDate FROM Rental_Record where ReturnDate IS NULL AND CheckoutDate >= \"" + dateFormatter(startDate) + "\" AND CheckoutDate <= \"" + dateFormatter(endDate) + "\"");
			while (rentCraft.next()) {
				result += ("|" + rentCraft.getString("Agency") + "|" + rentCraft.getString("MID") + "|" + rentCraft.getString("SNum") + "|" + rentCraft.getString("CheckoutDate") + "|\n");
			}
		} catch(Exception e){
			printException(e);
		}
		return result;
	}
	
	public static void staffListAgencyRentNum(){
		//System.out.println("staffListAgencyRentNum");
		try{
			System.out.println("|Agency|Number|");
			Statement stmt = conn.createStatement();
			ResultSet rentNum = stmt.executeQuery("SELECT Agency, COUNT(*) AS Num FROM Rental_Record where ReturnDate IS NULL GROUP BY Agency");
			while (rentNum.next()) {
				System.out.println("|" + rentNum.getString("Agency") + "|" + rentNum.getString("Num") + "|");
			}
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void printException(Exception e){
		System.out.println("[Error]: " + e.getMessage());
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
