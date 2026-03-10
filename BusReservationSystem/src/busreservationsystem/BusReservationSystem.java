package busreservationsystem;

import java.io.*;
import java.util.*;

public class BusReservationSystem {

    static final String FILE_NAME = "Seats.txt";
    static final int TOTAL_SEATS = 5;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ensureFileExists();

        while (true) {
            System.out.println("\n--- BUS RESERVATION SYSTEM ---");
            displaySeats();
            System.out.println("\nOptions: [1] Reserve [2] Cancel [Q] Quit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("q")) {
                System.out.println("Exiting system. Goodbye!");
                break;
            }

            try {
                if (choice.equals("1")) {
                    System.out.print("Enter seat number (1-" + TOTAL_SEATS + "): ");
                    int seat = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter passenger name: ");
                    String name = scanner.nextLine();
                    reserveSeat(seat, name);
                } else if (choice.equals("2")) {
                    System.out.print("Enter seat number to cancel: ");
                    int seat = Integer.parseInt(scanner.nextLine());
                    cancelReservation(seat);
                } else {
                    System.out.println("Invalid option. Please choose 1, 2, or Q.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
        scanner.close();
    }

    // --- EXISTING METHODS (STAY THE SAME) ---

    static void ensureFileExists() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (int i = 1; i <= TOTAL_SEATS; i++) {
                    writer.write(i + ",Available");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error initializing file.");
            }
        }
    }

    static void displaySeats() {
        System.out.println("Current Seat Status:");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                System.out.printf("Seat #%s: %s%n", parts[0], 
                    parts[1].equals("Available") ? "[ EMPTY ]" : "[ RESERVED: " + parts[1] + " ]");
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    static void reserveSeat(int seatNumber, String name) {
        updateSeatStatus(seatNumber, name, true);
    }

    // --- NEW CANCELLATION METHOD ---

    static void cancelReservation(int seatNumber) {
        updateSeatStatus(seatNumber, "Available", false);
    }

    /**
     * Helper method to handle both Reservations and Cancellations.
     * Keeps the code "DRY" (Don't Repeat Yourself).
     */
    static void updateSeatStatus(int seatNumber, String newValue, boolean isReserving) {
        if (seatNumber < 1 || seatNumber > TOTAL_SEATS) {
            System.out.println("Error: Seat number out of range.");
            return;
        }

        List<String> lines = new ArrayList<>();
        boolean actionTaken = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int currentSeat = Integer.parseInt(parts[0]);
                String currentStatus = parts[1];

                if (currentSeat == seatNumber) {
                    if (isReserving && currentStatus.equals("Available")) {
                        lines.add(currentSeat + "," + newValue);
                        actionTaken = true;
                    } else if (!isReserving && !currentStatus.equals("Available")) {
                        lines.add(currentSeat + ",Available");
                        actionTaken = true;
                        System.out.println("SUCCESS: Reservation for Seat " + seatNumber + " has been cancelled.");
                    } else {
                        // Logic for why it failed
                        System.out.println(isReserving ? "ERROR: Seat already taken." : "ERROR: Seat is already empty.");
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("File Error.");
            return;
        }

        if (actionTaken) {
            saveToFile(lines);
            if (isReserving) System.out.println("SUCCESS: Seat " + seatNumber + " reserved for " + newValue);
        }
    }

    static void saveToFile(List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving to file.");
        }
    }
}