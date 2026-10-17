-- ClearBill Solutions | Smart Invoice and Billing Manager
-- Accurate Accounts, Honest Bills

-- 1. Business Profile Settings
INSERT INTO business_settings (id, company_name, email, phone, address, city, state, state_code, postal_code, country, tax_id, gstin, upi_id, payee_name, currency_symbol, logo_url, notes_default)
VALUES (1, 'ClearBill Solutions', 'billing@clearbillsolutions.com', '+91 98765 43210', 'Shop 14, Lotus Arcade, MG Road', 'Bengaluru', 'Karnataka', '29', '560001', 'India', 'TAX-IN-98234', '29AABCH1234F1Z8', 'clearbill@upi', 'ClearBill Solutions', '₹', '', 'Thank you for your business! Please settle the invoice via UPI or Net Banking before the due date.');

-- 2. Sample Customers (3 customers with GSTIN and state)
INSERT INTO customers (id, name, email, phone, address, city, state, postal_code, country, gstin)
VALUES (1, 'Ramesh Sharma', 'ramesh.sharma@example.in', '+91 98765 11111', '74 Lantern Lane, Indiranagar', 'Bengaluru', 'Karnataka', '560038', 'India', '29ABCDE1234F1Z5');

INSERT INTO customers (id, name, email, phone, address, city, state, postal_code, country, gstin)
VALUES (2, 'Priya Patel', 'priya.patel@example.com', '+91 98765 22222', '10 Moving Hearth Way, Bandra West', 'Mumbai', 'Maharashtra', '400050', 'India', '27AABCP5678Q1Z2');

INSERT INTO customers (id, name, email, phone, address, city, state, postal_code, country, gstin)
VALUES (3, 'Rajesh Rao', 'rajesh.rao@example.net', '+91 98765 33333', '12 Heritage Arcade, Vontikoppal', 'Mysuru', 'Karnataka', '570001', 'India', '29XYZAB9876C1Z1');

-- 3. Sample Products / Services (5 products with HSN and GST rate across slabs: 5, 12, 18, 28)
INSERT INTO products (id, name, description, hsn_code, unit_price, tax_rate)
VALUES (1, 'Cloud Accounting Software Setup', 'Initial deployment, user provisioning, chart of accounts setup and data migration', '998313', 4500.00, 18.00);

INSERT INTO products (id, name, description, hsn_code, unit_price, tax_rate)
VALUES (2, 'Organic Green Tea Bulk Pack (1kg)', 'Premium whole-leaf high-altitude orthodox green tea from Nilgiris', '090210', 850.00, 5.00);

INSERT INTO products (id, name, description, hsn_code, unit_price, tax_rate)
VALUES (3, 'Thermal Receipt Printer Hardware', 'High-speed 80mm wireless thermal POS billing receipt printer', '844332', 3200.00, 18.00);

INSERT INTO products (id, name, description, hsn_code, unit_price, tax_rate)
VALUES (4, 'Handcrafted Saffron Sweets Box (1kg)', 'Artisanal traditional saffron and pure ghee dry fruit sweets', '210690', 1200.00, 12.00);

INSERT INTO products (id, name, description, hsn_code, unit_price, tax_rate)
VALUES (5, 'Heavy Commercial Electronic Safe', 'Digital fire-resistant biometric security locker for retail store cash management', '830300', 14500.00, 28.00);

-- 4. Sample Invoices (2 invoices)
-- Invoice 1: Ramesh Sharma (PAID, Intra-State: Karnataka -> CGST 50% + SGST 50%)
-- Item 1: 2 * 850 = 1700.00, GST 5% -> Tax: 85.00 (CGST 42.50, SGST 42.50)
-- Item 2: 1 * 1200 = 1200.00, GST 12% -> Tax: 144.00 (CGST 72.00, SGST 72.00)
-- Subtotal: 2900.00, Discount 5%: 145.00, CGST: 114.50, SGST: 114.50, Total Tax: 229.00
-- Total: 2900 - 145 + 229 = 2984.00
INSERT INTO invoices (id, invoice_number, customer_id, issue_date, due_date, status, subtotal, tax_amount, cgst_amount, sgst_amount, igst_amount, is_inter_state, discount_rate, discount_amount, total_amount, notes, template_name)
VALUES (1, 'INV-2026-0001', 1, '2026-03-01', '2026-03-15', 'PAID', 2900.00, 229.00, 114.50, 114.50, 0.00, FALSE, 5.00, 145.00, 2984.00, 'Paid in full via UPI. Thank you for choosing ClearBill Solutions!', 'red');

INSERT INTO invoice_items (id, invoice_id, product_id, description, hsn_code, quantity, unit_price, tax_rate, tax_amount, cgst_amount, sgst_amount, igst_amount, line_total)
VALUES (1, 1, 2, 'Organic Green Tea Bulk Pack (1kg)', '090210', 2, 850.00, 5.00, 85.00, 42.50, 42.50, 0.00, 1700.00);

INSERT INTO invoice_items (id, invoice_id, product_id, description, hsn_code, quantity, unit_price, tax_rate, tax_amount, cgst_amount, sgst_amount, igst_amount, line_total)
VALUES (2, 1, 4, 'Handcrafted Saffron Sweets Box (1kg)', '210690', 1, 1200.00, 12.00, 144.00, 72.00, 72.00, 0.00, 1200.00);

-- Invoice 2: Priya Patel (SENT, Inter-State: Karnataka to Maharashtra -> IGST 100%)
-- Item 1: 1 * 4500 = 4500.00, GST 18% -> Tax: 810.00 (IGST 810.00)
-- Item 2: 1 * 3200 = 3200.00, GST 18% -> Tax: 576.00 (IGST 576.00)
-- Subtotal: 7700.00, Discount 0%: 0.00, IGST: 1386.00, CGST: 0.00, SGST: 0.00, Total Tax: 1386.00
-- Total: 7700 + 1386 = 9086.00
INSERT INTO invoices (id, invoice_number, customer_id, issue_date, due_date, status, subtotal, tax_amount, cgst_amount, sgst_amount, igst_amount, is_inter_state, discount_rate, discount_amount, total_amount, notes, template_name)
VALUES (2, 'INV-2026-0002', 2, '2026-03-10', '2026-03-25', 'SENT', 7700.00, 1386.00, 0.00, 0.00, 1386.00, TRUE, 0.00, 0.00, 9086.00, 'Inter-state supply delivered to Bandra. Kindly scan the UPI QR code to complete payment.', 'blue');

INSERT INTO invoice_items (id, invoice_id, product_id, description, hsn_code, quantity, unit_price, tax_rate, tax_amount, cgst_amount, sgst_amount, igst_amount, line_total)
VALUES (3, 2, 1, 'Cloud Accounting Software Setup', '998313', 1, 4500.00, 18.00, 810.00, 0.00, 0.00, 810.00, 4500.00);

INSERT INTO invoice_items (id, invoice_id, product_id, description, hsn_code, quantity, unit_price, tax_rate, tax_amount, cgst_amount, sgst_amount, igst_amount, line_total)
VALUES (4, 2, 3, 'Thermal Receipt Printer Hardware', '844332', 1, 3200.00, 18.00, 576.00, 0.00, 0.00, 576.00, 3200.00);
