import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

/**
 * CodeAlpha — Task 4: Hotel Reservation System (GUI Version)
 * Built with Java Swing
 */
public class HotelReservationSystemGUI extends JFrame {

    // ── Colors ────────────────────────────────────────────────────────────
    static final Color BG      = new Color(18, 24, 38);
    static final Color CARD    = new Color(28, 37, 56);
    static final Color CARD2   = new Color(22, 30, 47);
    static final Color GOLD    = new Color(245, 158, 11);
    static final Color GREEN   = new Color(16, 185, 129);
    static final Color RED     = new Color(239, 68, 68);
    static final Color BLUE    = new Color(99, 102, 241);
    static final Color TEXT    = new Color(248, 250, 252);
    static final Color SUBTEXT = new Color(148, 163, 184);
    static final Color BORDER  = new Color(45, 60, 88);
    static final Font  TITLE_F = new Font("Segoe UI", Font.BOLD, 20);
    static final Font  HDR_F   = new Font("Segoe UI", Font.BOLD, 12);
    static final Font  BODY_F  = new Font("Segoe UI", Font.PLAIN, 12);
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── Data ──────────────────────────────────────────────────────────────
    enum RoomType { STANDARD(1500,"Standard","2 Guests • WiFi • TV • AC"),
                    DELUXE(2800,"Deluxe","2 Guests • WiFi • TV • AC • Mini Bar • City View"),
                    SUITE(5500,"Suite","4 Guests • Jacuzzi • Ocean View • Lounge"),
                    PRESIDENTIAL(12000,"Presidential","6 Guests • Private Pool • Butler • Gym"); 
        final double price; final String label, amenities;
        RoomType(double p,String l,String a){price=p;label=l;amenities=a;}
    }
    static class Room { int num; RoomType type; boolean available=true;
        Room(int n,RoomType t){num=n;type=t;}
    }
    static class Booking {
        String id, guestName, phone, email, status="CONFIRMED"; boolean paid=false;
        Room room; LocalDate checkIn, checkOut; int guests; double total;
        Booking(String id,String n,String ph,String em,Room r,LocalDate ci,LocalDate co,int g){
            this.id=id;guestName=n;phone=ph;email=em;room=r;checkIn=ci;checkOut=co;guests=g;
            total=ChronoUnit.DAYS.between(ci,co)*r.type.price;
        }
        long nights(){return ChronoUnit.DAYS.between(checkIn,checkOut);}
    }

    List<Room> rooms = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();
    int bookingCounter = 1000;

    DefaultTableModel roomModel, bookingModel;
    JLabel statRooms, statBooked, statRevenue, statOccupancy;
    JTabbedPane tabs;

