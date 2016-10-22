/*
 * Copyright (C) 2016 NanoDano <nanodano@devdungeon.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.devdungeon.ip2kml.helpers;

import com.devdungeon.ip2kml.windows.MainWindow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author NanoDano <nanodano@devdungeon.com>
 */
public class KmlGenerator implements Runnable {

    private final String[] ipList;
    private final String outputFile;
    private final MainWindow mainWindow;

    private String makeHtmlRequest(String url) throws MalformedURLException, ProtocolException, IOException {
        URL remoteUrl = null;
        HttpURLConnection conn = null;
        BufferedReader in = null;
        String inputLine;

        remoteUrl = new URL(url);
        conn = (HttpURLConnection) remoteUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent", "Ip2Kml");

        String responseText = "";
        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            responseText += inputLine + '\n';
        }
        in.close();
        return responseText;
    }

    // Given a list of IP/domain names, do the geoip lookup and generate a kml file
    /**
     */
    private void generateKml() throws IOException {
        String documentContents = "";
        for (String ip : ipList) {
            // ignore blank lines and comments
            ip = ip.trim();
            if (ip.length() == 0 || ip.charAt(0) == '#') {
                continue;
            }

            mainWindow.setStatusBar("Checking " + ip.trim());

            String httpResponse = null;
            try {
                httpResponse = makeHtmlRequest("http://freegeoip.net/json/" + ip);
            } catch (ProtocolException ex) {
                Logger.getLogger(KmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
                mainWindow.setStatusBar("Error making HTTP request: " + ex.getLocalizedMessage());
                continue;
            } catch (IOException ex) {
                Logger.getLogger(KmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
                mainWindow.setStatusBar("IO Error during HTTP request: " + ex.getLocalizedMessage());
                continue;
            }
            if (httpResponse == null) {
                mainWindow.setStatusBar("HTTP Response null, skipping " + ip);
                continue;
            }
            JSONObject jsonResponse = new JSONObject(httpResponse);
            documentContents += "<Placemark>";
            documentContents += "<name>" + jsonResponse.get("ip") + "</name>";
            documentContents += "<description>";
            documentContents += "Country code: " + jsonResponse.get("country_code");
            documentContents += "Country name: " + jsonResponse.get("country_name");
            documentContents += "Region code: " + jsonResponse.get("region_code");
            documentContents += "Region name: " + jsonResponse.get("region_name");
            documentContents += "City: " + jsonResponse.get("city");
            documentContents += "Zip code: " + jsonResponse.get("zip_code");
            documentContents += "Time zone: " + jsonResponse.get("time_zone");
            documentContents += "Latitude: " + jsonResponse.get("latitude");
            documentContents += "Longitude: " + jsonResponse.get("longitude");
            documentContents += "Metro code: " + jsonResponse.get("metro_code");
            documentContents += "</description>";
            documentContents += "<Point>";
            documentContents += "<coordinates>" + jsonResponse.get("longitude") + "," + jsonResponse.get("latitude") + ",0</coordinates>";
            documentContents += "</Point>";
            documentContents += "</Placemark>";
        }

        String documentOpen = "";
        documentOpen += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        documentOpen += "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n";
        documentOpen += "  <Document>\n";

        String documentClose = "";
        documentClose += "  </Document>\n";
        documentClose += "</kml> <!-- Auto generated by ip2kml -->\n";

        writeFile(documentOpen + documentContents + documentClose);
    }

    private void writeFile(String contents) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(contents);
        bufferedWriter.close();
    }

    public KmlGenerator(String[] ipList, String outputFile, MainWindow mainWindow) {
        this.ipList = ipList;
        this.outputFile = outputFile;
        this.mainWindow = mainWindow;
    }

    @Override
    public void run() {
        try {
            generateKml();
        } catch (IOException ex) {
            Logger.getLogger(KmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
            mainWindow.setStatusBar("IO Error: " + ex.getLocalizedMessage());
            return; // Do not continue and set the finished message
        }
        mainWindow.setStatusBar("Finished generating: " + outputFile);
    }

}
