class TicketBookingSystem {
    private boolean[] seats;

    public TicketBookingSystem(int totalSeats) {
        seats = new boolean[totalSeats];
    }

    public synchronized boolean bookSeat(int seatNumber, String userName) {
        if (seatNumber < 1 || seatNumber > seats.length) {
            System.out.println(userName + ": Invalid seat number!");
            return false;
        }
        if (!seats[seatNumber - 1]) {
            seats[seatNumber - 1] = true;
            System.out.println(userName + " booked seat " + seatNumber);
            return true;
        } else {
            System.out.println(userName + ": Seat " + seatNumber + " is already booked!");
            return false;
        }
    }
}

class UserThread extends Thread {
    private TicketBookingSystem system;
    private int seatNumber;
    private String userName;

    public UserThread(TicketBookingSystem system, int seatNumber, String userName, int priority) {
        this.system = system;
        this.seatNumber = seatNumber;
        this.userName = userName;
        setPriority(priority);
    }

    @Override
    public void run() {
        system.bookSeat(seatNumber, userName);
    }
}

public class TicketBookingMain {
    public static void main(String[] args) {
        TicketBookingSystem system = new TicketBookingSystem(5);

        System.out.println("\nTest Case 1: No Seats Available Initially");
        System.out.println("No bookings yet.");

        System.out.println("\nTest Case 2: Successful Booking");
        new UserThread(system, 1, "Anish (VIP)", Thread.MAX_PRIORITY).start();
        new UserThread(system, 2, "Bobby (Regular)", Thread.NORM_PRIORITY).start();
        new UserThread(system, 3, "Charlie (VIP)", Thread.MAX_PRIORITY).start();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        System.out.println("\nTest Case 3: Thread Priorities (VIP First)");
        new UserThread(system, 4, "Bobby (Regular)", Thread.MIN_PRIORITY).start();
        new UserThread(system, 4, "Anish (VIP)", Thread.MAX_PRIORITY).start();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        System.out.println("\nTest Case 4: Preventing Double Booking");
        new UserThread(system, 1, "Anish (VIP)", Thread.MAX_PRIORITY).start();
        new UserThread(system, 1, "Bobby (Regular)", Thread.NORM_PRIORITY).start();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        System.out.println("\nTest Case 5: Booking After All Seats Are Taken");
        new UserThread(system, 3, "Derek (Regular)", Thread.NORM_PRIORITY).start();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        System.out.println("\nTest Case 6: Invalid Seat Selection");
        new UserThread(system, 0, "Ethan (Regular)", Thread.NORM_PRIORITY).start();
        new UserThread(system, 6, "Frank (Regular)", Thread.NORM_PRIORITY).start();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        System.out.println("\nTest Case 7: Simultaneous Bookings (Concurrency Test)");
        Thread[] users = new Thread[10];
        for (int i = 0; i < 10; i++) {
            int seat = (i % 5) + 1;
            users[i] = new UserThread(system, seat, "User " + (i + 1), (i % 2 == 0) ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
            users[i].start();
        }

        for (Thread user : users) {
            try {
                user.join();
            } catch (InterruptedException e) {}
        }
    }
}
