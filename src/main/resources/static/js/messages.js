/**
 * ClearBill Solutions Central Messages Dictionary
 * Clean, Professional English Language Dictionary
 * Tagline: Accurate Accounts, Honest Bills
 */
const MESSAGES = {
    appName: 'ClearBill',
    fullName: 'ClearBill Solutions',
    tagline: 'Accurate Accounts, Honest Bills',
    browserTitle: 'ClearBill Solutions | Smart Invoice and Billing Manager',
    greeting: 'Welcome!',
    welcomeSub: 'Comprehensive billing management and accounting overview',

    // Sidebar & Navigation Groups
    menuGroups: {
        main: 'Main Menu',
        analytics: 'Analytics',
        system: 'System'
    },
    nav: {
        dashboard: 'Dashboard',
        invoices: 'Invoices',
        customers: 'Customers',
        products: 'Products',
        reports: 'Reports',
        settings: 'Settings'
    },

    // Page Headings & Subtitles
    headings: {
        dashboard: 'Dashboard',
        dashboardSub: 'Real-time financial overview and business health',
        invoices: 'Invoice Management',
        invoicesSub: 'Manage, track, download, and send invoices to clients',
        newInvoice: 'Create New Invoice',
        editInvoice: 'Edit Invoice',
        customers: 'Customer Directory',
        customersSub: 'Customer profiles, addresses, states, and GSTIN records',
        products: 'Products & Services',
        productsSub: 'Catalog items, services, HSN/SAC codes, and GST rates',
        reports: 'Business Reports',
        reportsSub: 'Tax collection, intra vs inter-state sales, and customer volume',
        settings: 'Business Settings',
        settingsSub: 'Business profile, registered GSTIN, state code, and UPI payment setup'
    },

    // Action Buttons
    buttons: {
        newInvoice: 'Create Invoice',
        save: 'Save',
        edit: 'Edit',
        delete: 'Delete',
        cancel: 'Cancel',
        downloadPdf: 'Download PDF',
        sendWhatsApp: 'Send on WhatsApp',
        filter: 'Apply Filter',
        newCustomer: 'Add Customer',
        newProduct: 'Add Product',
        updateSettings: 'Save Settings',
        downloadCsv: 'Export CSV',
        uploadLogo: 'Upload Logo',
        searchPlaceholder: 'Search customers or invoice numbers... (Ctrl + /)',
        addItem: '+ Add Line Item',
        pickProduct: 'Select from Catalog:',
        viewAllInvoices: 'View All Invoices'
    },

    // Toast Notifications & Alerts
    toasts: {
        invoiceCreated: 'Invoice created successfully!',
        invoiceUpdated: 'Invoice updated successfully!',
        invoiceDeleted: 'Invoice deleted successfully!',
        customerSaved: 'Customer saved successfully!',
        customerDeleted: 'Customer deleted successfully!',
        productSaved: 'Product saved successfully!',
        productDeleted: 'Product deleted successfully!',
        settingsSaved: 'Settings updated successfully!',
        logoUploaded: 'Logo uploaded successfully!',
        error: 'Something went wrong. Please try again.',
        selectCustomer: 'Please select a customer.',
        itemRequired: 'At least one line item is required.',
        missingPhone: 'Customer phone number not found. Please check profile.',
        revenueCsvDownloaded: 'Revenue chart CSV exported successfully!',
        taxCsvDownloaded: 'Tax and sales CSV exported successfully!',
        copied: 'Copied to clipboard!'
    },

    // Modal Confirmation Dialogs
    confirmations: {
        deleteTitle: 'Are you sure you want to delete?',
        deleteSubtitle: 'This action cannot be undone. Do you wish to continue?',
        discardChanges: 'Are you sure you want to discard unsaved changes?'
    },

    // Empty States
    empty: {
        invoices: 'No invoices found. Create your first invoice!',
        recentInvoices: 'No recent invoices found.',
        customers: 'No customers found. Add your first customer!',
        products: 'No products or services found. Add your first product!',
        reports: 'No report data available yet.',
        noResults: 'No matching records found.'
    },

    // Helper Texts
    helpers: {
        overdueAlert: 'Invoice is overdue! Please follow up for payment settlement.',
        upiNote: 'Customers can scan this QR code using any UPI app to pay instantly.',
        gstIntraNote: 'Intra-state supply: CGST and SGST are divided equally.',
        gstInterNote: 'Inter-state supply: IGST applies to the full tax amount.'
    },

    // Table Headers
    tables: {
        invoiceNumber: 'Invoice #',
        customer: 'Customer',
        issueDate: 'Issue Date',
        dueDate: 'Due Date',
        status: 'Status',
        total: 'Total Amount',
        actions: 'Actions',
        hsn: 'HSN/SAC',
        unitPrice: 'Unit Price',
        qty: 'Quantity',
        gstRate: 'GST %',
        taxAmount: 'Tax Amount',
        lineTotal: 'Line Total',
        taxableValue: 'Taxable Value',
        cgst: 'CGST',
        sgst: 'SGST',
        igst: 'IGST',
        email: 'Email',
        phone: 'Phone',
        cityState: 'City, State',
        gstin: 'GSTIN'
    },

    // Invoice Status Labels
    status: {
        DRAFT: 'Draft',
        SENT: 'Sent',
        PAID: 'Paid',
        OVERDUE: 'Overdue'
    },

    // Template Display Names
    templates: {
        red: 'Red (Classic)',
        blue: 'Blue (Corporate)',
        green: 'Green (Sidebar)',
        orange: 'Orange (Modern)',
        black: 'Black (Minimal)'
    },

    // PDF Footer
    pdfFooter: 'Thank you for your business — ClearBill Solutions'
};

// Compatibility aliases
MESSAGES.english = MESSAGES;

if (typeof module !== 'undefined' && module.exports) {
    module.exports = MESSAGES;
}
