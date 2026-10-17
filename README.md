# 🧾 ClearBill Solutions: Smart Invoice and Billing Manager
### *Accurate Accounts, Honest Bills*

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![TailwindCSS](https://img.shields.io/badge/TailwindCSS-v3.x-blue.svg)](https://tailwindcss.com/)
[![Chart.js](https://img.shields.io/badge/Chart.js-v4.x-F5788D.svg)](https://www.chartjs.org/)
[![OpenAPI / Swagger](https://img.shields.io/badge/OpenAPI-Swagger%20UI-brightgreen.svg)](http://localhost:8080/swagger-ui.html)
[![PDF Engine](https://img.shields.io/badge/PDF%20Engine-Flying%20Saucer%20OpenPDF-indigo.svg)](https://github.com/flyingsaucerproject/flyingsaucer)
[![Database](https://img.shields.io/badge/Database-H2%20(Dev)%20%7C%20MySQL%20(Prod)-blueviolet.svg)](https://www.mysql.com/)

A modern, full-stack GST-ready billing and invoice management system built with **Java 21**, **Spring Boot 3.4.x**, **Spring Data JPA**, **Thymeleaf**, and **Flying Saucer OpenPDF**. Crafted with a clean, professional SaaS interface and engineered specifically for Indian businesses.

---

## 📖 Table of Contents
1. [Project Overview](#-project-overview)
2. [Key Features](#-key-features)
3. [English Language System & Central Dictionary](#-english-language-system--central-dictionary)
4. [The 5 Handcrafted Invoice Templates](#-the-5-handcrafted-invoice-templates)
5. [GST & Indian Currency System](#-gst--indian-currency-system)
6. [Tech Stack & Layered Architecture](#-tech-stack--layered-architecture)
7. [Interactive API Docs (Swagger UI)](#-interactive-api-docs-swagger-ui)
8. [Getting Started (Run Locally)](#-getting-started-run-locally)
9. [Running Automated Tests](#-running-automated-tests)
10. [Docker Deployment](#-docker-deployment)
11. [REST API Endpoints](#-rest-api-endpoints)
12. [Walkthrough & UI Guide](#-walkthrough--ui-guide)
13. [Architecture & Design FAQ](#-architecture--design-faq)

---

## 🌿 Project Overview

**ClearBill Solutions** (*"Accurate Accounts, Honest Bills"*) is a smart, all-in-one billing management application tailored for Indian traders, freelancers, small businesses, and service agencies. It combines:
- Real-time side-by-side invoice live preview
- Instant UPI payment QR code generated server-side using ZXing (hidden automatically if no UPI ID is provided)
- Smart GST tax distribution (Intra-State CGST+SGST vs. Inter-State IGST)
- 1-click invoice dispatch via WhatsApp (`wa.me` with automatic `+91` normalization)
- Permanent dark mode UI theme for comfortable, modern viewing (with light-mode invoices & PDFs for print fidelity)
- Interactive notification bell popover for real-time overdue invoice alerts and 1-click actions
- Visual analytics with Chart.js line charts, doughnut breakdowns, and sparklines
- Pixel-perfect PDF generation with Flying Saucer OpenPDF matching browser preview

### Highlights:
- **No Node.js or frontend build steps**: Pure Thymeleaf + Tailwind CSS CDN + Vanilla JavaScript (ES6+) + Chart.js CDN.
- **Zero-Config Development Database**: Runs out of the box using an in-memory H2 database pre-populated with Indian sample businesses, customers, products, and invoices.
- **Cloud & Production Ready**: Seamlessly runs in Docker containers and connects to MySQL in production.
- **Strict Monetary Precision**: Uses `java.math.BigDecimal` exclusively for all financial mathematics, preventing floating-point rounding errors.

---

## ✨ Key Features

1. **Vibrant Admin Dashboard**:
   - 4 two-colour gradient stat cards with real-time Chart.js sparklines and month-over-month % change indicators:
     - **Total Revenue** (Modern Sapphire Blue gradient)
     - **Paid Amount** (Clean Emerald Green gradient)
     - **Pending Amount** (Warm Amber gradient)
     - **Overdue Amount** (Rich Rose / Red gradient)
   - Large Revenue Trend Chart (Invoiced vs Paid) with **Month / Year** toggle and **CSV Export**.
   - Status Doughnut Chart and Recent Invoices table.
   - Bell icon with overdue invoice count badge and direct filter link.

2. **GST-Ready Invoicing Engine**:
   - Automatic detection of **Intra-State (CGST 50% + SGST 50%)** vs. **Inter-State (IGST 100%)** based on supplier state and client state.
   - Line-item HSN/SAC codes and standard GST rate slabs (0%, 5%, 12%, 18%, 28%).
   - Full tax summary table with taxable amounts, tax rates, and tax split.
   - 15-character GSTIN format validation (optional field for unregistered customers).

3. **Indian Currency & Legal Amount in Words**:
   - Pure `BigDecimal` Indian numbering system: e.g. `₹1,25,000.00` and `₹1,00,00,000.00` (lakh and crore grouping).
   - Algorithmic English Amount in Words translation: *"Rupees One Lakh Twenty-Five Thousand Only"* including fractional paise support (*"and Fifty Paise Only"*).
   - Embedded TrueType vector fonts ensure the Rupee glyph `₹` renders sharply in PDFs.

4. **Instant Dynamic UPI Payment QR Code**:
   - Generates standard UPI payment strings (`upi://pay?pa=<upi-id>&pn=<name>&am=<amount>&cu=INR&tn=<invoice-number>`).
   - Converted server-side into a Base64 PNG image using Google ZXing, displayed in preview and printed inside the PDF. Customers can scan and pay directly via GPay, PhonePe, Paytm, or BHIM.
   - Automatically hides the QR section if no UPI ID is saved.

5. **1-Click WhatsApp Invoice Dispatch**:
   - Opens `https://wa.me/<phone>?text=...` with a professional payment request message.
   - Auto-adds the `+91` country code if omitted, and provides a clear alert if the phone number is missing.

6. **Customer, Product & Reports Management**:
   - Customer directory with State and optional GSTIN.
   - Products catalog with default prices, HSN codes, and GST rates.
   - Reports page with CGST, SGST, IGST tax breakdown, sales by client, and CSV export.

---

## 🌐 English Language System & Central Dictionary

ClearBill Solutions features a **100% English-language user interface and PDF engine**, designed for clean usability, professional presentations, and broad accessibility:

| Area | Scope | Examples |
|---|---|---|
| **App Branding & Tagline** | Sidebar header, metadata, Swagger docs | **ClearBill**, *"Accurate Accounts, Honest Bills"* |
| **Navigation & Headings** | Sidebar items, greeting, page titles | Dashboard, Customers, Products, Invoices, Reports, Settings |
| **Action Buttons** | Primary action buttons and modal triggers | *“Create Invoice”*, *“Save Invoice”*, *“Edit”*, *“Delete”*, *“Download PDF”*, *“Send on WhatsApp”*, *“Add Customer”*, *“Export CSV”* |
| **Notifications & Dialogs** | Toast messages, empty states, confirmation alerts | *“Invoice created successfully!”*, *“Are you sure you want to delete?”*, *“No invoices found. Create your first invoice!”* |
| **Invoices & PDF Documents** | Tables, tax breakdowns, legal footer | Tax Invoice, HSN/SAC, CGST, SGST, IGST, **“Thank you, visit again”** |

All user-visible text is maintained in [`src/main/resources/static/js/messages.js`](src/main/resources/static/js/messages.js) in a clean dictionary, avoiding scattered text in HTML.

---

## 🎨 The 5 Handcrafted Invoice Templates

ClearBill Solutions includes 5 dedicated invoice templates, each featuring a unique color palette and distinct layout structure. All templates render the identical dataset and use HTML tables + floats so Flying Saucer OpenPDF matches browser preview pixel-for-pixel:

| # | Template | Dominant Colors | Layout Style | Key Visual Highlights |
|---|---|---|---|---|
| 1 | **Red** *(Default)* | Classic Crimson `#8B1E2D` & Cream | **Classic Layout** | Traditional Indian invoice style, centered banner, double borders, warm cream backdrop |
| 2 | **Blue** | Deep Corporate Blue `#2F3E8F` & White | **Corporate Layout** | Solid dark blue header ribbon, crisp corporate lines, modern shaded summary block |
| 3 | **Green** | Forest Green `#4F7942` & Sage Grey | **Sidebar Layout** | Left-hand tinted column with business & UPI details, wide right column for line items |
| 4 | **Orange** | Amber `#E8731A` & Saffron | **Modern Layout** | Top accent bar, rounded card styling, pill badges, vibrant total highlight |
| 5 | **Black** | Charcoal `#111827` & Monochrome | **Minimal Layout** | Editorial minimalist layout, clean hairline rules, generous whitespace |

> 🔄 **Automatic Fallback**: If an older invoice references an unknown template name, ClearBill Solutions safely defaults to `Red` without crashing.

---

## 🏛️ GST & Indian Currency System

### 1. Intra-State vs. Inter-State Tax Logic
- **Intra-State** (`Business State == Customer State`): Tax splits equally between **CGST** and **SGST**:
  $$\text{CGST} = \frac{\text{GST Rate}}{2}, \quad \text{SGST} = \frac{\text{GST Rate}}{2}$$
- **Inter-State** (`Business State != Customer State`): Tax is assigned entirely to **IGST**:
  $$\text{IGST} = \text{GST Rate}$$

### 2. Comma Formatting
Numbers follow the Indian numbering system (3 digits, then pairs of 2 digits):
- `1000` $\rightarrow$ `₹1,000.00`
- `100000` $\rightarrow$ `₹1,00,000.00` (1 Lakh)
- `10000000` $\rightarrow$ `₹1,00,00,000.00` (1 Crore)

---

## 🛠️ Tech Stack & Layered Architecture

- **Backend**: Java 21, Spring Boot 3.4.3
- **Persistence**: Spring Data JPA, Hibernate ORM, In-memory H2 (Dev), MySQL (Prod)
- **PDF Engine**: Flying Saucer (`flying-saucer-pdf-openpdf:9.1.22`), OpenPDF, Jsoup for XHTML normalization
- **QR Code**: ZXing (`com.google.zxing:javase:3.5.3`)
- **API Docs**: Springdoc OpenAPI 2.8.5 (Swagger UI)
- **Validation**: Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Min`, `@DecimalMin`)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Frontend**: Vanilla JavaScript (ES6+), HTML5, Tailwind CSS (CDN), Chart.js (CDN)

### Layered Architecture:
```
com.clearbillsolutions
├── ClearBillApplication.java        # Spring Boot application entry point
├── config                           # OpenAPI & Swagger configuration (OpenApiConfig.java)
├── controller                       # REST controllers (Invoice, Customer, Product, Dashboard, Settings)
├── service                          # Business logic, GST calculations, PDF & QR rendering
├── repository                       # Spring Data JPA repositories
├── entity                           # JPA database entities (Customer, Product, Invoice, Settings)
├── dto                              # Request & response data transfer objects
├── exception                        # Global exception handling & custom exceptions
└── util                             # IndianCurrencyUtil, GstUtil, QrCodeUtil
```

---

## 📑 Interactive API Docs (Swagger UI)

Explore and test all REST endpoints interactively via Swagger UI:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🚀 Getting Started (Run Locally)

### Prerequisites:
- Java 21 or higher (`java -version`)
- Maven 3.8+ (`mvn -version`)

### 1. Run with Maven (Development Profile - H2 In-Memory)
No MySQL installation required. Pre-loaded with sample Indian businesses, customers, products, and invoices:

```bash
# Clone the repository and enter the directory
cd clearbillsolutions

# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Alternatively, build and run the executable JAR:
```bash
mvn clean package -DskipTests
java -jar target/clearbillsolutions-1.0.0.jar --spring.profiles.active=dev
```

### 2. Access the Application:
- **Main Web Application**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Database Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:clearbilldb`
  - Username: `sa`
  - Password: *(leave blank)*

---

## 🧪 Running Automated Tests

ClearBill Solutions includes comprehensive JUnit 5 test suites covering monetary math, tax distribution, odd paise precision handling, large numbers (Crores), UPI QR codes, and PDF generation across all 5 templates:

```bash
mvn test
```

### Test Suites Included:
1. `TaxCalculationTest`: Intra-state CGST/SGST split, inter-state IGST, odd paise rounding rules (`HALF_UP`), mixed GST slabs (5%, 12%, 18%, 28%), multi-crore transactions, and UPI QR omission when blank.
2. `IndianCurrencyUtilTest`: Indian numbering system comma separation, zero, paise, lakhs, crores, negatives, and English words translation up to Crores.
3. `GstAndQrUtilTest`: 15-character GSTIN regex validation, state code detection, and UPI URL generation.
4. `PdfGenerationTest`: Verifies PDF creation for all 5 templates (**Red, Blue, Green, Orange, Black**) and fallback handling.

---

## 🐳 Docker Deployment

A production-ready multi-stage `Dockerfile` is included:

```bash
# 1. Build Docker image
docker build -t clearbillsolutions:1.0.0 .

# 2. Run with in-memory dev profile:
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev clearbillsolutions:1.0.0

# 3. Run with external MySQL:
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/clearbilldb?createDatabaseIfNotExist=true \
  -e DB_USER=root \
  -e DB_PASSWORD=secret \
  clearbillsolutions:1.0.0
```

---

## 📡 REST API Endpoints

### Invoices
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/invoices` | List all invoices (supports `?status=PAID`, etc.) |
| `GET` | `/api/invoices/{id}` | Get invoice details including tax split & UPI QR |
| `POST` | `/api/invoices` | Create new invoice with auto tax calculation |
| `PUT` | `/api/invoices/{id}` | Update existing invoice |
| `PATCH`| `/api/invoices/{id}/status?status=PAID` | Update invoice status |
| `DELETE`| `/api/invoices/{id}` | Delete invoice |
| `GET` | `/api/invoices/next-number` | Auto-generate next invoice number (`INV-2026-XXXX`) |
| `GET` | `/api/invoices/{id}/pdf?template=red` | Download invoice PDF in selected template |
| `GET` | `/api/invoices/{id}/preview?template=red` | View saved invoice HTML |
| `POST`| `/api/invoices/preview?template=red` | Live HTML preview for draft invoice |

### Customers & Catalog
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/customers` | List all customers (`?search=query`) |
| `POST` | `/api/customers` | Add customer (with state and GSTIN) |
| `GET` | `/api/products` | List all products/services |
| `POST` | `/api/products` | Add product (with HSN code and GST rate) |

### Dashboard & Settings
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard` | Metric counters, recent invoices, and sparklines |
| `GET` | `/api/dashboard/charts` | Monthly & annual invoiced vs paid trends for Chart.js |
| `GET` | `/api/settings` | Get business profile (GSTIN, State, UPI ID, Payee Name) |
| `PUT` | `/api/settings` | Update business profile |

---

## 📸 Walkthrough & UI Guide

1. **Dashboard & Analytics**:
   - 4 gradient metric cards with Chart.js sparklines and percentage trends.
   - Dual-line Monthly/Yearly Invoiced vs. Paid chart with CSV download.
   - Doughnut chart representing Paid vs. Pending vs. Overdue balances.
2. **Side-by-Side Invoice Editor**:
   - Real-time live HTML preview updating on every keystroke.
   - 5-template picker with color swatches (**Red, Blue, Green, Orange, Black**).
   - Dynamic product picker auto-populating HSN codes and GST rates.
3. **Multi-Channel Dispatch**:
   - **Download PDF**: Generates A4 invoice with crisp UPI QR and footer.
   - **WhatsApp**: 1-click `wa.me` message with pre-filled billing details and auto `+91`.
4. **Theme & Notifications**:
   - **Permanent Dark Mode**: Modern deep dark theme (`#0B0F19` backdrop, `#151D2F` card surfaces) throughout all application screens. Invoices and PDFs stay in crisp light mode for printing fidelity.
   - **Notification Bell Popover**: Top-bar bell displays overdue badge count and opens an interactive popover detailing overdue bills, days past due, quick "View Bill" and "WhatsApp Reminder" actions, and a "Dismiss badge" option.
   - **Search Palette (`Ctrl + /`)**: Rapid keyboard search to find invoices and customers instantly.

---

## 💡 Architecture & Design FAQ

### Q1: How does the application calculate GST intra-state vs. inter-state taxes?
> **Answer**: When an invoice is created, the system compares the business state from `BusinessSettings` with the client's state from `Customer`. If both states match, it treats the transaction as **intra-state** and splits the tax equally between **CGST** and **SGST**. If states differ, it assigns the full tax amount to **IGST**. All arithmetic uses `BigDecimal` with `RoundingMode.HALF_UP` to prevent penny rounding errors.

### Q2: How is the UPI payment QR code generated and embedded into PDFs?
> **Answer**: We use the Google **ZXing** library. The application formats a standard UPI deep-link URI (`upi://pay?pa=...&pn=...&am=...&cu=INR&tn=...`), encodes it into a 2D matrix, and converts it into a Base64 PNG data URL (`data:image/png;base64,...`). Flying Saucer embeds Base64 image strings directly into XHTML, rendering the QR code identically in both the browser preview and the final PDF.

### Q3: Why is Flying Saucer OpenPDF chosen over client-side libraries like jsPDF?
> **Answer**: Client-side libraries rasterize the webpage DOM into bitmap canvas screenshots, which often leads to blurry text on print, inconsistent fonts across operating systems, and page cutoff issues. Flying Saucer parses strict XML-compliant HTML + CSS on the server, embedding true vector fonts and adhering to CSS `@page` print standards.

### Q4: How is user-visible text structured across the application?
> **Answer**: All user-visible strings are centralized into `src/main/resources/static/js/messages.js` in clean, professional English, categorised into headings, navigation, buttons, toasts, confirmations, and empty states. This prevents string scattering across HTML, allows simple maintenance, and keeps the entire application uniform and accessible.

### Q5: Why does the application run in permanent dark mode while keeping invoices in light mode?
> **Answer**: The application workspace and dashboard run in permanent dark mode for visual ergonomics, high contrast, and a modern aesthetic. Meanwhile, all 5 invoice templates and generated PDFs are strictly kept in light mode (`#FFFFFF` / `#FFFDF9`) to guarantee exact paper fidelity, professional print clarity, and minimal ink consumption.

---

*ClearBill Solutions | Smart Invoice and Billing Manager — Accurate Accounts, Honest Bills*
