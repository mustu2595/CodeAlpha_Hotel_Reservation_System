import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

class Room {
    private int roomNumber;
    private String category;
    private double pricePerNight;
    private boolean isAvailable;

    public Room(int roomNumber, String category, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isAvailable = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", category='" + category + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", isAvailable=" + isAvailable +
                '}';
    }
}

class Reservation {
    private int reservationId;
    private String guestName;
    private Room room;
    private Date checkInDate;
    private Date checkOutDate;
    private double totalAmount;
    private boolean isPaid;

    public Reservation(int reservationId, String guestName, Room room, Date checkInDate, Date checkOutDate) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = calculateTotalAmount();
        this.isPaid = false;
    }

    private double calculateTotalAmount() {
        long diffInMillies = Math.abs(checkOutDate.getTime() - checkInDate.getTime());
        long diff = diffInMillies / (1000 * 60 * 60 * 24);
        return diff * room.getPricePerNight();
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public Room getRoom() {
        return room;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", guestName='" + guestName + '\'' +
                ", room=" + room +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", totalAmount=" + totalAmount +
                ", isPaid=" + isPaid +
                '}';
    }
}

class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;
    private int nextReservationId;

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        nextReservationId = 1;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> searchAvailableRooms(String category) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable() && room.getCategory().equalsIgnoreCase(category)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    public Reservation makeReservation(String guestName, int roomNumber, Date checkInDate, Date checkOutDate) {
        Optional<Room> roomOpt = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber && r.isAvailable()).findFirst();
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setAvailable(false);
            Reservation reservation = new Reservation(nextReservationId++, guestName, room, checkInDate, checkOutDate);
            reservations.add(reservation);
            return reservation;
        } else {
            return null;
        }
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean processPayment(Reservation reservation, String paymentDetails) {
        // Simulate payment processing. In a real application, integrate with a payment gateway.
        if (paymentDetails != null && !paymentDetails.isEmpty()) {
            reservation.setPaid(true);
            return true;
        }
        return false;
    }
}

public class HotelReservationSystem {

    public static void main(String[] args) throws ParseException {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(101, "Single", 50.0));
        hotel.addRoom(new Room(102, "Double", 80.0));
        hotel.addRoom(new Room(103, "Suite", 150.0));

        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        while (true) {
            System.out.println("Hotel Reservation System");
            System.out.println("1. Search for available rooms");
            System.out.println("2. Make a reservation");
            System.out.println("3. View reservations");
            System.out.println("4. Process payment");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter room category: ");
                    String category = scanner.next();
                    List<Room> availableRooms = hotel.searchAvailableRooms(category);
                    if (availableRooms.isEmpty()) {
                        System.out.println("No available rooms in this category.");
                    } else {
                        for (Room room : availableRooms) {
                            System.out.println(room);
                        }
                    }
                    break;
                case 2:
                    System.out.print("Enter guest name: ");
                    String guestName = scanner.next();
                    System.out.print("Enter room number: ");
                    int roomNumber = scanner.nextInt();
                    System.out.print("Enter check-in date (yyyy-MM-dd): ");
                    String checkInDateStr = scanner.next();
                    Date checkInDate = dateFormat.parse(checkInDateStr);
                    System.out.print("Enter check-out date (yyyy-MM-dd): ");
                    String checkOutDateStr = scanner.next();
                    Date checkOutDate = dateFormat.parse(checkOutDateStr);

                    Reservation reservation = hotel.makeReservation(guestName, roomNumber, checkInDate, checkOutDate);
                    if (reservation != null) {
                        System.out.println("Reservation successful: " + reservation);
                    } else {
                        System.out.println("Failed to make reservation. Room might be unavailable.");
                    }
                    break;
                case 3:
                    List<Reservation> reservations = hotel.getReservations();
                    for (Reservation res : reservations) {
                        System.out.println(res);
                    }
                    break;
                case 4:
                    System.out.print("Enter reservation ID: ");
                    int reservationId = scanner.nextInt();
                    Optional<Reservation> reservationOpt = hotel.getReservations().stream().filter(r -> r.getReservationId() == reservationId).findFirst();
                    if (reservationOpt.isPresent()) {
                        Reservation res = reservationOpt.get();
                        if (!res.isPaid()) {
                            System.out.print("Enter payment details: ");
                            String paymentDetails = scanner.next();
                            boolean paymentSuccess = hotel.processPayment(res, paymentDetails);
                            if (paymentSuccess) {
                                System.out.println("Payment successful: " + res);
                            } else {
                                System.out.println("Payment failed. Try again.");
                            }
                        } else {
                            System.out.println("Reservation is already paid.");
                        }
                    } else {
                        System.out.println("Invalid reservation ID.");
                    }
                    break;
                case 5:
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
