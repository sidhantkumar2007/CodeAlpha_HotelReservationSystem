# 🏨 Hotel Reservation System

A Java Swing desktop application simulating a complete hotel reservation management system.

---

## Overview

**CodeAlpha Grand Hotel** is a full-featured, dark-themed hotel management GUI that lets staff manage room availability, make and cancel bookings, process payments, and generate detailed manager reports — all in one professional desktop application.

---

## Features

### 🛏 Room Management
- 15 rooms across 4 categories: Standard, Deluxe, Suite, and Presidential
- Real-time availability status (✅ Available / ❌ Booked)
- Filter rooms by type or availability
- Room category cards showing amenities and pricing at a glance

### 📅 New Booking
- Full guest information form (name, phone, email)
- Date validation — no past check-in, check-out must be after check-in
- Guest count validation per room capacity
- Live cost calculator showing nights, rate, and total before confirming
- Auto-generated unique Booking ID (e.g. `BK1001`)

### 📋 Booking Management
- View all bookings in a sortable table
- Cancel any confirmed booking with one click
- Smart refund policy on cancellation:
  - 7+ days before check-in → **100% refund**
  - 3–6 days before check-in → **50% refund**
  - Less than 3 days → **No refund**

### 💳 Payment Processing
- Fetch any booking by ID and view full details
- Supports Credit/Debit Card, UPI/Net Banking, and Cash
- Generates a unique Transaction ID on successful payment
- Prevents duplicate payments and payments on cancelled bookings

### 📊 Manager Report
- Full hotel summary: total rooms, availability, occupancy rate
- Confirmed vs cancelled booking counts
- Total revenue from paid bookings
- Revenue and booking breakdown by room type
- List of 10 most recent bookings

### 📊 Live Stats Dashboard
- Always-visible top bar showing Available Rooms, Booked Rooms, Total Revenue, and Occupancy %

---

## Room Types & Pricing

| Type          | Price/Night | Max Guests | Highlights                              |
|---------------|-------------|------------|-----------------------------------------|
| Standard      | ₹1,500      | 2          | WiFi, TV, AC                            |
| Deluxe        | ₹2,800      | 2          | WiFi, TV, AC, Mini Bar, City View       |
| Suite         | ₹5,500      | 4          | Jacuzzi, Ocean View, Lounge             |
| Presidential  | ₹12,000     | 6          | Private Pool, Butler Service, Gym       |

---

## Hotel Inventory

| Floor | Rooms       | Type          | Count |
|-------|-------------|---------------|-------|
| 1     | 101 – 105   | Standard      | 5     |
| 2     | 201 – 204   | Deluxe        | 4     |
| 3     | 301 – 303   | Suite         | 3     |
| 4     | 401 – 402   | Presidential  | 2     |

**Total: 14 Rooms**

---

## Project Structure

```
HotelReservationSystemGUI.java             # Main source file
HotelReservationSystemGUI.class            # Compiled main class
HotelReservationSystemGUI$Room.class       # Inner Room data model
HotelReservationSystemGUI$Booking.class    # Inner Booking data model
HotelReservationSystemGUI$RoomType.class   # Enum for room categories
HotelReservationSystemGUI$1.class  through
HotelReservationSystemGUI$7.class          # Anonymous inner classes (renderers, listeners)
```

---

## Requirements

- **Java 17 or higher**
- No external libraries — standard Java SE only (`javax.swing`, `java.awt`, `java.time`, `java.util`)

---

## How to Run

### Option 1 — Run from pre-compiled `.class` files

Place all `.class` files in the same directory and run:

```bash
java HotelReservationSystemGUI
```

### Option 2 — Compile and run from source

```bash
javac HotelReservationSystemGUI.java
java HotelReservationSystemGUI
```

---

## How to Use

1. **Browse rooms** → Go to the 🛏 Rooms tab, use the filter dropdown to find available rooms.
2. **Make a booking** → Go to 📅 New Booking, fill in guest details, select room and dates, click **Calculate Cost**, then **Confirm Booking**.
3. **View all bookings** → Go to 📋 All Bookings to see every reservation and cancel if needed.
4. **Process payment** → Go to 💳 Payment, enter the Booking ID, select payment method, and click **Confirm Payment**.
5. **Generate report** → Go to 📊 Report and click **Generate Full Report** for a complete hotel summary.

---

## Author

**Sidhant Kumar**
🎓 Java Programming Intern @ CodeAlpha
🐙 GitHub: [sidhantkumar2007](https://github.com/sidhantkumar2007)
📌 Project: CodeAlpha Internship — Task 4
