package AppointmentScheduling;

import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;
//import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import Dashboardpak.Dashboard;
import Visit_Referral_Management.Date;

import javax.swing.*;

public class Appointment {
    Scanner scanner = new Scanner(System.in);
    private static final int MAX_APPOINTMENTS = 3;
    private static final int recordSize = 58; 
    private static final int TotalRecords = 6; // assuming 2 doctors with 3 slots each
    private static final int dateSize = 10; // assuming format is dd/mm/yyyy
    private static final int statusSize = 9; // assuming max status length is 18
    private int appointmentNumber;
    private int patientNumber;
    private int doctorNumber;
    private Date appointmentDate;
    private String status;
    private static int slotNumber = 0;// to give each appointment a unique number
    private static Queue<Integer> walkInQueue = new LinkedList<>();// to store walk-in patients

    public Appointment() {
        this.appointmentNumber = 0; // 4 bytes
        this.patientNumber = 0;// 4 bytes
        this.doctorNumber = 0; // 4 bytes
        this.appointmentDate = new Date(1,0, 0); // assuming format is yyyy-MM-dd 10 char bytes
        this.status = String.format("%-9s", " ").substring(0, 9);// 18 bytes
                                                                  // start time int 4 bytes
                                                                  // end time int 4 bytes
    }

    public Appointment(int patientNumber, int doctorNumber, int day, int month, int year, String status) {
        this.patientNumber = patientNumber;
        this.doctorNumber = doctorNumber;
        this.appointmentDate = new Date(year, month, day);
        this.status = String.format("%-9s", status).substring(0, 9);
    }

    public static int getMaxAppointments() {
        return MAX_APPOINTMENTS;
    }

    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public int getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(int patientNumber) {
        this.patientNumber = patientNumber;
    }

    public int getDoctorNumber() {
        return doctorNumber;
    }