    public HotelReservationSystemGUI() {
        initRooms();
        setTitle("CodeAlpha Grand Hotel — Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);

        setContentPane(root);
        refreshAll();
        setVisible(true);
    }

    void initRooms() {
        for (int i=101;i<=105;i++) rooms.add(new Room(i,RoomType.STANDARD));
        for (int i=201;i<=204;i++) rooms.add(new Room(i,RoomType.DELUXE));
        for (int i=301;i<=303;i++) rooms.add(new Room(i,RoomType.SUITE));
        rooms.add(new Room(401,RoomType.PRESIDENTIAL));
        rooms.add(new Room(402,RoomType.PRESIDENTIAL));
    }

    // ── Header ────────────────────────────────────────────────────────────
    JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD); p.setPreferredSize(new Dimension(0,62));
        p.setBorder(BorderFactory.createMatteBorder(0,0,2,0,GOLD));
        JLabel t = new JLabel("  🏨 CodeAlpha Grand Hotel"); t.setFont(TITLE_F); t.setForeground(GOLD);
        JLabel s = new JLabel("⭐⭐⭐⭐⭐  Reservation Management System  "); s.setFont(BODY_F); s.setForeground(SUBTEXT);
        p.add(t,BorderLayout.WEST); p.add(s,BorderLayout.EAST); return p;
    }

    // ── Body ──────────────────────────────────────────────────────────────
    JPanel buildBody() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(BG); p.setBorder(new EmptyBorder(12,12,12,12));
        p.add(buildStats(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setBackground(BG); tabs.setForeground(TEXT); tabs.setFont(HDR_F);
        tabs.addTab("🛏 Rooms",        buildRoomsTab());
        tabs.addTab("📅 New Booking",  buildBookingTab());
        tabs.addTab("📋 All Bookings", buildBookingsTab());
        tabs.addTab("💳 Payment",      buildPaymentTab());
        tabs.addTab("📊 Report",       buildReportTab());

        p.add(tabs, BorderLayout.CENTER);
        return p;
    }

    // ── Stats Row ─────────────────────────────────────────────────────────
    JPanel buildStats() {
        JPanel p = new JPanel(new GridLayout(1,4,10,0));
        p.setBackground(BG); p.setPreferredSize(new Dimension(0,72));
        statRooms     = tile("🏨 Available",  "—", GREEN);
        statBooked    = tile("📌 Booked",      "—", GOLD);
        statRevenue   = tile("💰 Revenue",     "—", BLUE);
        statOccupancy = tile("📈 Occupancy",   "—", TEXT);
        p.add(statRooms); p.add(statBooked); p.add(statRevenue); p.add(statOccupancy);
        return p;
    }
    JLabel tile(String lbl, String val, Color c) {
        JLabel l = new JLabel(htile(lbl,val,c),JLabel.CENTER);
        l.setOpaque(true); l.setBackground(CARD);
        l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(8,8,8,8)));
        return l;
    }
    String htile(String lbl,String val,Color c){
        return "<html><center><div style='font-size:8px;color:#94a3b8'>"+lbl+"</div><div style='font-size:18px;font-weight:bold;color:"+hex(c)+"'>"+val+"</div></center></html>";
    }

    // ── Rooms Tab ─────────────────────────────────────────────────────────
    JPanel buildRoomsTab() {
        JPanel p = tabPanel();
        String[] cols={"Room","Floor","Type","Rate/Night","Max Guests","Amenities","Status"};
        roomModel = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable t = styledTable(roomModel);
        t.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setForeground("✅ Available".equals(v)?GREEN:RED); l.setFont(HDR_F); l.setHorizontalAlignment(JLabel.CENTER); return l;
            }
        });

        // Filter
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); filter.setBackground(BG);
        filter.add(fLabel("Filter: "));
        String[] types={"All","Standard","Deluxe","Suite","Presidential","Available Only"};
        JComboBox<String> cb = new JComboBox<>(types); styleCombo(cb);
        cb.addActionListener(e -> filterRooms((String)cb.getSelectedItem()));
        filter.add(cb);

        p.add(filter, BorderLayout.NORTH);
        p.add(scrollOf(t), BorderLayout.CENTER);

        // Room category cards at bottom
        JPanel cats = new JPanel(new GridLayout(1,4,8,0)); cats.setBackground(BG); cats.setPreferredSize(new Dimension(0,90));
        for (RoomType rt : RoomType.values()) cats.add(roomCard(rt));
        p.add(cats, BorderLayout.SOUTH);
        return p;
    }
    JPanel roomCard(RoomType rt) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBackground(CARD); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(8,10,8,10)));
        JLabel name=new JLabel(rt.label); name.setFont(HDR_F); name.setForeground(GOLD); name.setAlignmentX(0);
        JLabel price=new JLabel("₹"+String.format("%,.0f",rt.price)+"/night"); price.setFont(BODY_F); price.setForeground(GREEN); price.setAlignmentX(0);
        JLabel am=new JLabel("<html><span style='font-size:9px;color:#94a3b8'>"+rt.amenities+"</span></html>"); am.setAlignmentX(0);
        p.add(name); p.add(Box.createVerticalStrut(2)); p.add(price); p.add(Box.createVerticalStrut(2)); p.add(am);
        return p;
    }
    void filterRooms(String filter) {
        roomModel.setRowCount(0);
        for (Room r : rooms) {
            if ("Available Only".equals(filter) && !r.available) continue;
            if (!"All".equals(filter) && !"Available Only".equals(filter) && !r.type.label.equals(filter)) continue;
            roomModel.addRow(new Object[]{
                "Room "+r.num, "Floor "+r.num/100,
                r.type.label, "₹"+String.format("%,.0f",r.type.price),
                r.type == RoomType.STANDARD?2:r.type==RoomType.DELUXE?2:r.type==RoomType.SUITE?4:6,
                r.type.amenities, r.available?"✅ Available":"❌ Booked"
            });
        }
    }

    // ── New Booking Tab ───────────────────────────────────────────────────
    JPanel buildBookingTab() {
        JPanel p = tabPanel();
        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(BG);
        GridBagConstraints g = new GridBagConstraints(); g.insets=new Insets(6,8,6,8); g.fill=GridBagConstraints.HORIZONTAL;

        JTextField nameF  = inputField("Full name");
        JTextField phoneF = inputField("Phone number");
        JTextField emailF = inputField("Email address");
        JTextField ciF    = inputField("Check-in  (yyyy-MM-dd)");
        JTextField coF    = inputField("Check-out (yyyy-MM-dd)");
        JSpinner guestSp  = new JSpinner(new SpinnerNumberModel(1,1,6,1)); styleSpinner(guestSp);

        // Room selection
        String[] roomOpts = rooms.stream().filter(r->r.available)
            .map(r->"Room "+r.num+" - "+r.type.label+" (₹"+String.format("%,.0f",r.type.price)+"/night)")
            .toArray(String[]::new);
        JComboBox<String> roomCb = new JComboBox<>(roomOpts); styleCombo(roomCb);

        JTextArea preview = new JTextArea(5,0); preview.setBackground(CARD2); preview.setForeground(SUBTEXT);
        preview.setFont(new Font("Monospaced",Font.PLAIN,11)); preview.setEditable(false); preview.setBorder(new EmptyBorder(8,8,8,8));

        JButton calcBtn = btn("🧮 Calculate Cost", BLUE);
        calcBtn.addActionListener(e -> {
            try {
                LocalDate ci=LocalDate.parse(ciF.getText().trim(),FMT);
                LocalDate co=LocalDate.parse(coF.getText().trim(),FMT);
                if(!co.isAfter(ci)){preview.setText("⚠ Check-out must be after check-in.");return;}
                int selIdx = roomCb.getSelectedIndex();
                List<Room> avail = rooms.stream().filter(r->r.available).collect(java.util.stream.Collectors.toList());
                if(selIdx<0||selIdx>=avail.size()){preview.setText("⚠ Select a room.");return;}
                Room room = avail.get(selIdx);
                long nights = ChronoUnit.DAYS.between(ci,co);
                double total = nights*room.type.price;
                preview.setText(String.format("  Room   : %s (%s)%n  Nights : %d%n  Rate   : ₹%.0f/night%n  TOTAL  : ₹%.2f",
                    "Room "+room.num, room.type.label, nights, room.type.price, total));
                preview.setForeground(GREEN);
            } catch(Exception ex){preview.setText("⚠ Enter valid dates (yyyy-MM-dd)."); preview.setForeground(RED);}
        });

        JButton bookBtn = btn("✅ Confirm Booking", GREEN);
        bookBtn.addActionListener(e -> {
            String name=nameF.getText().trim(), phone=phoneF.getText().trim(), email=emailF.getText().trim();
            if(name.isEmpty()||phone.isEmpty()){showError("Name and phone are required.");return;}
            try {
                LocalDate ci=LocalDate.parse(ciF.getText().trim(),FMT);
                LocalDate co=LocalDate.parse(coF.getText().trim(),FMT);
                if(!co.isAfter(ci)){showError("Check-out must be after check-in.");return;}
                if(ci.isBefore(LocalDate.now())){showError("Check-in cannot be in the past.");return;}
                int selIdx=roomCb.getSelectedIndex();
                List<Room> avail=rooms.stream().filter(r->r.available).collect(java.util.stream.Collectors.toList());
                if(selIdx<0||selIdx>=avail.size()){showError("Select a valid room.");return;}
                Room room=avail.get(selIdx); int guests=(int)guestSp.getValue();
                int maxG=room.type==RoomType.STANDARD?2:room.type==RoomType.DELUXE?2:room.type==RoomType.SUITE?4:6;
                if(guests>maxG){showError("This room fits max "+maxG+" guests.");return;}
                String id="BK"+(++bookingCounter);
                Booking b=new Booking(id,name,phone,email,room,ci,co,guests);
                room.available=false; bookings.add(b);
                refreshAll();
                // Reset fields
                nameF.setText("");phoneF.setText("");emailF.setText("");ciF.setText("");coF.setText("");
                // Rebuild room combo
                roomCb.removeAllItems();
                rooms.stream().filter(r->r.available).forEach(r->roomCb.addItem("Room "+r.num+" - "+r.type.label+" (₹"+String.format("%,.0f",r.type.price)+"/night)"));
                JOptionPane.showMessageDialog(this,
                    "✅ Booking Confirmed!\n\nBooking ID: "+id+"\nGuest: "+name+"\nRoom: "+room.num+"\nTotal: ₹"+String.format("%.2f",b.total),
                    "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            } catch(Exception ex){showError("Please enter valid dates in yyyy-MM-dd format.");}
        });

        // Layout
        int row=0;
        addRow(form,g,row++,"Guest Name",nameF); addRow(form,g,row++,"Phone",phoneF);
        addRow(form,g,row++,"Email",emailF); addRow(form,g,row++,"Room",roomCb);
        addRow(form,g,row++,"Check-In",ciF); addRow(form,g,row++,"Check-Out",coF);
        addRow(form,g,row++,"Guests",guestSp);

        g.gridx=0;g.gridy=row;g.gridwidth=2;g.insets=new Insets(10,8,4,8);
        JPanel btnRow=new JPanel(new GridLayout(1,2,8,0));btnRow.setBackground(BG);btnRow.add(calcBtn);btnRow.add(bookBtn);
        form.add(btnRow,g); row++;

        g.gridy=row; g.insets=new Insets(4,8,4,8);
        JScrollPane ps=new JScrollPane(preview);ps.setBackground(CARD2);ps.getViewport().setBackground(CARD2);ps.setBorder(BorderFactory.createLineBorder(BORDER));
        form.add(ps,g);

        JScrollPane fs=new JScrollPane(form);fs.setBackground(BG);fs.getViewport().setBackground(BG);fs.setBorder(null);
        p.add(fs,BorderLayout.CENTER);
        return p;
    }
    void addRow(JPanel p,GridBagConstraints g,int row,String lbl,JComponent field){
        g.gridx=0;g.gridy=row;g.gridwidth=1;g.weightx=0;g.insets=new Insets(5,8,5,4);
        JLabel l=new JLabel(lbl);l.setFont(HDR_F);l.setForeground(SUBTEXT);p.add(l,g);
        g.gridx=1;g.weightx=1;g.insets=new Insets(5,4,5,8);p.add(field,g);
    }

    // ── All Bookings Tab ──────────────────────────────────────────────────
    JPanel buildBookingsTab() {
        JPanel p = tabPanel();
        String[] cols={"Booking ID","Guest","Room","Check-In","Check-Out","Nights","Total","Paid","Status"};
        bookingModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable t=styledTable(bookingModel);

        // Status renderer
        t.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setForeground("CONFIRMED".equals(v)?GREEN:RED); l.setFont(HDR_F); l.setHorizontalAlignment(JLabel.CENTER); return l;
            }
        });
        t.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setForeground("✅ Yes".equals(v)?GREEN:GOLD); l.setHorizontalAlignment(JLabel.CENTER); return l;
            }
        });

        JButton cancelBtn = btn("❌ Cancel Selected Booking", RED);
        cancelBtn.addActionListener(e -> {
            int row=t.getSelectedRow(); if(row<0){showError("Select a booking first.");return;}
            String id=(String)bookingModel.getValueAt(row,0);
            Booking b=findBooking(id); if(b==null) return;
            if("CANCELLED".equals(b.status)){showError("Already cancelled.");return;}
            int confirm=JOptionPane.showConfirmDialog(this,"Cancel booking "+id+" for "+b.guestName+"?","Confirm Cancel",JOptionPane.YES_NO_OPTION);
            if(confirm!=JOptionPane.YES_OPTION) return;
            b.status="CANCELLED"; b.room.available=true;
            long days=ChronoUnit.DAYS.between(LocalDate.now(),b.checkIn);
            double refund=b.paid?(days>=7?b.total:days>=3?b.total*0.5:0):0;
            refreshAll();
            JOptionPane.showMessageDialog(this,"✅ Booking cancelled.\nRefund: ₹"+String.format("%.2f",refund),"Cancelled",JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.LEFT));btns.setBackground(BG);btns.add(cancelBtn);
        p.add(btns,BorderLayout.NORTH);
        p.add(scrollOf(t),BorderLayout.CENTER);
        return p;
    }

    // ── Payment Tab ───────────────────────────────────────────────────────
    JPanel buildPaymentTab() {
        JPanel p = tabPanel();
        JPanel form = new JPanel(); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBackground(BG); form.setBorder(new EmptyBorder(20,40,20,40));

        JLabel title=new JLabel("💳 Process Payment"); title.setFont(TITLE_F); title.setForeground(GOLD); title.setAlignmentX(0);
        JTextField bidF=inputField("Enter Booking ID (e.g. BK1001)");
        String[] methods={"Credit / Debit Card","UPI / Net Banking","Cash"};
        JComboBox<String> methodCb=new JComboBox<>(methods); styleCombo(methodCb); methodCb.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        JTextArea info=new JTextArea(6,0); info.setBackground(CARD2); info.setForeground(TEXT);
        info.setFont(new Font("Monospaced",Font.PLAIN,12)); info.setEditable(false); info.setBorder(new EmptyBorder(10,10,10,10));

        JButton fetchBtn=btn("🔍 Fetch Booking",BLUE);
        fetchBtn.addActionListener(e->{
            Booking b=findBooking(bidF.getText().trim().toUpperCase());
            if(b==null){info.setText("⚠ Booking not found.");info.setForeground(RED);return;}
            if(b.paid){info.setText("✅ Already paid.\nBooking: "+b.id+"\nGuest: "+b.guestName);info.setForeground(GREEN);return;}
            if("CANCELLED".equals(b.status)){info.setText("⚠ Booking is cancelled.");info.setForeground(RED);return;}
            info.setForeground(TEXT);
            info.setText(String.format("  Booking  : %s%n  Guest    : %s%n  Room     : %s (%s)%n  Nights   : %d%n  Amount   : ₹%.2f%n  Status   : %s",
                b.id,b.guestName,"Room "+b.room.num,b.room.type.label,b.nights(),b.total,b.status));
        });

        JButton payBtn=btn("✅ Confirm Payment",GREEN);
        payBtn.addActionListener(e->{
            Booking b=findBooking(bidF.getText().trim().toUpperCase());
            if(b==null){showError("Fetch a valid booking first.");return;}
            if(b.paid){showError("Already paid.");return;}
            if("CANCELLED".equals(b.status)){showError("Cannot pay for a cancelled booking.");return;}
            int c=JOptionPane.showConfirmDialog(this,
                String.format("Confirm payment of ₹%.2f via %s?",b.total,(String)methodCb.getSelectedItem()),
                "Confirm Payment",JOptionPane.YES_NO_OPTION);
            if(c!=JOptionPane.YES_OPTION) return;
            b.paid=true; refreshAll();
            info.setForeground(GREEN);
            info.setText("✅ PAYMENT SUCCESSFUL!\n\n  Amount : ₹"+String.format("%.2f",b.total)+"\n  Method : "+methodCb.getSelectedItem()+"\n  TxnID  : TXN"+(100000+new Random().nextInt(899999)));
        });

        form.add(title); form.add(Box.createVerticalStrut(16));
        form.add(fLabel("Booking ID")); form.add(Box.createVerticalStrut(4)); form.add(bidF); form.add(Box.createVerticalStrut(8));
        form.add(fLabel("Payment Method")); form.add(Box.createVerticalStrut(4)); form.add(methodCb); form.add(Box.createVerticalStrut(10));
        JPanel br=new JPanel(new GridLayout(1,2,8,0));br.setBackground(BG);br.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        br.add(fetchBtn);br.add(payBtn); form.add(br); form.add(Box.createVerticalStrut(12));
        JScrollPane is=new JScrollPane(info);is.setBackground(CARD2);is.getViewport().setBackground(CARD2);is.setBorder(BorderFactory.createLineBorder(BORDER));
        is.setMaximumSize(new Dimension(Integer.MAX_VALUE,160)); form.add(is);

        JScrollPane fs=new JScrollPane(form);fs.setBackground(BG);fs.getViewport().setBackground(BG);fs.setBorder(null);
        p.add(fs,BorderLayout.CENTER); return p;
    }

    // ── Report Tab ────────────────────────────────────────────────────────
    JPanel buildReportTab() {
        JPanel p = tabPanel();
        JTextArea report=new JTextArea(); report.setBackground(CARD2); report.setForeground(TEXT);
        report.setFont(new Font("Monospaced",Font.PLAIN,13)); report.setEditable(false); report.setBorder(new EmptyBorder(16,16,16,16));

        JButton gen=btn("📊 Generate Full Report",GOLD);
        gen.addActionListener(e->{
            long avail=rooms.stream().filter(r->r.available).count();
            long occupied=rooms.size()-avail;
            long confirmed=bookings.stream().filter(b->"CONFIRMED".equals(b.status)).count();
            long cancelled=bookings.stream().filter(b->"CANCELLED".equals(b.status)).count();
            double revenue=bookings.stream().filter(b->b.paid&&"CONFIRMED".equals(b.status)).mapToDouble(b->b.total).sum();
            double occ=rooms.isEmpty()?0:occupied*100.0/rooms.size();

            StringBuilder sb=new StringBuilder();
            sb.append("════════════════════════════════════════\n");
            sb.append("   CODEALPHA GRAND HOTEL — MANAGER REPORT\n");
            sb.append("   Generated: ").append(LocalDate.now().format(FMT)).append("\n");
            sb.append("════════════════════════════════════════\n\n");
            sb.append(String.format("  Total Rooms        : %d%n",rooms.size()));
            sb.append(String.format("  Available Rooms    : %d%n",avail));
            sb.append(String.format("  Occupied Rooms     : %d%n",occupied));
            sb.append(String.format("  Occupancy Rate     : %.1f%%%n%n",occ));
            sb.append(String.format("  Confirmed Bookings : %d%n",confirmed));
            sb.append(String.format("  Cancelled Bookings : %d%n",cancelled));
            sb.append(String.format("  Total Revenue      : ₹%.2f%n%n",revenue));
            sb.append("  ── By Room Type ─────────────────────\n");
            for (RoomType rt : RoomType.values()) {
                long cnt=bookings.stream().filter(b->b.room.type==rt&&"CONFIRMED".equals(b.status)).count();
                double rev=bookings.stream().filter(b->b.room.type==rt&&b.paid).mapToDouble(b->b.total).sum();
                sb.append(String.format("  %-15s: %2d bookings | ₹%.0f revenue%n",rt.label,cnt,rev));
            }
            sb.append("\n  ── Recent Bookings ──────────────────\n");
            bookings.stream().limit(10).forEach(b->sb.append(
                String.format("  %s | %-18s | Room %-3d | ₹%.0f | %s%n",
                    b.id,b.guestName,b.room.num,b.total,b.status)));
            report.setText(sb.toString());
        });

        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT));top.setBackground(BG);top.add(gen);
        p.add(top,BorderLayout.NORTH);
        p.add(scrollOf(report),BorderLayout.CENTER);
        return p;
    }

    // ── Refresh ───────────────────────────────────────────────────────────
    void refreshAll() {
        filterRooms("All");
        // Bookings table
        if(bookingModel!=null){
            bookingModel.setRowCount(0);
            for(Booking b:bookings) bookingModel.addRow(new Object[]{
                b.id,b.guestName,"Room "+b.room.num,b.checkIn.format(FMT),b.checkOut.format(FMT),
                b.nights(),"₹"+String.format("%.0f",b.total),b.paid?"✅ Yes":"⏳ No",b.status});
        }
        // Stats
        long avail=rooms.stream().filter(r->r.available).count();
        long occ=rooms.size()-avail;
        double rev=bookings.stream().filter(b->b.paid&&"CONFIRMED".equals(b.status)).mapToDouble(b->b.total).sum();
        statRooms.setText(htile("🏨 Available",String.valueOf(avail),GREEN));
        statBooked.setText(htile("📌 Booked",String.valueOf(occ),GOLD));
        statRevenue.setText(htile("💰 Revenue","₹"+String.format("%,.0f",rev),BLUE));
        statOccupancy.setText(htile("📈 Occupancy",String.format("%.0f%%",rooms.isEmpty()?0:occ*100.0/rooms.size()),TEXT));
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    Booking findBooking(String id){for(Booking b:bookings)if(b.id.equalsIgnoreCase(id))return b;return null;}

    JTable styledTable(DefaultTableModel m){
        JTable t=new JTable(m); t.setBackground(CARD); t.setForeground(TEXT); t.setFont(BODY_F);
        t.setRowHeight(34); t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,1));
        t.setSelectionBackground(new Color(99,102,241,80)); t.setSelectionForeground(TEXT);
        t.getTableHeader().setBackground(BG); t.getTableHeader().setForeground(SUBTEXT);
        t.getTableHeader().setFont(HDR_F); t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        return t;
    }
    JPanel tabPanel(){JPanel p=new JPanel(new BorderLayout(0,8));p.setBackground(BG);p.setBorder(new EmptyBorder(10,0,0,0));return p;}
    JScrollPane scrollOf(JComponent c){JScrollPane s=new JScrollPane(c);s.setBackground(CARD);s.getViewport().setBackground(CARD);s.setBorder(BorderFactory.createLineBorder(BORDER));return s;}
    JTextField inputField(String ph){
        JTextField f=new JTextField(ph); f.setBackground(CARD2); f.setForeground(SUBTEXT); f.setCaretColor(TEXT); f.setFont(BODY_F);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(6,8,6,8)));
        f.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){if(f.getText().equals(ph)){f.setText("");f.setForeground(TEXT);}}
            public void focusLost(FocusEvent e){if(f.getText().isEmpty()){f.setText(ph);f.setForeground(SUBTEXT);}}
        });
        return f;
    }
    void styleCombo(JComboBox<?> cb){cb.setBackground(CARD2);cb.setForeground(TEXT);cb.setFont(BODY_F);cb.setAlignmentX(0);}
    void styleSpinner(JSpinner sp){sp.setBackground(CARD2);sp.setFont(BODY_F);((JSpinner.DefaultEditor)sp.getEditor()).getTextField().setBackground(CARD2);((JSpinner.DefaultEditor)sp.getEditor()).getTextField().setForeground(TEXT);}
    JButton btn(String text,Color color){
        JButton b=new JButton(text);b.setFont(HDR_F);b.setBackground(color);b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(8,14,8,14));b.setFocusPainted(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(0);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        b.addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){b.setBackground(color.darker());}public void mouseExited(MouseEvent e){b.setBackground(color);}});
        return b;
    }
    JLabel fLabel(String t){JLabel l=new JLabel(t);l.setFont(HDR_F);l.setForeground(SUBTEXT);l.setAlignmentX(0);return l;}
    String hex(Color c){return String.format("#%02x%02x%02x",c.getRed(),c.getGreen(),c.getBlue());}
    void showError(String m){JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE);}

    public static void main(String[] args) {
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ignored){}
        SwingUtilities.invokeLater(HotelReservationSystemGUI::new);
    }
}
