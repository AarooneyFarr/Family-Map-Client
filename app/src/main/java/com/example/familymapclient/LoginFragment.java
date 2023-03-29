package com.example.familymapclient;

import static com.example.familymapclient.ServerProxy.getEvents;
import static com.example.familymapclient.ServerProxy.getPeople;
import static com.example.familymapclient.ServerProxy.getPerson;
import static com.example.familymapclient.ServerProxy.login;
import static com.example.familymapclient.ServerProxy.register;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import model.Person;
import result.EventsResponse;
import result.LoginResponse;
import result.PersonResponse;
import result.PersonsResponse;
import result.RegisterResponse;

public class LoginFragment extends Fragment {


    private static final String SUCCESS_KEY = "Success";
    private static final String PERSON_ID_KEY = "personID";
    private static final String AUTHTOKEN_KEY = "authToken";
    private static final String PERSONS_KEY = "persons";
    private static final String EVENTS_KEY = "events";
    private static final String PERSON_KEY = "person";





    private EditText serverHost, serverPort, username, password, firstName, lastName, email;

    private String serverHostString, serverPortString, usernameString, passwordString, firstNameString, lastNameString, emailString, genderString;

    private Button loginButton, registerButton;
    private RadioGroup genderRadioGroup;

    private LoginResponse loginResponse;
    private RegisterResponse registerResponse;