    public void setDoctorNumber(int doctorNumber) {
        this.doctorNumber = doctorNumber;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int getSlotNumber() {
        return slotNumber;
    }

    public static void setSlotNumber(int slotNumber) {
        Appointment.slotNumber = slotNumber;
    }

    public void displayAppointment() {
        // Create the frame
        JFrame appointmentFrame = new JFrame("View Appointment");
        appointmentFrame.setSize(500, 500);
        appointmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        appointmentFrame.setLayout(new BorderLayout());

        // Create and configure components
        JTextArea appointmentDetails = new JTextArea();
        appointmentDetails.setEditable(false);

        JButton close = new JButton("Close");
        close.addActionListener(e -> appointmentFrame.dispose());

        JScrollPane scrollPane = new JScrollPane(appointmentDetails);

        // File handling and appointment details retrieval
        try (RandomAccessFile pen = new RandomAccessFile("appointment_times.txt", "rw")) {
            pen.seek(6 + (long) (getPatientNumber() - 1) * recordSize);
            int startTime = pen.readInt();
            int endTime = pen.readInt();

            if (startTime == 0 && endTime == 0) {
                appointmentDetails.setText("No appointments available");
            } else {
                appointmentDetails.setText(
                        "Appointment Number  : " + getAppointmentNumber() + "\n" +
                                "Patient Number      : " + getPatientNumber() + "\n" +
                                "Doctor Number       : " + getDoctorNumber() + "\n" +
                                "Date                : " + getAppointmentDate() + "\n" +
                                "Status              : " + getStatus() + "\n" +
                                "Time                : " + startTime + " - " + endTime + "\n" +
                                "---------------------------------------------------------------"
                );
            }
        } catch (Exception e) {
            System.out.println("An Exception occurred");
        }

        // Add components to frame
        appointmentFrame.add(scrollPane, BorderLayout.CENTER);
        appointmentFrame.add(close, BorderLayout.SOUTH);
        appointmentFrame.setVisible(true);
    }

    public void ManageAppointments() {
        // Create the frame
        JFrame frame = new JFrame("Manage Appointments");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Manage Appointments", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Button panel setup
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        // Create buttons
        JButton updateStatusButton = new JButton("Update Appointment Status");
        JButton cancelAppointmentButton = new JButton("Cancel Appointment");
        JButton scheduleAppointmentButton = new JButton("Schedule Appointment");
        JButton returnToDashboardButton = new JButton("Return to Dashboard Menu");

        // Button action listeners
        updateStatusButton.addActionListener(e -> modifyAppointment('U'));
        cancelAppointmentButton.addActionListener(e -> modifyAppointment('C'));
        scheduleAppointmentButton.addActionListener(e -> {
            frame.setVisible(false);
            setAppointment();
        });
        returnToDashboardButton.addActionListener(e -> {
            frame.dispose();
            doctorDashboard();
        });

        // Add buttons to panel
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(cancelAppointmentButton);
        buttonPanel.add(scheduleAppointmentButton);
        buttonPanel.add(returnToDashboardButton);

        // Add components to frame
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Make frame visible
        frame.setVisible(true);
    }

    public void setAppointment() {
        // Create the frame
        JFrame frame = new JFrame("Set Appointment");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Input panel setup
        JPanel inputPanel = new JPanel();
        JLabel label = new JLabel("Enter Appointment Number:");
        JTextField appointmentNumberField = new JTextField(10);
        JButton submitButton = new JButton("Schedule");
        JButton returnToMenuButton = new JButton("Return to Appointment Menu");

        returnToMenuButton.addActionListener(e -> {
            frame.dispose();
            main(null);
        });

        // Add components to the input panel
        inputPanel.add(label);
        inputPanel.add(appointmentNumberField);
        inputPanel.add(submitButton);
        inputPanel.add(returnToMenuButton, BorderLayout.EAST);

        // Status area setup
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);

        submitButton.addActionListener(e -> {
            int slot;
            try {
                //Get the patient number from the user to set the appointment
                System.out.print("Enter your Patient Number: ");
                setPatientNumber(Integer.parseInt(appointmentNumberField.getText()));
                // Parse the input slot number
                slot = Integer.parseInt(appointmentNumberField.getText());
                RandomAccessFile pen = new RandomAccessFile("appointment_times.txt", "rw");
                pen.seek((long) (slot - 1) * recordSize);

                // Read appointment details
                int apptNumber = pen.readInt();
                pen.seek(5 + (long) (slot - 1) * recordSize);
                char[] stat = new char[statusSize];

                for (int i = 0; i < statusSize; i++) {
                    stat[i] = pen.readChar();//status
                }

                String statusFromFile = new String(stat);

                // Check if appointment is valid
                if (slot == apptNumber && statusFromFile.trim().equals(" ")) {
                    statusArea.setText("Appointment Scheduled Successfully!\n");
                    if (!walkInQueue.isEmpty()) {
                        int nextPatient = walkInQueue.poll();
                        System.out.println("Next walk-in patient (Patient #" + nextPatient + ") can now be scheduled.");
                    }
                    pen.seek((long) (slot - 1) * recordSize);
                    setAppointmentNumber(pen.readInt());
                    pen.writeInt(getPatientNumber());

                    pen.seek(4 + (long) (slot - 1) * recordSize);
                    char[] dateChars = new char[dateSize]; // DD-MM-YYYY
                    for (int i = 0; i < dateSize; i++) {
                        dateChars[i] = pen.readChar();
                    }

                    String[] dateParts = new String(dateChars).split("-");
                    int day = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    int year = Integer.parseInt(dateParts[2]);

                    Date appointmentDate = new Date(day, month, year);
                    setAppointmentDate(appointmentDate);
                    setStatus("Scheduled");
                    pen.writeChars("Scheduled");

                    int startTime = pen.readInt();
                    int endTime = pen.readInt();
                    statusArea.append("Time: " + startTime + " - " + endTime + "\n");

                    displayAppointment();
                } else {
                    statusArea.setText("Not a valid appointment number.");
                }

                pen.close();
            } catch (Exception ex) {
                statusArea.setText("An error occurred while scheduling the appointment.");
            }
        });

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }
    public void registerWalkIn() {
        System.out.println("Registering patient " + getPatientNumber() + " as a walk-in...");
        walkInQueue.offer(getPatientNumber());
        System.out.println("You are added to the walk-in queue. Position: " + walkInQueue.size());
    }
    public void autoAssignWalkIn() {
        RandomAccessFile pen = null;
        try {
            pen = new RandomAccessFile("appointment_times.txt", "rw");
            while (!walkInQueue.isEmpty()) {
                for (int i = 0; i < TotalRecords; i++) {
                    pen.seek((i * recordSize) + 22);
                    char[] statusChars = new char[statusSize];
                    for (int s = 0; s < statusSize; s++) {
                        statusChars[s] = pen.readChar();
                    }
                    String stat = new String(statusChars);
                    if (stat.trim().equals("") || stat.trim().equals("Cancelled")) {
                        int walkInPatient = walkInQueue.poll();
                        pen.seek((i * recordSize) + 4);
                        pen.writeInt(walkInPatient);
                        pen.seek((i * recordSize) + 22);
                        pen.writeChars(String.format("%-9s", "Walk-in"));
                        System.out.println("Walk-in patient #" + walkInPatient + " assigned to slot #" + (i + 1));
                        break;
                    }
                }
            }
            pen.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void modifyAppointment(char choice) {
        // Create the frame
        JFrame frame = new JFrame("Modify Appointment");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Input panel setup
        JPanel inputPanel = new JPanel();
        JLabel label = new JLabel("Enter Appointment Number:");
        JTextField appointmentNumberField = new JTextField(10);
        JLabel statusLabel = new JLabel("Enter New Status (if updating):");
        JTextField statusField = new JTextField(10);

        JButton returnToMenuButton = new JButton("Return to Menu");
        returnToMenuButton.addActionListener(e -> {
            frame.dispose();
            doctorDashboard();
        });

        // Submit button setup
        JButton submitButton;
        if (choice == 'U') {
            submitButton = new JButton("Update");
        } else {
            submitButton = new JButton("Cancel");
        }

        // Add components to input panel
        inputPanel.add(label);
        inputPanel.add(appointmentNumberField);
        if (choice == 'U') {
            inputPanel.add(statusLabel);
            inputPanel.add(statusField);
        }
        inputPanel.add(submitButton);

        // Status area setup
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);

        submitButton.addActionListener(e -> {
            boolean found = false;
            int apptNumber = Integer.parseInt(appointmentNumberField.getText());
            String newStatus = choice == 'U' ? statusField.getText() : "Cancelled";

            try {
                // File handling to modify appointment details
                RandomAccessFile pen = new RandomAccessFile("appointment_times.txt", "rw");
                pen.seek((long) (apptNumber - 1) * recordSize);
                if (pen.readInt() == apptNumber) {
                    found = true;
                } else {
                    statusArea.setText("No appointment found.");
                }

                if (found) {
                    pen.seek(5 + (long) (apptNumber - 1) * recordSize);
                    setStatus(newStatus);
                    pen.writeChars(choice == 'U' ? String.format("%-9s", newStatus) : " ");
                    statusArea.setText(choice == 'U' ? "Appointment status updated successfully!" : "Appointment cancelled successfully!");
                    if (statusArea.getText().equals("Appointment cancelled successfully!")) {
                        autoAssignWalkIn();
                    }
                }

                pen.close();
            } catch (IOException ex) {
                statusArea.setText("An error occurred while modifying the appointment.");
            }
        });

        // Add components to frame
        inputPanel.add(returnToMenuButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void doctorDashboard() {
        // Create the frame
        JFrame frame = new JFrame("Doctor Dashboard");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Title label setup
        JLabel titleLabel = new JLabel("Doctor Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Button panel setup
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        // Create buttons
        JButton generateAppointmentsButton = new JButton("Generate Appointment Times");
        JButton manageAppointmentsButton = new JButton("Manage Appointments");
        JButton returnToAppointmentButton = new JButton("Return to Appointment Menu");

        // Add action listeners to buttons
        generateAppointmentsButton.addActionListener(e -> {
            frame.setVisible(false);
            generateAppointmentTimes();
        });

        manageAppointmentsButton.addActionListener(e -> {
            frame.setVisible(false);
            ManageAppointments();
        });

        returnToAppointmentButton.addActionListener(e -> {
            frame.dispose();
            start();
        });

        // Add buttons to the button panel
        buttonPanel.add(generateAppointmentsButton);
        buttonPanel.add(manageAppointmentsButton);
        buttonPanel.add(returnToAppointmentButton);

        // Add components to the frame
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);
    }

    public void patientDashboard() {
        // Create the frame
        JFrame patientFrame = new JFrame("Patient Dashboard");
        patientFrame.setSize(600, 500);
        patientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        patientFrame.setLayout(new BorderLayout());

        // Title label setup
        JLabel titleLabel = new JLabel("Patient Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Button panel setup
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        // Create buttons
        JButton bookAppointmentButton = new JButton("Book Appointment");
        JButton viewAppointmentsButton = new JButton("View Appointments");
        JButton cancelAppointmentButton = new JButton("Modify Appointment");
        JButton returnToAppointmentMenuButton = new JButton("Return to Appointment Menu");

        // Add action listeners to buttons
        bookAppointmentButton.addActionListener(e -> {
            patientFrame.setVisible(false);
            setAppointment();
        });

        viewAppointmentsButton.addActionListener(e -> {
            displayAppointment();
        });

        cancelAppointmentButton.addActionListener(e -> {
            patientFrame.setVisible(false);
            modifyAppointment('C');
        });

        returnToAppointmentMenuButton.addActionListener(e -> {
            patientFrame.dispose();
            start();
        });

        // Add buttons to the button panel
        buttonPanel.add(bookAppointmentButton);
        buttonPanel.add(viewAppointmentsButton);
        buttonPanel.add(cancelAppointmentButton);
        buttonPanel.add(returnToAppointmentMenuButton);

        // Add components to the frame
        patientFrame.add(titleLabel, BorderLayout.NORTH);
        patientFrame.add(buttonPanel, BorderLayout.CENTER);

        // Make the frame visible
        patientFrame.setVisible(true);
    }

    public void generateAppointmentTimes() {
        // Create the frame
        JFrame frame = new JFrame("Generate Appointment Times");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Input panel setup
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel startTimeLabel = new JLabel("Start Time (HH 24-hour format):");
        JTextField startTimeField = new JTextField();
        JLabel endTimeLabel = new JLabel("End Time (HH 24-hour format):");
        JTextField endTimeField = new JTextField();
        JLabel dateLabel = new JLabel("Date (DD-MM-YYYY):");
        JTextField dateField = new JTextField();
        JButton generateButton = new JButton("Generate Slots");
        JButton returnToDashboardButton = new JButton("Return to Dashboard");

        // Return to dashboard action
        returnToDashboardButton.addActionListener(e -> {
            frame.dispose();
            doctorDashboard();
        });

        // Add components to input panel
        inputPanel.add(startTimeLabel);
        inputPanel.add(startTimeField);
        inputPanel.add(endTimeLabel);
        inputPanel.add(endTimeField);
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        inputPanel.add(generateButton);
        inputPanel.add(returnToDashboardButton);

        // Status area setup
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);

        // Generate button action
        generateButton.addActionListener(e -> {
            try {
                int startHour = Integer.parseInt(startTimeField.getText());
                int endHour = Integer.parseInt(endTimeField.getText());
                String[] dateParts = dateField.getText().split("-");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);

                if (startHour >= endHour) {
                    statusArea.setText("Start time must be less than end time. Please try again.");
                    return;
                }

                Date date = new Date(year, month, day);
                RandomAccessFile pen = new RandomAccessFile("appointment_times.txt", "rw");

                StringBuilder timeSlotsDisplay = new StringBuilder("Generated Time Slots:\n");
                LocalTime[][] timeSlots = new LocalTime[MAX_APPOINTMENTS][2];
                int slotIndex = 0;

                // Generate time slots
                for (LocalTime time = LocalTime.of(startHour, 0);
                     time.plusMinutes(30).isBefore(LocalTime.of(endHour, 0)) && slotIndex < MAX_APPOINTMENTS;
                     time = time.plusMinutes(30)) {

                    LocalTime endTimeSlot = time.plusMinutes(30);
                    timeSlotsDisplay.append(time).append(" - ").append(endTimeSlot).append("\n");
                    timeSlots[slotIndex][0] = time;
                    timeSlots[slotIndex][1] = endTimeSlot;
                    slotIndex++;
                }

                statusArea.setText(timeSlotsDisplay.toString());

                // Save appointments to file
                for (int appt = 1; appt <= MAX_APPOINTMENTS; appt++) {
                    pen.seek((appt - 1) * recordSize);
                    pen.writeInt(appt);
                    //pen.writeChars(new SimpleDateFormat("dd-MM-yyyy").format(date));
                    // String.format("%02d-%02d-%04d", day, month, year));
                    pen.writeInt(timeSlots[appt - 1][0].getHour());
                    pen.writeChars(String.format("%02d-%02d-%04d", date.getDay(), date.getMonth(), date.getYear()));
                    pen.writeInt(timeSlots[appt - 1][1].getHour());
                }
                pen.close();
            } catch (IOException | NumberFormatException ex) {
                statusArea.setText("An error occurred while generating appointments.");
            }
        });

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void viewAppointments() {
        RandomAccessFile read = null;
        try {
            // Open the file for reading
            read = new RandomAccessFile("appointment_times.txt", "r");

            // Iterate through the file
            while (read.getFilePointer() < read.length()) {
                int appointmentNumber = read.readInt();
                int patientNumber = read.readInt();
                int doctorNumber = read.readInt();

                // Read schedule details
                char[] schedule = new char[dateSize];
                for (int i = 0; i <= dateSize; i++) {
                    schedule[i] = read.readChar();
                }
                String date = new String(schedule);

                // Read status details
                char[] status = new char[statusSize];
                for (int i = 0; i < statusSize; i++) {
                    status[i] = read.readChar();
                }
                String stat = new String(status);

                int startTime = read.readInt();
                int endTimeSlot = read.readInt();

                // Display appointment details
                System.out.println("------------------------------------------------");
                System.out.println("Dashboard");
                System.out.println("------------------------------------------------");
                System.out.println("AppointmentScheduling.Appointment Number: " + appointmentNumber);
                System.out.println("Patient Number: " + patientNumber);
                System.out.println("Doctor Number: " + doctorNumber);
                System.out.println("Date: " + date);
                System.out.println("Status: " + stat);
                System.out.println("Time: " + startTime + " - " + endTimeSlot);
                System.out.println("------------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void createFile(){
        RandomAccessFile pen=null;
        Appointment appointment = new Appointment();
        try{
            pen = new RandomAccessFile("appointment_times.txt", "rw");
            for(int entry=1; entry<=TotalRecords; entry++){
                pen.seek((entry-1)*recordSize);
                pen.writeInt(getAppointmentNumber());
                pen.writeInt(getPatientNumber());
                pen.writeInt(getDoctorNumber());
                String date = String.format("%-10s", getAppointmentDate()).substring(0,10);
                pen.writeChars(date);
                pen.writeChars(String.format("%-9s", getStatus()).substring(0,9));
                int starttime=0;//time slots
                int endtime=0;//time slots
                pen.writeInt( starttime);
                pen.writeInt( endtime);
                
            }
            if(pen.length() == (TotalRecords*recordSize)){
                System.out.println("File created successfully");
            }
            pen.close();
        }catch(IOException e){
            System.out.println(e);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void start() {
        // Create the frame
        JFrame appointmentFrame = new JFrame("Schedule Appointment");
        appointmentFrame.setSize(600, 600);
        appointmentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Title label setup
        JLabel titleLabel = new JLabel("Welcome to Appointment Scheduling", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Button panel setup
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton patientDashboardButton = new JButton("Patient Dashboard");
        JButton doctorDashboardButton = new JButton("Doctor Dashboard");
        JButton exitButton = new JButton("Exit");

        // Add action listeners to buttons
        patientDashboardButton.addActionListener(e -> {
            appointmentFrame.dispose();
            patientDashboard();
        });

        doctorDashboardButton.addActionListener(e -> {
            appointmentFrame.dispose();
            doctorDashboard();
        });

        exitButton.addActionListener(e -> {
            appointmentFrame.dispose();
            Dashboard.open();
        });

        // Add buttons to the button panel
        buttonPanel.add(patientDashboardButton);
        buttonPanel.add(doctorDashboardButton);
        buttonPanel.add(exitButton);

        // Add components to the main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add main panel to the frame and make it visible
        appointmentFrame.add(mainPanel);
        appointmentFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Appointment appt = new Appointment();
        appt.start();
    }
}
