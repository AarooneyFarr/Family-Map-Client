package com.example.familymapclient;

import java.io.*;
import java.net.*;
import com.google.gson.Gson;

import request.LoginRequest;
import request.RegisterRequest;
import result.EventsResponse;
import result.LoginResponse;
import result.PersonResponse;
import result.PersonsResponse;
import result.RegisterResponse;

public class ServerProxy {

    public static void main() {



    }




    public static LoginResponse login(String server, String port, String username, String password) {



        try {

            URL url = new URL("http://" + server + ":" + port + "/user/login");


            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");

            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);	// There is a request body


            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request
            http.connect();

            // This is the JSON string we will send in the HTTP request body
            String reqData =
                    "{" +
                            "\"username\": \"" + username + "\"" +
                            "\"password\": \"" + password + "\"" +
                            "}";

            LoginRequest request = new LoginRequest(username, password);
            Gson gson = new Gson();

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            writeString(gson.toJson(request), reqBody);

            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                System.out.println("Login successful.");

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();


                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                // Display the JSON data returned from the server
                System.out.println(respData);

                //Reader resBody = new InputStreamReader(http.getInputStream());
                Gson gson2 = new Gson();
                LoginResponse response = (LoginResponse) gson2.fromJson(respData, LoginResponse.class);

                respBody.close();

                return response;
            }
            else {

                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println(respData);

                Gson gson3 = new Gson();
                LoginResponse response = (LoginResponse) gson3.fromJson(respData, LoginResponse.class);

                respBody.close();

                return response;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }


        return null;
    }

    public static RegisterResponse register(String server, String port, String username, String password, String firstName, String lastName, String mail, String gender) {

        try {

            URL url = new URL("http://" + server + ":" + port + "/user/register");


            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");


            http.setDoOutput(true);	// There is a request body

            http.addRequestProperty("Accept", "application/json");


            http.connect();

            RegisterRequest request = new RegisterRequest(username, password, mail, firstName, lastName, gender);
            Gson gson = new Gson();

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            writeString(gson.toJson(request), reqBody);


            reqBody.close();


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                System.out.println("Register successful.");

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();


                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                // Display the JSON data returned from the server
                System.out.println(respData);

                Gson gson2 = new Gson();
                RegisterResponse response = (RegisterResponse) gson2.fromJson(respData, RegisterResponse.class);

                respBody.close();

                return response;
            }
            else {

                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println(respData);

                Gson gson3 = new Gson();
                RegisterResponse response = (RegisterResponse) gson3.fromJson(respData, RegisterResponse.class);

                respBody.close();

                return response;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }


        return null;
    }


    public static PersonsResponse getPeople(String server, String port, String authtoken) {

        try {

            URL url = new URL("http://" + server + ":" + port + "/person");


            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.addRequestProperty("Authorization", authtoken);


            http.addRequestProperty("Accept", "application/json");


            http.connect();



            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                System.out.println("Persons request successful.");

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();


                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                // Display the JSON data returned from the server
                System.out.println(respData);

                Gson gson2 = new Gson();
                PersonsResponse response = (PersonsResponse) gson2.fromJson(respData, PersonsResponse.class);

                respBody.close();

                return response;
            }
            else {

                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println(respData);

                Gson gson3 = new Gson();
                PersonsResponse response = (PersonsResponse) gson3.fromJson(respData, PersonsResponse.class);

                respBody.close();

                return response;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }


        return null;
    }

    public static PersonResponse getPerson(String server, String port, String authtoken, String personId) {

        try {

            URL url = new URL("http://" + server + ":" + port + "/person/" + personId);


            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.addRequestProperty("Authorization", authtoken);



            http.addRequestProperty("Accept", "application/json");


            http.connect();


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                System.out.println("Person request successful.");

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();


                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                // Display the JSON data returned from the server
                System.out.println(respData);

                Gson gson2 = new Gson();
                PersonResponse response = (PersonResponse) gson2.fromJson(respData, PersonResponse.class);

                respBody.close();

                return response;
            }
            else {

                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println(respData);

                Gson gson3 = new Gson();
                PersonResponse response = (PersonResponse) gson3.fromJson(respData, PersonResponse.class);

                respBody.close();

                return response;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }


        return null;
    }

    public static EventsResponse getEvents(String server, String port, String authtoken) {

        try {

            URL url = new URL("http://" + server + ":" + port + "/event");


            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.addRequestProperty("Authorization", authtoken);


            http.addRequestProperty("Accept", "application/json");


            http.connect();



            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // The HTTP response status code indicates success,
                // so print a success message
                System.out.println("Events request successful.");

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();


                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                // Display the JSON data returned from the server
                System.out.println(respData);

                Gson gson2 = new Gson();
                EventsResponse response = (EventsResponse) gson2.fromJson(respData, EventsResponse.class);

                respBody.close();

                return response;
            }
            else {

                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println(respData);

                Gson gson3 = new Gson();
                EventsResponse response = (EventsResponse) gson3.fromJson(respData, EventsResponse.class);

                respBody.close();

                return response;
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }


        return null;
    }

    /*
        The readString method shows how to read a String from an InputStream.
    */
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
