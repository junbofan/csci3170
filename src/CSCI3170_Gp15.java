import java.util.Scanner;
import java.sql.*;
import java.io.*;
public class CSCI3170_Gp15 {
	//public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2312/db00";
	//public static String dbUsername = "Group00";
	//public static String dbPassword = "CSCI3170";
	public static String dbAddress = "jdbc:mysql://localhost:3306?autoReconnect=true&useSSL=false";
	public static String dbUsername = "root";
	public static String dbPassword = "19951215";
	public static String[] NEAAttr = {"ID", "Distance", "Family", "Duration", "Energy", "Resources"};
	public static String[] scAttr = {"Agency", "MID", "SNum", "Type", "Energy", "T", "Capacity", "Charge"};
	public static String[] certainMissDesignAttr = {"Agency", "MID", "SNum", "Cost", "Benefit"};
	public static String[] mostBenMissDesignAttr = {"NEA ID", "Family", "Agency", "MID", "SNum", "Duration", "Cost", "Benefit"};
	public static Connection conn;
	public static Scanner choice;
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
	
	public static void adminCreateTable(){
		try{
			System.out.println("Processing...Done! Database is initialized!");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void adminDeleteTable(){
		try{
			System.out.println("Processing...Done! Database is removed!");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void adminLoadData(){
		try{
			System.out.println("Processing...Data are successfully loaded!");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static void adminCountRecord(){
		try{
			System.out.println("Number of records in each table:");
		} catch(Exception e){
			printException(e);
		}
	}
	
	public static String searchNEAFromDB(int choiceNum, String keyword){
		String result = "searchNEAFromDB:";
		try{
			result += "\nchoiceNum: "+choiceNum+"\nkeyword: "+keyword;
		} catch(Exception e){
			printException(e);
		}
		return result;
	}
	
	public static String searchSCFromDB(int choiceNum, String keyword){
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
