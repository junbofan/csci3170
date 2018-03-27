import java.util.Scanner;
import java.sql.*;
import java.io.*;
public class SystemMenu {
	public static void mainMenu(){
		int choiceNum = -1;
		System.out.println("-----Main menu-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Operations for administrator");
		System.out.println("2. Operations for exploration companies (rental customers)");
		System.out.println("3. Operations for spacecraft rental staff");
		System.out.println("0. Exit this program");
		System.out.print("Enter Your Choice: ");
		
		try{
			choiceNum = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		switch(choiceNum){
			case 0:
				System.out.println("Bye bye!");
				System.exit(0);
			case 1:
				SystemMenu.adminMenu();
				break;
			case 2:
				SystemMenu.companyMenu();
				break;
			case 3:
				SystemMenu.staffMenu();
				break;
			default:
				System.out.println("[Error]: Invalid choice.");
		}
	}
	
	public static void adminMenu(){
		int choiceNum = -1;
		System.out.println("-----Operations for administrator menu-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Create all tables");
		System.out.println("2. Delete all tables");
		System.out.println("3. Load data from a dataset");
		System.out.println("4. Show number of records in each table");
		System.out.println("0. Return to the main menu");
		System.out.print("Enter Your Choice: ");
		
		try{
			choiceNum = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		switch(choiceNum){
			case 0:
				return;
			case 1:
				CSCI3170_Gp15.createTables();
				break;
			case 2:
				CSCI3170_Gp15.deleteTables();
				break;
			case 3:
				SystemMenu.adminLoadData();
				break;
			case 4:
				CSCI3170_Gp15.countRecords();
				break;
			default:
				System.out.println("[Error]: Invalid choice.");			
		}
		SystemMenu.adminMenu();
	}
	
	public static void adminLoadData(){
		String filePath = "invalidFilePath";
		System.out.print("Type in the Source Data Path: ");
		filePath = CSCI3170_Gp15.choice.nextLine();
		CSCI3170_Gp15.loadDataFromFile(filePath);
	}	
	
	//company menus
	public static void companyMenu(){
		int choiceNum = -1;
		System.out.println("-----Operations for exploration companies (rental customers)-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Search for NEAs based on some criteria");
		System.out.println("2. Search for spacecrafts based on some criteria");
		System.out.println("3. A certain NEA exploration mission design");
		System.out.println("4. The mose beneficial NEA exploration design");
		System.out.println("0. Return to the main menu");
		System.out.print("Enter Your Choice: ");
		
		try{
			choiceNum = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		switch(choiceNum){
			case 0:
				return;
			case 1:
				SystemMenu.companySearchNEA();
				break;
			case 2:
				SystemMenu.companySearchSC();
				break;
			case 3:
				SystemMenu.companyCertainMissDesign();
				break;
			case 4:
				SystemMenu.companyMostBenMissDeisgn();
				break;
			default:
				System.out.println("[Error]: Invalid choice.");		
		}
		SystemMenu.companyMenu();
	}

	public static void companySearchNEA(){
		int choiceNum = -1;
		String keyword = "invalidKeyword";
		System.out.println("Choose the search criterion:");
		System.out.println("1. ID");
		System.out.println("2. Family");
		System.out.println("3. Resource type");
		System.out.print("My criterion: ");
		try{
			choiceNum = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		if(choiceNum>0 && choiceNum<4){
			System.out.print("Type in the search keyword: ");
			keyword = CSCI3170_Gp15.choice.nextLine();
			String result = CSCI3170_Gp15.searchNEA(choiceNum, keyword);
			String tableHeader = "|";
			for(String attribute: CSCI3170_Gp15.NEAAttr)
				tableHeader += attribute+"|";
			System.out.println(tableHeader);
			System.out.println(result);
			System.out.println("End of Query");
		} else
			System.out.println("[Error]: Invalid choice.");	
	}
	
	public static void companySearchSC(){
		int choiceNum = -1;
		String keyword = "invalidKeyword";
		System.out.println("Choose the search criterion:");
		System.out.println("1. Agency Name");
		System.out.println("2. Type");
		System.out.println("3. Least energy [km/s]");
		System.out.println("4. Least working time [days]");
		System.out.println("5. Least capacity [m^3]");
		System.out.print("My criterion: ");
		try{
			choiceNum = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		if(choiceNum>0 && choiceNum<5){
			System.out.print("Type in the search keyword: ");
			keyword = CSCI3170_Gp15.choice.nextLine();
			String result = CSCI3170_Gp15.searchSC(choiceNum, keyword);
			String tableHeader = "|";
			for(String attribute: CSCI3170_Gp15.scAttr)
				tableHeader += attribute+"|";
			System.out.println(tableHeader);
			System.out.println(result);
			System.out.println("End of Query");
		} else
			System.out.println("[Error]: Invalid choice.");	
	}
	
	public static void companyCertainMissDesign(){
		String NEAID = "invalidNEAID";
		System.out.print("Typing in the NEA ID: ");
		NEAID = CSCI3170_Gp15.choice.nextLine();
		System.out.println("All possible solutions:");
		String result = CSCI3170_Gp15.certMissionDesign(NEAID);
		String tableHeader = "|";
		for(String attribute: CSCI3170_Gp15.certainMissDesignAttr)
			tableHeader += attribute+"|";
		System.out.println(tableHeader);
		System.out.println(result);
		System.out.println("End of Query");
	}
	
	public static void companyMostBenMissDeisgn(){
		int budget = -1;
		String resourceType = "invalidType"; 
		System.out.print("Typing in your budget [$]: ");
		try{
			budget = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		if(budget>=0){
			System.out.print("Typing in the resource type: ");
			resourceType = CSCI3170_Gp15.choice.nextLine();
			System.out.println("The most beneficial mission is:");
			String result = CSCI3170_Gp15.mostBenMissionDesign(budget, resourceType);
			String tableHeader = "|";
			for(String attribute: CSCI3170_Gp15.mostBenMissDesignAttr)
				tableHeader += attribute+"|";
			System.out.println(tableHeader);
			System.out.println(result);
			System.out.println("End of Query");
		} else
			System.out.println("[Error]: Invalid budget");	
	}
	
	//staff menus
	public static void staffMenu(){
		int choiceNum = -1;
		System.out.println("-----Operations for spacecraft rental staff-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Rent a spacecraft");
		System.out.println("2. Return a spacecraft");
		System.out.println("3. List all spacecraft currently rented out (on a mission) for a certain period");
		System.out.println("4. List the number of spacecrafts currently rented out by each Agency");
		System.out.println("0. Return to the main menu");
		System.out.print("Enter Your Choice: ");
		
		try{
			choiceNum = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		switch(choiceNum){
			case 0:
				return;
			case 1:
				SystemMenu.staffRentReturnSC(true);
				break;
			case 2:
				SystemMenu.staffRentReturnSC(false);
				break;
			case 3:
				SystemMenu.staffListRentedSC();
				break;
			case 4:
				CSCI3170_Gp15.staffListAgencyRentNum();
				break;
			default:
				System.out.println("[Error]: Invalid choice.");		
		}
		SystemMenu.staffMenu();
	}
	
	public static void staffRentReturnSC(boolean isRent){
		String agencyName = "invalidAgencyName";
		String MID = "invalidMID";
		int SNum = -1;
		System.out.print("Enter the space agency name: ");
		agencyName = CSCI3170_Gp15.choice.nextLine();
		System.out.print("Enter the MID: ");
		MID = CSCI3170_Gp15.choice.nextLine();
		System.out.print("Enter the SNum: ");
		try{
			SNum = Integer.parseInt(CSCI3170_Gp15.choice.nextLine());
		} catch(Exception e){}
		if(SNum>=0){
			if(isRent){
				CSCI3170_Gp15.rentSC(agencyName, MID, SNum);
				System.out.println("Spacecraft rented successfully!");
			} else{
				CSCI3170_Gp15.returnSC(agencyName, MID, SNum);
				System.out.println("Spacecraft returned successfully!");
			}
		} else
			System.out.println("[Error]: Invalid SNum.");
	}
	
	public static void staffListRentedSC(){
		String startDate = "invalidStartDate";
		String endDate = "invalidEndDate";
		System.out.print("Typing in the starting date [DD-MM-YYYY]: ");
		startDate = CSCI3170_Gp15.choice.nextLine();
		System.out.print("Typing in the ending date [DD-MM-YYYY]: ");
		endDate = CSCI3170_Gp15.choice.nextLine();
		System.out.println("List of the unreturned spacecraft");
		String result = CSCI3170_Gp15.listRentedSC(startDate, endDate);
		String tableHeader = "|";
		for(String attribute: CSCI3170_Gp15.rentedSCAttr)
			tableHeader += attribute+"|";
		System.out.println(tableHeader);
		System.out.println(result);
		System.out.println("End of Query");
	}
}