    private DataCache dataCache;
    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }


    public LoginFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        dataCache = DataCache.getInstance();

        loginButton = view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);

        //EditTexts
        serverHost = view.findViewById(R.id.editTextServerHost);
        serverPort = view.findViewById(R.id.editTextServerPort);
        username = view.findViewById(R.id.editTextUsername);
        password = view.findViewById(R.id.editTextPassword);
        firstName = view.findViewById(R.id.editTextFirstName);
        lastName = view.findViewById(R.id.editTextLastName);
        email = view.findViewById(R.id.editTextEmail);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);

        //listeners for string data
        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        genderRadioGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            genderString = "f";
            if(id == 0){
                genderString = "m";
            }
            enableButtons();
        });

        registerButton.setOnClickListener(v -> {

            // Set up a handler that will process messages from the task and make updates on the UI thread
            Handler registerHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Bundle bundle = message.getData();
                    boolean success = bundle.getBoolean(SUCCESS_KEY, false);
                    if(success){
                        String personId = bundle.getString(PERSON_ID_KEY);
                        String authToken = bundle.getString(AUTHTOKEN_KEY);
                        getPersonData(authToken, personId);
//                        listener.notifyDone();
                    }
                    else {
                        Toast.makeText(getActivity(), "Error: register error", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            RegisterTask task = new RegisterTask(registerHandler);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(task);

        });


        loginButton.setOnClickListener(v -> {

                // Set up a handler that will process messages from the task and make updates on the UI thread
                Handler loginHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean(SUCCESS_KEY, false);
                        if(success){
                            String personId = bundle.getString(PERSON_ID_KEY);
                            String authToken = bundle.getString(AUTHTOKEN_KEY);
                            getPersonData(authToken, personId);

                        }
                        else {
                            Toast.makeText(getActivity(), "Error: login error", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                LoginTask task = new LoginTask(loginHandler);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);

        });

        return view;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            serverHostString = serverHost.getText().toString().trim();
            serverPortString = serverPort.getText().toString().trim();
            usernameString = username.getText().toString().trim();
            passwordString = password.getText().toString().trim();
            firstNameString = firstName.getText().toString().trim();
            lastNameString = lastName.getText().toString().trim();
            emailString = email.getText().toString().trim();
            enableButtons();
        }


        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void enableButtons() {
        loginButton.setEnabled(!usernameString.isEmpty() && !passwordString.isEmpty() && !serverHostString.isEmpty() && !serverPortString.isEmpty());
        registerButton.setEnabled(!usernameString.isEmpty() && !passwordString.isEmpty() && !serverHostString.isEmpty() && !serverPortString.isEmpty() && !firstNameString.isEmpty() && !lastNameString.isEmpty() && !emailString.isEmpty() && genderString != null);
    }

    public void getPersonData(String authtoken, String personID){
        Handler personHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean success = bundle.getBoolean(SUCCESS_KEY, false);
                if(success){
                    Gson gson = new Gson();
                    PersonResponse result = gson.fromJson(bundle.getString(PERSON_KEY), PersonResponse.class);

                    Person user = new Person(result.getPersonID(), result.getAssociatedUsername(), result.getFirstName(), result.getLastName(), result.getGender(), result.getFatherID(), result.getMotherID(), result.getSpouseID());

                    dataCache.setCurrentUser(user);
                    Toast.makeText(getActivity(), "Welcome " + user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();
//                    listener.notifyDone();
                }
                else {
                    Toast.makeText(getActivity(), "Error: person error", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Handler personsHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean success = bundle.getBoolean(SUCCESS_KEY, false);
                if(success){
                    Gson gson = new Gson();
                    PersonsResponse result = gson.fromJson(bundle.getString(PERSONS_KEY), PersonsResponse.class);
                    dataCache.addPeople(result.getData());
                }
                else {
                    Toast.makeText(getActivity(), "Error: persons error", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Handler eventsHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean success = bundle.getBoolean(SUCCESS_KEY, false);
                if(success){
                    Gson gson = new Gson();
                    EventsResponse result = gson.fromJson(bundle.getString(EVENTS_KEY), EventsResponse.class);
                    dataCache.addEvents(result.getData());
                }
                else {
                    Toast.makeText(getActivity(), "Error: persons error", Toast.LENGTH_SHORT).show();
                }
            }
        };

        getPeopleTask peopleTask = new getPeopleTask(personsHandler, authtoken);
        getEventsTask eventsTask = new getEventsTask(eventsHandler, authtoken);
        getPersonTask personTask = new getPersonTask(personHandler, authtoken, personID);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(peopleTask);
        executor.submit(eventsTask);
        executor.submit(personTask);
    }

    private class LoginTask implements Runnable {

        private final Handler messageHandler;

        public LoginTask(Handler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {


            loginResponse = login(serverHostString, serverPortString, usernameString, passwordString);

            assert loginResponse != null;
            sendMessage(loginResponse.isSuccess(), loginResponse.getPersonID(), loginResponse.getAuthtoken());
        }

        private void sendMessage(Boolean success, String personID, String authToken) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, success);
            messageBundle.putString(PERSON_ID_KEY, personID);
            messageBundle.putString(AUTHTOKEN_KEY, authToken);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private class RegisterTask implements Runnable {

        private final Handler messageHandler;

        public RegisterTask(Handler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {


            registerResponse = register(serverHostString, serverPortString, usernameString, passwordString, firstNameString, lastNameString, emailString, genderString);

            assert registerResponse != null;
            sendMessage(registerResponse.isSuccess(), registerResponse.getPersonID(), registerResponse.getAuthtoken());
        }

        private void sendMessage(Boolean success, String personID, String authToken) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, success);
            messageBundle.putString(PERSON_ID_KEY, personID);
            messageBundle.putString(AUTHTOKEN_KEY, authToken);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private class getPeopleTask implements Runnable {

        private final Handler messageHandler;
        private final String authtoken;

        public getPeopleTask(Handler messageHandler, String authtoken) {
            this.messageHandler = messageHandler;
            this.authtoken = authtoken;
        }

        @Override
        public void run() {
            PersonsResponse personsResponse = getPeople(serverHostString, serverPortString, authtoken);

            assert personsResponse != null;
            sendMessage(personsResponse);
        }

        private void sendMessage(PersonsResponse personsResponse) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, personsResponse.isSuccess());

            Gson gson = new Gson();
            String personsJSON = gson.toJson(personsResponse);

            messageBundle.putString(PERSONS_KEY, personsJSON);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private class getPersonTask implements Runnable {

        private final Handler messageHandler;
        private final String authtoken;
        private final String personID;

        public getPersonTask(Handler messageHandler, String authtoken, String personID) {
            this.messageHandler = messageHandler;
            this.authtoken = authtoken;
            this.personID = personID;
        }

        @Override
        public void run() {
            PersonResponse personResponse = getPerson(serverHostString, serverPortString, authtoken, personID);

            assert personResponse != null;
            sendMessage(personResponse);
        }

        private void sendMessage(PersonResponse personResponse) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, personResponse.isSuccess());

            Gson gson = new Gson();
            String personsJSON = gson.toJson(personResponse);

            messageBundle.putString(PERSON_KEY, personsJSON);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private class getEventsTask implements Runnable {

        private final Handler messageHandler;
        private final String authtoken;

        public getEventsTask(Handler messageHandler, String authtoken) {
            this.messageHandler = messageHandler;
            this.authtoken = authtoken;
        }

        @Override
        public void run() {
            EventsResponse eventsResponse = getEvents(serverHostString, serverPortString, authtoken);

            assert eventsResponse != null;
            sendMessage(eventsResponse);
        }

        private void sendMessage(EventsResponse eventsResponse) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, eventsResponse.isSuccess());

            Gson gson = new Gson();
            String personsJSON = gson.toJson(eventsResponse);

            messageBundle.putString(EVENTS_KEY, personsJSON);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
}