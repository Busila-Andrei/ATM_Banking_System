package org.fasttrackit;

import org.fasttrackit.persistance.DataBase;
import org.fasttrackit.service.ATM;

public class App 
{
    public static void main( String[] args )
    {
        String locationDB = "database/db.db";
        DataBase dataBase = new DataBase(locationDB);
        dataBase.createNewDatabase();
        dataBase.createTableCard();
        ATM atm = new ATM(dataBase);
        atm.menuStart();
    }
}
