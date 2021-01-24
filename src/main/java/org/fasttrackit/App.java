package org.fasttrackit;

import org.fasttrackit.persistance.DataBase;
import org.fasttrackit.service.ATM;

public class App 
{
    public static void main( String[] args )
    {

        /*
            Error
            File -> Project Structure -> Global Libraries -> + -> From maven
            check org.xerial:sqlite-jdbc:3.34.0 and install
         */

        String locationDB = "database/ingb.db";
        DataBase dataBase = new DataBase(locationDB);
        dataBase.createNewDatabase();
        dataBase.createTableCard();
        ATM atm = new ATM(dataBase);
        atm.menuStart();
    }
}
