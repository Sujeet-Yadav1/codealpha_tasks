import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HotelReservationSystem extends JFrame {

    // ---------- Theme ----------
    static final Color NAV = new Color(15,23,42);
    static final Color NAV2 = new Color(30,41,59);
    static final Color BG = new Color(245,247,251);
    static final Color WHITE = Color.WHITE;
    static final Color TEXT = new Color(30,41,59);
    static final Color MUTED = new Color(100,116,139);
    static final Color BLUE = new Color(37,99,235);
    static final Color GREEN = new Color(16,185,129);
    static final Color RED = new Color(239,68,68);
    static final Color PURPLE = new Color(139,92,246);
    static final Color ORANGE = new Color(245,158,11);
    static final Color BORDER = new Color(226,232,240);

    // ---------- Models ----------
    static class Room {
        int number;
        String type;
        double price;
        boolean available = true;

        Room(int number, String type, double price) {
            this.number=number; this.type=type; this.price=price;
        }
    }

    static class Booking {
        int id;
        String guest, phone, checkIn, checkOut;
        Room room;
        double amount;

        Booking(int id, String guest, String phone, String in,
                String out, Room room, double amount) {
            this.id=id; this.guest=guest; this.phone=phone;
            this.checkIn=in; this.checkOut=out;
            this.room=room; this.amount=amount;
        }
    }

    java.util.List<Room> rooms = new ArrayList<>();
    java.util.List<Booking> bookings = new ArrayList<>();
    int nextBooking = 1001;

    // ---------- UI ----------
    JPanel content, roomsGrid;
    CardLayout cardLayout;
    JTable bookingTable;
    DefaultTableModel bookingModel;
    JTextField searchField, guestField, phoneField, roomField, nightsField, bookingSearch;
    JComboBox<String> typeBox;
    JLabel availableLabel, bookedLabel, revenueLabel, occupancyLabel;
    JLabel pageTitle;

    HotelReservationSystem() {
        setTitle("StayEase Hotel Management");
        setSize(1280, 780);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createRooms();
        buildApplication();
        refreshEverything();
    }

    void createRooms() {
        rooms.add(new Room(101,"Standard",2000));
        rooms.add(new Room(102,"Standard",2000));
        rooms.add(new Room(103,"Standard",2000));
        rooms.add(new Room(201,"Deluxe",3500));
        rooms.add(new Room(202,"Deluxe",3500));
        rooms.add(new Room(203,"Deluxe",3500));
        rooms.add(new Room(301,"Suite",6000));
        rooms.add(new Room(302,"Suite",6000));
    }

    // ---------- Main Layout ----------
    void buildApplication() {
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(BG);
        right.add(createTopBar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        content = new JPanel(cardLayout);
        content.setBackground(BG);
        content.add(createDashboardPage(), "dashboard");
        content.add(createRoomsPage(), "rooms");
        content.add(createBookingsPage(), "bookings");
        content.add(createAboutPage(), "about");

        right.add(content, BorderLayout.CENTER);
        add(right, BorderLayout.CENTER);
    }

    JPanel createSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(225,0));
        side.setBackground(NAV);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(28,22,25,18));
        top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🏨  StayEase");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI",Font.BOLD,23));

        JLabel sub = new JLabel("HOTEL MANAGEMENT");
        sub.setForeground(new Color(148,163,184));
        sub.setFont(new Font("Segoe UI",Font.BOLD,10));

        top.add(logo);
        top.add(Box.createVerticalStrut(6));
        top.add(sub);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(8,12,0,12));

        menu.add(menuButton("▣   Dashboard","dashboard"));
        menu.add(menuButton("▦   Rooms","rooms"));
        menu.add(menuButton("▤   Reservations","bookings"));
        menu.add(menuButton("ⓘ   About","about"));

        side.add(top,BorderLayout.NORTH);
        side.add(menu,BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(15,14,20,14));
        JButton exit = sidebarButton("↪   Exit");
        exit.addActionListener(e -> System.exit(0));
        bottom.add(exit);
        side.add(bottom,BorderLayout.SOUTH);

        return side;
    }

    JButton menuButton(String text, String page) {
        JButton b = sidebarButton(text);
        b.addActionListener(e -> {
            cardLayout.show(content,page);
            pageTitle.setText(page.equals("dashboard") ? "Dashboard" :
                    page.equals("rooms") ? "Room Management" :
                    page.equals("bookings") ? "Reservations" : "About StayEase");
            refreshEverything();
        });
        return b;
    }

    JButton sidebarButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(200,45));
        b.setPreferredSize(new Dimension(200,45));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setForeground(new Color(203,213,225));
        b.setBackground(NAV);
        b.setBorder(new EmptyBorder(0,15,0,5));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(NAV2);
                b.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(NAV);
                b.setForeground(new Color(203,213,225));
            }
        });
        return b;
    }

    JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);
        top.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,BORDER),
                new EmptyBorder(14,25,14,25)));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Segoe UI",Font.BOLD,23));
        pageTitle.setForeground(TEXT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        right.setOpaque(false);

        JLabel date = new JLabel(
                "Today  •  " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        date.setForeground(MUTED);
        date.setFont(new Font("Segoe UI",Font.PLAIN,12));

        JButton refresh = actionButton("↻ Refresh",new Color(71,85,105));
        refresh.addActionListener(e -> refreshEverything());

        right.add(date);
        right.add(refresh);
        top.add(pageTitle,BorderLayout.WEST);
        top.add(right,BorderLayout.EAST);
        return top;
    }

    // ---------- Dashboard ----------
    JPanel createDashboardPage() {
        JPanel page = pagePanel();
        JPanel stats = new JPanel(new GridLayout(1,4,14,0));
        stats.setOpaque(false);

        availableLabel = statCard(stats,"🟢","Available Rooms","0",GREEN);
        bookedLabel = statCard(stats,"🔴","Booked Rooms","0",RED);
        occupancyLabel = statCard(stats,"📊","Occupancy","0%",BLUE);
        revenueLabel = statCard(stats,"💳","Revenue","₹0",PURPLE);

        page.add(stats,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14,14));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16,0,0,0));

        JPanel quick = whitePanel();
        quick.setBorder(cardBorder());
        quick.setLayout(new BorderLayout());
        JLabel title = sectionTitle("Quick Actions");
        quick.add(title,BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(1,3,12,0));
        actions.setOpaque(false);
        JButton b1=actionButton("🛎  New Reservation",GREEN);
        JButton b2=actionButton("🛏  Manage Rooms",BLUE);
        JButton b3=actionButton("📋  View Reservations",PURPLE);
        b1.addActionListener(e->cardLayout.show(content,"bookings"));
        b2.addActionListener(e->cardLayout.show(content,"rooms"));
        b3.addActionListener(e->cardLayout.show(content,"bookings"));
        actions.add(b1);actions.add(b2);actions.add(b3);
        quick.add(actions,BorderLayout.CENTER);

        JPanel summary = whitePanel();
        summary.setBorder(cardBorder());
        summary.setLayout(new BorderLayout());
        summary.add(sectionTitle("Hotel Overview"),BorderLayout.NORTH);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new Font("Segoe UI",Font.PLAIN,14));
        info.setForeground(TEXT);
        info.setBorder(new EmptyBorder(15,5,5,5));
        info.setText("Welcome to StayEase Hotel Management.\n\n" +
                "Use the sidebar to manage rooms and reservations.\n" +
                "Bookings automatically update room availability and revenue.\n\n" +
                "Demo payment processing is enabled for internship demonstration.");
        summary.add(info);

        body.add(quick,BorderLayout.NORTH);
        body.add(summary,BorderLayout.CENTER);
        page.add(body,BorderLayout.CENTER);
        return page;
    }

    // ---------- Rooms ----------
    JPanel createRoomsPage() {
        JPanel page=pagePanel();

        JPanel head=new JPanel(new BorderLayout(12,0));
        head.setOpaque(false);
        searchField=new JTextField();
        searchField.setPreferredSize(new Dimension(250,38));
        styleField(searchField);
        searchField.putClientProperty("JTextField.placeholderText","Search room...");
        searchField.addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e){refreshRoomsGrid();}
        });

        typeBox=new JComboBox<>(new String[]{"All Rooms","Standard","Deluxe","Suite"});
        typeBox.setPreferredSize(new Dimension(150,38));
        typeBox.addActionListener(e->refreshRoomsGrid());

        head.add(searchField,BorderLayout.WEST);
        head.add(typeBox,BorderLayout.EAST);
        page.add(head,BorderLayout.NORTH);

        roomsGrid=new JPanel(new GridLayout(0,3,15,15));
        roomsGrid.setOpaque(false);

        JScrollPane scroll=new JScrollPane(roomsGrid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(BG);
        page.add(scroll,BorderLayout.CENTER);

        return page;
    }

    void refreshRoomsGrid() {
        if(roomsGrid==null)return;
        roomsGrid.removeAll();
        String query=searchField==null?"":searchField.getText().trim().toLowerCase();
        String type=typeBox==null?"All Rooms":(String)typeBox.getSelectedItem();

        for(Room r:rooms) {
            boolean match=r.type.toLowerCase().contains(query) ||
                    String.valueOf(r.number).contains(query);
            boolean typeMatch=type.equals("All Rooms") || r.type.equals(type);
            if(match && typeMatch) roomsGrid.add(roomCard(r));
        }
        roomsGrid.revalidate();
        roomsGrid.repaint();
    }

    JPanel roomCard(Room r) {
        JPanel p=whitePanel();
        p.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true),
                new EmptyBorder(18,18,18,18)));
        p.setLayout(new BorderLayout(8,8));

        JPanel top=new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel num=new JLabel("Room "+r.number);
        num.setFont(new Font("Segoe UI",Font.BOLD,19));
        num.setForeground(TEXT);
        JLabel status=new JLabel(r.available?"● AVAILABLE":"● BOOKED");
        status.setFont(new Font("Segoe UI",Font.BOLD,11));
        status.setForeground(r.available?GREEN:RED);
        top.add(num,BorderLayout.WEST);
        top.add(status,BorderLayout.EAST);

        JLabel type=new JLabel(r.type);
        type.setFont(new Font("Segoe UI",Font.BOLD,14));
        type.setForeground(BLUE);

        JLabel price=new JLabel("₹"+fmt(r.price)+" / night");
        price.setFont(new Font("Segoe UI",Font.BOLD,16));
        price.setForeground(TEXT);

        JButton action=actionButton(r.available?"Book This Room":"View Booking",
                r.available?GREEN:new Color(100,116,139));
        action.addActionListener(e->{
            if(r.available) {
                cardLayout.show(content,"bookings");
                pageTitle.setText("New Reservation");
                roomField.setText(String.valueOf(r.number));
            } else showBookingForRoom(r.number);
        });

        JPanel middle=new JPanel();
        middle.setOpaque(false);
        middle.setLayout(new BoxLayout(middle,BoxLayout.Y_AXIS));
        middle.add(type);middle.add(Box.createVerticalStrut(8));middle.add(price);

        p.add(top,BorderLayout.NORTH);
        p.add(middle,BorderLayout.CENTER);
        p.add(action,BorderLayout.SOUTH);
        return p;
    }

    // ---------- Bookings ----------
    JPanel createBookingsPage() {
        JPanel page=pagePanel();

        JPanel top=new JPanel(new BorderLayout(12,0));
        top.setOpaque(false);

        JPanel form=whitePanel();
        form.setBorder(cardBorder());
        form.setLayout(new GridBagLayout());

        GridBagConstraints g=new GridBagConstraints();
        g.insets=new Insets(5,6,5,6);
        g.fill=GridBagConstraints.HORIZONTAL;
        g.weightx=1;

        guestField=new JTextField();
        phoneField=new JTextField();
        roomField=new JTextField();
        nightsField=new JTextField();

        addFormField(form,g,0,"Guest Name",guestField);
        addFormField(form,g,1,"Phone",phoneField);
        addFormField(form,g,2,"Room Number",roomField);
        addFormField(form,g,3,"Nights",nightsField);

        JButton book=actionButton("✓  Confirm Reservation",GREEN);
        book.addActionListener(e->makeBooking());

        g.gridx=0;g.gridy=4;g.gridwidth=2;
        form.add(book,g);

        top.add(form,BorderLayout.WEST);

        JPanel search=whitePanel();
        search.setBorder(cardBorder());
        search.setLayout(new BorderLayout(8,8));
        JLabel st=sectionTitle("Find Reservation");
        bookingSearch=new JTextField();
        styleField(bookingSearch);
        JButton find=actionButton("🔎 Search",BLUE);
        find.addActionListener(e->findBooking());
        search.add(st,BorderLayout.NORTH);
        search.add(bookingSearch,BorderLayout.CENTER);
        search.add(find,BorderLayout.SOUTH);
        top.add(search,BorderLayout.CENTER);

        page.add(top,BorderLayout.NORTH);

        bookingModel=new DefaultTableModel(
                new Object[]{"ID","Guest","Phone","Room","Category","Nights","Check-in","Total"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        bookingTable=new JTable(bookingModel);
        bookingTable.setRowHeight(38);
        bookingTable.setFont(new Font("Segoe UI",Font.PLAIN,13));
        bookingTable.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        bookingTable.getTableHeader().setBackground(NAV);
        bookingTable.getTableHeader().setForeground(Color.WHITE);
        bookingTable.setSelectionBackground(new Color(219,234,254));

        JPanel tablePanel=whitePanel();
        tablePanel.setBorder(cardBorder());
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(sectionTitle("Active Reservations"),BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(bookingTable),BorderLayout.CENTER);

        JButton cancel=actionButton("❌  Cancel Selected Reservation",RED);
        cancel.addActionListener(e->cancelSelected());
        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);bottom.add(cancel);
        tablePanel.add(bottom,BorderLayout.SOUTH);

        page.add(tablePanel,BorderLayout.CENTER);
        return page;
    }

    void addFormField(JPanel p,GridBagConstraints g,int row,String label,JTextField field) {
        g.gridx=0;g.gridy=row;g.gridwidth=1;
        JLabel l=new JLabel(label);l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI",Font.BOLD,11));p.add(l,g);
        g.gridx=1;styleField(field);p.add(field,g);
    }

    // ---------- Actions ----------
    void makeBooking() {
        try {
            String guest=guestField.getText().trim();
            String phone=phoneField.getText().trim();
            int rn=Integer.parseInt(roomField.getText().trim());
            int nights=Integer.parseInt(nightsField.getText().trim());

            if(guest.isEmpty() || phone.isEmpty() || nights<=0)
                throw new Exception();

            Room selected=null;
            for(Room r:rooms)
                if(r.number==rn) selected=r;

            if(selected==null) {
                error("Room "+rn+" does not exist.");
                return;
            }
            if(!selected.available) {
                error("Room "+rn+" is already booked.");
                return;
            }

            double total=selected.price*nights;

            int confirm=JOptionPane.showConfirmDialog(
                    this,
                    "<html><b>Reservation Summary</b><br><br>"+
                    "Guest: "+guest+"<br>"+
                    "Room: "+selected.number+" ("+selected.type+")<br>"+
                    "Nights: "+nights+"<br>"+
                    "<b>Total: ₹"+fmt(total)+"</b><br><br>"+
                    "Proceed with simulated payment?</html>",
                    "Confirm Booking",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if(confirm!=JOptionPane.YES_OPTION)return;

            selected.available=false;
            String today=LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String out=LocalDate.now().plusDays(nights)
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

            bookings.add(new Booking(nextBooking++,guest,phone,today,out,
                    selected,total));

            refreshEverything();
            clearBookingForm();

            JOptionPane.showMessageDialog(this,
                    "<html><b>✓ Booking Confirmed!</b><br><br>"+
                    "Reservation ID: <b>"+(nextBooking-1)+"</b><br>"+
                    "Payment: ₹"+fmt(total)+"<br>"+
                    "Check-out: "+out+"</html>",
                    "Success",JOptionPane.INFORMATION_MESSAGE);

        } catch(Exception e) {
            error("Please enter valid guest, phone, room and nights.");
        }
    }

    void cancelSelected() {
        int row=bookingTable.getSelectedRow();
        if(row<0){error("Select a reservation first.");return;}

        int id=(Integer)bookingModel.getValueAt(row,0);
        Booking found=null;
        for(Booking b:bookings)if(b.id==id)found=b;

        if(found==null)return;

        int confirm=JOptionPane.showConfirmDialog(this,
                "Cancel reservation #"+id+" for "+found.guest+"?",
                "Cancel Reservation",JOptionPane.YES_NO_OPTION);

        if(confirm==JOptionPane.YES_OPTION){
            found.room.available=true;
            bookings.remove(found);
            refreshEverything();
            JOptionPane.showMessageDialog(this,
                    "Reservation cancelled successfully.",
                    "Cancelled",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void findBooking() {
        try {
            int id=Integer.parseInt(bookingSearch.getText().trim());
            for(Booking b:bookings) {
                if(b.id==id) {
                    JOptionPane.showMessageDialog(this,
                            "<html><b>Reservation #"+b.id+"</b><br><br>"+
                            "Guest: "+b.guest+"<br>"+
                            "Phone: "+b.phone+"<br>"+
                            "Room: "+b.room.number+" ("+b.room.type+")<br>"+
                            "Check-in: "+b.checkIn+"<br>"+
                            "Check-out: "+b.checkOut+"<br>"+
                            "Total: ₹"+fmt(b.amount)+"</html>",
                            "Reservation Details",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            error("Reservation not found.");
        }catch(Exception e){error("Enter a valid booking ID.");}
    }

    void showBookingForRoom(int roomNumber) {
        for(Booking b:bookings)
            if(b.room.number==roomNumber) {
                JOptionPane.showMessageDialog(this,
                        "<html><b>Room "+roomNumber+" is booked</b><br><br>"+
                        "Guest: "+b.guest+"<br>"+
                        "Booking ID: "+b.id+"<br>"+
                        "Check-out: "+b.checkOut+"</html>",
                        "Room Details",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
    }

    void clearBookingForm() {
        guestField.setText("");phoneField.setText("");
        roomField.setText("");nightsField.setText("");
    }

    // ---------- Refresh ----------
    void refreshEverything() {
        int available=0,booked=0;double revenue=0;
        for(Room r:rooms){if(r.available)available++;else booked++;}
        for(Booking b:bookings)revenue+=b.amount;

        if(availableLabel!=null)availableLabel.setText(""+available);
        if(bookedLabel!=null)bookedLabel.setText(""+booked);
        if(revenueLabel!=null)revenueLabel.setText("₹"+fmt(revenue));
        if(occupancyLabel!=null)
            occupancyLabel.setText(Math.round(booked*100.0/rooms.size())+"%");

        refreshRoomsGrid();
        refreshBookings();
    }

    void refreshBookings() {
        if(bookingModel==null)return;
        bookingModel.setRowCount(0);
        for(Booking b:bookings)
            bookingModel.addRow(new Object[]{
                    b.id,b.guest,b.phone,b.room.number,b.room.type,
                    b.checkOut.equals(b.checkIn)?"1":"—",b.checkIn,"₹"+fmt(b.amount)
            });
    }

    // ---------- About ----------
    JPanel createAboutPage() {
        JPanel p=pagePanel();
        JPanel box=whitePanel();
        box.setBorder(cardBorder());
        box.setLayout(new BoxLayout(box,BoxLayout.Y_AXIS));

        JLabel logo=new JLabel("🏨 StayEase Hotel Management");
        logo.setFont(new Font("Segoe UI",Font.BOLD,28));
        logo.setForeground(BLUE);
        JLabel v=new JLabel("Professional Java Swing Reservation System");
        v.setFont(new Font("Segoe UI",Font.PLAIN,16));
        v.setForeground(MUTED);

        JTextArea a=new JTextArea(
                "\nFEATURES\n\n"+
                "✓ Room categories: Standard, Deluxe and Suite\n"+
                "✓ Real-time room availability\n"+
                "✓ Guest reservation management\n"+
                "✓ Simulated payment confirmation\n"+
                "✓ Booking cancellation\n"+
                "✓ Reservation search\n"+
                "✓ Revenue and occupancy dashboard\n"+
                "✓ Professional Java Swing interface\n\n"+
                "Developed as a CodeAlpha Java Programming internship project."
        );
        a.setEditable(false);a.setOpaque(false);
        a.setFont(new Font("Segoe UI",Font.PLAIN,15));a.setForeground(TEXT);

        box.add(logo);box.add(Box.createVerticalStrut(6));box.add(v);
        box.add(Box.createVerticalStrut(20));box.add(a);
        p.add(box,BorderLayout.CENTER);
        return p;
    }

    // ---------- Helpers ----------
    JPanel pagePanel() {
        JPanel p=new JPanel(new BorderLayout(0,0));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(20,22,22,22));
        return p;
    }

    JPanel whitePanel() {
        JPanel p=new JPanel();
        p.setBackground(WHITE);
        return p;
    }

    Border cardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER,1,true),
                new EmptyBorder(16,16,16,16));
    }

    JLabel sectionTitle(String text) {
        JLabel l=new JLabel(text);
        l.setFont(new Font("Segoe UI",Font.BOLD,17));
        l.setForeground(TEXT);
        return l;
    }

    JLabel statCard(JPanel parent,String icon,String title,String value,Color color) {
        JPanel p=new JPanel(new BorderLayout(12,0));
        p.setBackground(WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true),
                new EmptyBorder(14,16,14,16)));

        JLabel i=new JLabel(icon);
        i.setFont(new Font("Segoe UI Emoji",Font.PLAIN,25));
        p.add(i,BorderLayout.WEST);

        JPanel q=new JPanel();
        q.setOpaque(false);
        q.setLayout(new BoxLayout(q,BoxLayout.Y_AXIS));

        JLabel t=new JLabel(title);
        t.setFont(new Font("Segoe UI",Font.PLAIN,12));
        t.setForeground(MUTED);

        JLabel v=new JLabel(value);
        v.setFont(new Font("Segoe UI",Font.BOLD,22));
        v.setForeground(color);

        q.add(t);q.add(Box.createVerticalStrut(5));q.add(v);
        p.add(q,BorderLayout.CENTER);
        parent.add(p);
        return v;
    }

    JButton actionButton(String text,Color color) {
        JButton b=new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(180,40));
        return b;
    }

    void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(203,213,225),1,true),
                new EmptyBorder(8,10,8,10)));
    }

    String fmt(double x){return String.format("%,.2f",x);}

    void error(String message) {
        JOptionPane.showMessageDialog(this,message,"Attention",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ignored){}

        SwingUtilities.invokeLater(() -> {
            HotelReservationSystem app=new HotelReservationSystem();
            app.setVisible(true);
        });
    }
}
