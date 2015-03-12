package com.GoEuro.test;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Elmar on 12.03.2015.
 * Required libs: google-gson-1.7.1, httpcomponents-client-4.4
 */
public class CSVConverter {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax error. Command line should looks like java -jar GoEuroTest.jar \"CITY_NAME\"");
            return;
        }

        String data;
        System.out.println("Connecting to server...");
        try {
            data= Request.Get("http://api.goeuro.com/api/v2/position/suggest/en/" + args[0]).execute().returnContent().asString();
        } catch (IOException e) {
            System.out.println("Can't get data from server. please check connection. URL: ");
            System.out.println("URL: http://api.goeuro.com/api/v2/position/suggest/en/" + args[0]);
            System.out.println("Exception: " + e.getMessage());
            return;
        }

        System.out.println("Parsing data..");
        Elem[] c = new Gson().fromJson(data, Elem[].class);
        try {
            FileWriter fw = new FileWriter("result.csv");

            for (int i = 0; i < c.length; i++)
                writeToCSV(fw, c[i]);

            fw.flush();
            fw.close();
            System.out.println("Export finished. Result file: result.csv");
        } catch (IOException e) {
            System.out.println("File writing problem.");
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private static void writeToCSV(FileWriter fw, Elem elem) throws IOException {
        fw.write(String.valueOf(elem._id));
        fw.write(",");
        fw.write(escapeForCSV(elem.name));
        fw.write(",");
        fw.write(escapeForCSV(elem.type));
        fw.write(",");
        fw.write(escapeForCSV(String.valueOf(elem.geo_position.latitude)));
        fw.write(",");
        fw.write(escapeForCSV(String.valueOf(elem.geo_position.longitude)));
        fw.write(System.lineSeparator());
    }

    private static String escapeForCSV(String name) {
        return "\"" + name.replaceAll("\"", "\"\"") + "\"";
    }

}

class Elem {
    int _id;
    String name;
    String type;
    GeoPosition geo_position;
}

class GeoPosition {
    float latitude;
    float longitude;
}