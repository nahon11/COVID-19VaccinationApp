import java.sql.*;
import java.util.*;
public class VaccineApp {
    public static void main (String[] args) throws SQLException{
        Scanner scanner = new Scanner(System.in);
        try { DriverManager.registerDriver ( new com.ibm.db2.jcc.DB2Driver() ) ; }
        catch (Exception cnfe){ System.out.println("Class not found"); }
        String url = "jdbc:db2://winter2021-comp421.cs.mcgill.ca:50000/cs421";
        String your_userid = System.getenv("SOCSUSER");
        String your_password = System.getenv("SOCSPASSWD");
        if(your_userid == null)
        {
          System.err.println("Error! do not have a password to connect to the database!");
          System.exit(1);
        }
        if(your_password == null)
        {
          System.err.println("Error! do not have a password to connect to the database!");
          System.exit(1);
        }
        Connection con = DriverManager.getConnection (url,your_userid,your_password);
        Statement statement = con.createStatement ( ) ;

        while (true){
            
            System.out.println("VaccineApp Main Menu");
            System.out.println("    1. Add a Person");
            System.out.println("    2. Assign a slot to a Person");
            System.out.println("    3. Enter Vaccination information");
            System.out.println("    4. Exit Application");
            System.out.println("Please Enter Your Option: ");
            int decision = scanner.nextInt();
            
            if (decision == 1){
                scanner.nextLine();
                System.out.println("Please enter your health insurance number:");
                String hinsurnum = scanner.nextLine();
                PreparedStatement checkHinsurnum = con.prepareStatement("SELECT hinsurnum FROM Person WHERE Person.hinsurnum = ?");
                checkHinsurnum.setString(1, hinsurnum);
                ResultSet rs = checkHinsurnum.executeQuery();
                if (rs.next()){
                    System.out.println("Error! You have entered a health insurance number already in the system.");
                    System.out.println("If you would like to update the information related to this health insurance number, enter Yes");
                    System.out.println("If you want to cancel this operation, enter anything else");
                    String updateOrQuit = scanner.nextLine();
                    if (updateOrQuit.equals("Yes")){
                        System.out.println("Please enter your first name:");
                        String firstName = scanner.nextLine();
                        System.out.println("Please enter your last name:");
                        String lastName = scanner.nextLine();
                        String fullName = firstName + " " + lastName;
                        System.out.println("Please enter your gender:");
                        String gender = scanner.nextLine();
                        System.out.println("Please enter your vaccination category:");
                        String category = scanner.nextLine();
                        System.out.println("Please enter your postal code, without space separation:");
                        String postalCode = scanner.nextLine();
                        System.out.println("Please enter your street address:");
                        String streetAddr = scanner.nextLine();
                        System.out.println("Please enter your date of birth, in the format MM/DD/YYYY:");
                        String birthdate = scanner.nextLine();
                        System.out.println("Please enter your phone number:");
                        String phoneNumber = scanner.nextLine();
                        System.out.println("Please enter your city:");
                        String city = scanner.nextLine();
                        
                        String insert = "UPDATE Person SET name = " + "\'" + fullName + "\'"+ ",postal_code = " + "\'" + postalCode + "\'" + ",street_address = " + "\'" + streetAddr + "\'" + ",date_of_birth = " + "\'" + birthdate + "\'" + ",phone_number = " + "\'" + phoneNumber + "\'" + ",city = " + "\'" + city + "\'" + ", gender = " + "\'" + gender + "\'" + ", category = " + "\'" +category + "\'" + " WHERE hinsurnum = " + "\'" + hinsurnum + "\'";
                        statement.executeUpdate(insert);
                        System.out.println("Information successfully updated!");
                    } else{
                        continue;
                    }

                } else {
                    System.out.println("Please enter your first name:");
                    String firstName = scanner.nextLine();
                    System.out.println("Please enter your last name:");
                    String lastName = scanner.nextLine();
                    String fullName = firstName + " " + lastName;
                    System.out.println("Please enter your gender:");
                    String gender = scanner.nextLine();
                    System.out.println("Please enter your vaccination category:");
                    String category = scanner.nextLine();
                    System.out.println("Please enter your postal code, without space separation:");
                    String postalCode = scanner.nextLine();
                    System.out.println("Please enter your street address:");
                    String streetAddr = scanner.nextLine();
                    System.out.println("Please enter your date of birth, in the format MM/DD/YYYY:");
                    String birthdate = scanner.nextLine();
                    System.out.println("Please enter your phone number:");
                    String phoneNumber = scanner.nextLine();
                    System.out.println("Please enter your city:");
                    String city = scanner.nextLine();
    
                    String insert = "INSERT INTO Person (hinsurnum,name,postal_code,street_address,date_of_birth,phone_number,city,gender,category)" + " VALUES (\'" + hinsurnum + "\'," +"\'" + fullName + "\'," + "\'" + postalCode + "\'," + "\'" + streetAddr + "\'," + "\'" + birthdate+ "\'," + "\'" + phoneNumber + "\'," + "\'" + city + "\'," + "\'" + gender + "\'," + "\'" + category + "\')";
                    statement.executeUpdate(insert);
                    System.out.println("New person successfully added!");
                }
                rs.close();
            } else if (decision == 2){
                String city="";
                int dosesReceived = 0;
                int dosesNeeded = 0;
                boolean flag = false;
                boolean availFlag = false;
                boolean existenceFlag = false;
                ArrayList<String> validLocations = new ArrayList<String>();
                scanner.nextLine();
                System.out.println("Please enter the health insurance number of the person to assign a slot to:");
                String hinsurnum = scanner.nextLine();
                System.out.println("Please enter the current date, in the format MM/DD/YYYY:");
                String currentDate = scanner.nextLine();
                System.out.println("Please enter the slot code of the slot you are trying to assign:");
                String slotCode = scanner.nextLine();
                System.out.println("Please enter the location name of the slot you are trying to assign:");
                String locationName = scanner.nextLine();
                System.out.println("Please enter the date of the slot you are trying to assign, in the format MM/DD/YYYY:");
                String slotDate= scanner.nextLine();
                System.out.println("Please enter the time of day of the slot you are tring to assign, in the format HH:MM:");
                String slotTime = scanner.nextLine();

                PreparedStatement query4 = con.prepareStatement("SELECT hinsurnum FROM Person WHERE hinsurnum = ?");
                query4.setString(1, hinsurnum);
                ResultSet rs4 = query4.executeQuery();

                if (!rs4.next()){
                    System.out.println("This person does not exist in the database! Please add the person's information before entering vaccine information.");
                    continue;
                }

                PreparedStatement query3 = con.prepareStatement("SELECT hinsurnum FROM Slot WHERE vaccine_date = ? AND slot_code = ? AND location_name = ? AND vaccine_time = ?");
                // PreparedStatement query = con.prepareStatement("SELECT * FROM Slot WHERE vaccine_date > ? AND hinsurnum IS NULL AND location_name IN (SELECT DISTINCT Location.location_name FROM Location,Slot WHERE Location.location_name = Slot.location_name AND Location.city = ?) LIMIT 1");
                query3.setString(1, slotDate);
                query3.setString(2, slotCode);
                query3.setString(3, locationName);
                query3.setString(4, slotTime);
                ResultSet rs3 = query3.executeQuery();

                if (!rs3.next()){
                    System.out.println("This slot does not exist! Please choose a valid slot.");
                    continue;
                }

                String[] current = currentDate.split("/");
                String[] slot = slotDate.split("/");

                if (Integer.parseInt(current[2]) > Integer.parseInt(slot[2])){
                    System.out.println("This slot has already happened! Please choose a different slot.");
                    continue;
                } else if (Integer.parseInt(current[2]) == Integer.parseInt(slot[2])){
                    if (Integer.parseInt(current[1]) > Integer.parseInt(slot[1])){
                        System.out.println("This slot has already happened! Please choose a different slot.");
                        continue;
                    } else if (Integer.parseInt(current[1]) == Integer.parseInt(slot[1])){
                        if (Integer.parseInt(current[0]) > Integer.parseInt(slot[0])){
                            System.out.println("This slot has already happened! Please choose a different slot.");
                            continue;
                        }
                    }
                }

                PreparedStatement findCity = con.prepareStatement("SELECT city FROM Person WHERE Person.hinsurnum = ?");
                findCity.setString(1, hinsurnum);
                ResultSet rs = findCity.executeQuery();

                while (rs.next()){
                    city = rs.getString("city");
                }

                PreparedStatement findVaccineType = con.prepareStatement("SELECT DISTINCT manufacturer FROM Slot WHERE Slot.hinsurnum = ? AND Slot.vaccine_date < ?");
                findVaccineType.setString(1, hinsurnum);
                findVaccineType.setString(2, currentDate);
                ResultSet rs2 = findVaccineType.executeQuery();

                while (rs2.next()){
                    String man = rs2.getString("manufacturer");
                    PreparedStatement numDoses = con.prepareStatement("SELECT doses_needed FROM Vaccine WHERE manufacturer = ?");
                    numDoses.setString(1, man);
                    ResultSet r4 = numDoses.executeQuery();

                    while (r4.next()){
                        dosesNeeded = r4.getInt("doses_needed");
                    }

                    PreparedStatement numDosesReceived = con.prepareStatement("SELECT COUNT(*) FROM Slot WHERE Slot.hinsurnum = ? AND Slot.vaccine_date < ?");
                    numDosesReceived.setString(1, hinsurnum);
                    numDosesReceived.setString(2, currentDate);
                    ResultSet r3 = numDosesReceived.executeQuery();

                    while (r3.next()){
                        dosesReceived = r3.getInt(1);
                    }

                    if (dosesReceived >= dosesNeeded){
                        flag = true;
                        System.out.println("This patient has already taken the necessary amount of vaccine doses for their vaccine type. Please do not assign them a vaccination slot!");
                        continue;
                    }
                }

                if (!flag){
                    PreparedStatement query = con.prepareStatement("SELECT hinsurnum FROM Slot WHERE vaccine_date = ? AND slot_code = ? AND location_name = ? AND vaccine_time = ?");
                    // PreparedStatement query = con.prepareStatement("SELECT * FROM Slot WHERE vaccine_date > ? AND hinsurnum IS NULL AND location_name IN (SELECT DISTINCT Location.location_name FROM Location,Slot WHERE Location.location_name = Slot.location_name AND Location.city = ?) LIMIT 1");
                    query.setString(1, slotDate);
                    query.setString(2, slotCode);
                    query.setString(3, locationName);
                    query.setString(4, slotTime);
                    ResultSet rs1 = query.executeQuery();

                    while (rs1.next()){
                        existenceFlag = true;
                        String health = rs1.getString("hinsurnum");
                        if (health != null){
                            System.out.println("This slot is already occupied. Please try assigning a different slot.");
                        } else {
                            String insert = "UPDATE Slot SET hinsurnum = " + "\'" + hinsurnum + "\'" + ",allocation_date = " + "\'" + currentDate + "\'" + " WHERE slot_code = " + "\'" + slotCode + "\'" + " AND location_name = " + "\'" + locationName+ "\'" + " AND vaccine_date = " + "\'" + slotDate + "\'" + " AND vaccine_time = " + "\'" + slotTime+ "\'";
                            System.out.println(insert);
                            statement.executeUpdate(insert);
                            System.out.println("Successfully assigned Slot to Person!");
                        }
                    }
                    
                }

            } else if (decision == 3){
                String man = "";
                scanner.nextLine();
                System.out.println("Please enter the health insurance number of the Person who received this vaccination:");
                String hinsurnum = scanner.nextLine();
                System.out.println("Please enter the slot code of this vaccination:");
                String slotCode = scanner.nextLine();
                System.out.println("Please enter the hospital where this vaccination took place:");
                String locationName = scanner.nextLine();
                System.out.println("Please enter the date of this vaccination, in the format MM/DD/YYYY:");
                String vaccinationDate = scanner.nextLine();
                System.out.println("Please enter the time of day of this vaccination, in the format HH:MM:");
                String vaccinationTime = scanner.nextLine();
                System.out.println("Please enter the Canadian nurse license number of the nurse who did the vaccination:");
                String cnurlicensenum = scanner.nextLine();
                System.out.println("Please enter the vial number for this vaccination:");
                String vialNumber = scanner.nextLine();
                System.out.println("Please enter the batch number for this vaccination:");
                String batchNumber = scanner.nextLine();
                System.out.println("Please enter the manufacturer for this vaccination:");
                String manufacturer = scanner.nextLine();

                PreparedStatement query4 = con.prepareStatement("SELECT hinsurnum FROM Person WHERE hinsurnum = ?");
                query4.setString(1, hinsurnum);
                ResultSet rs4 = query4.executeQuery();

                if (!rs4.next()){
                    System.out.println("This person does not exist in the database! Please add the person's information before entering vaccine information.");
                    continue;
                }

                PreparedStatement query3 = con.prepareStatement("SELECT hinsurnum FROM Slot WHERE vaccine_date = ? AND slot_code = ? AND location_name = ? AND vaccine_time = ?");
                query3.setString(1, vaccinationDate);
                query3.setString(2, slotCode);
                query3.setString(3, locationName);
                query3.setString(4, vaccinationTime);
                ResultSet rs3 = query3.executeQuery();

                if (!rs3.next()){
                    System.out.println("This slot does not exist! Please choose a valid slot.");
                    continue;
                }

                PreparedStatement findManufacturer = con.prepareStatement("SELECT manufacturer FROM Slot WHERE Slot.hinsurnum = ? AND Slot.vaccine_date < ? LIMIT 1");
                findManufacturer.setString(1, hinsurnum);
                findManufacturer.setString(2, vaccinationDate);
                ResultSet rs = findManufacturer.executeQuery();

                while (rs.next()){
                    man = rs.getString("manufacturer");
                }

                if (man.equals(manufacturer) || man.equals("")){
                    String insert = "UPDATE Slot SET hinsurnum = " + "\'" + hinsurnum + "\'" + ",cnurlicensenumber = " + "\'" + cnurlicensenum + "\'" + ",vial_number = " + "\'" + vialNumber+ "\'" + ",batch_number = " + "\'" + batchNumber + "\'" + ",manufacturer = " + "\'" + manufacturer + "\'" +" WHERE slot_code = " + "\'" + slotCode + "\'" + " AND location_name = " + "\'" + locationName+ "\'" + " AND vaccine_date = " + "\'" + vaccinationDate + "\'" + " AND vaccine_time = " + "\'" + vaccinationTime+ "\'";
                    System.out.println(insert);
                    statement.executeUpdate(insert);
                    System.out.println("Successfully entered Vaccination information!");
                } else {
                    System.out.println("The vaccine brand for this vaccination does not match the vaccine brand of this person's previous vaccination!");
                    continue;
                }

            } else if (decision == 4) {
                statement.close();
                con.close();
                System.exit(0);
            } else{
                System.out.println("Please enter a valid option!");
            }
        }
    }
}
