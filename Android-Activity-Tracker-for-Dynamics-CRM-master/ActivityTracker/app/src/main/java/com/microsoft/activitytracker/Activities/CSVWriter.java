package com.microsoft.activitytracker.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import com.microsoft.activitytracker.Data.Overall;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.io.PrintWriter;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by Bob on 2016-03-06.
 */
public class CSVWriter {

    //Delimiter used in CSV file

    private static final String COMMA_DELIMITER = ",";

    private static final String NEW_LINE_SEPARATOR = "\n";

    private static final String FILE_HEADER = "First Name, Last Name, Home Phone, Cell Phone, " +
            "Email, Alternate Email, Preferred Contact, Experiences, Previous Experience, " +
            "Little Sister Views, Health, Cultures, Children Beliefs, Time Commitment, " +
            "Changes, Address, City, Postal Code, Date Of Birth, Age, BornInCanada";


    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();

        String strDate = sdfDate.format(now);
        return strDate;
    }


    public static void printToCSV(Overall o, Context ctx) {

        String data;

        String newLine = System.getProperty("line.separator");

        data = FILE_HEADER.toString() + newLine + o.getFirstName() + COMMA_DELIMITER
                + o.getLastName() + COMMA_DELIMITER
                + o.getHomePhone() + COMMA_DELIMITER
                + o.getCellPhone() + COMMA_DELIMITER
                + o.getEmail() + COMMA_DELIMITER
                + o.getAltEmail() + COMMA_DELIMITER
                + o.getPreferred() + COMMA_DELIMITER
                + o.getExperiences() + COMMA_DELIMITER
                + o.getPrevexp() + COMMA_DELIMITER
                + o.getLittleSister() + COMMA_DELIMITER
                + o.getHealth() + COMMA_DELIMITER
                + o.getCultures() + COMMA_DELIMITER
                + o.getBeliefsAboutChild() + COMMA_DELIMITER
                + o.getTimeCommitment() + COMMA_DELIMITER
                + o.getChangesInEdu() + COMMA_DELIMITER
                + o.getAddress() + COMMA_DELIMITER
                + o.getCity() + COMMA_DELIMITER
                + o.getPostalCode() + COMMA_DELIMITER
                + o.getDateOfBirth() + COMMA_DELIMITER
                + o.getAge() + COMMA_DELIMITER
                + o.getBornInCanada();


        try {
            // catches IOException below
            final String TESTSTRING = new String(data);

       /* We have to use the openFileOutput()-method
       * the ActivityContext provides, to
       * protect your file from others and
       *
       * This is done for security-reasons.
       * We chose MODE_WORLD_READABLE, because
       *  we have nothing to hide in our file */
            FileOutputStream fOut = ctx.openFileOutput("data.csv",
                    Context.MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(TESTSTRING);

       /* ensure that everything is
        * really written out and close */
            osw.flush();
            osw.close();

//Reading the file back...

       /* We have to use the openFileInput()-method
        * the ActivityContext provides.
        * Again for security reasons with
        * openFileInput(...) */

            FileInputStream fIn = ctx.openFileInput("data.csv");
            InputStreamReader isr = new InputStreamReader(fIn);

        /* Prepare a char-Array that will
         * hold the chars we read back in. */
            char[] inputBuffer = new char[TESTSTRING.length()];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);

            // Check if we read back the same chars that we had written out
            boolean isTheSame = TESTSTRING.equals(readString);

            System.out.println(readString);




            Log.i("File Reading stuff", "success = " + isTheSame);






        } catch (IOException ioe)
        {ioe.printStackTrace();}



/*


        try {
            fileWriter = new FileWriter("/Desktop/" + fileName);

            System.out.println(fileName);

            //Write the CSV file header
            fileWriter.append(FILE_HEADER.toString());

            fileWriter.append(o.getFirstName());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getLastName());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getHomePhone());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getCellPhone());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getEmail());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getAltEmail());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getPreferred());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getExperiences());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getPrevexp());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getLittleSister());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getHealth());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getCultures());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getBeliefsAboutChild());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getTimeCommitment());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getChangesInEdu());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getAddress());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getCity());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getPostalCode());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getDateOfBirth());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getAge());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(o.getBornInCanada());
            fileWriter.append(COMMA_DELIMITER);


            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();

            }
        }*/


    }
}



