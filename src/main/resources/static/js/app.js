/**
 * ClearBill Solutions | Smart Invoice and Billing Manager
 * Accurate Accounts, Honest Bills
 * Pure Vanilla JavaScript Controller
 */

// Application State
const state = {
    currentView: 'dashboard',
    activeFilter: 'ALL',
    chartPeriod: 'month', // 'month' or 'year'
    isSidebarCollapsed: false,
    customers: [],
    products: [],
    invoices: [],
    allInvoices: [],
    dashboardStats: null,
    dashboardCharts: null,
    businessSettings: {
        companyName: 'ClearBill Solutions',
        currencySymbol: '₹',
        upiId: 'clearbill@upi'
    },
    editingInvoiceId: null,
    currentModalInvoiceId: null,
    currentModalInvoiceTemplate: 'red',
    selectedTemplate: 'red',
    previewTimeout: null,
    deleteTarget: { type: null, id: null },
    notificationsDismissed: false
};

// Chart.js Instances
let revenueChartInstance = null;
let statusDoughnutInstance = null;
let sparklineInstances = {};

// ==========================================
// Initialization
// ==========================================
document.addEventListener('DOMContentLoaded', async () => {
    // Permanently enforce dark mode
    document.documentElement.classList.add('dark');
    try { localStorage.setItem('clearbill_theme', 'dark'); } catch (e) {}

    setupKeyboardShortcuts();
    setupNotificationListeners();

    await loadSettings();
    await loadCustomers();
    await loadProducts();
    await loadInvoices();
    await loadDashboard();

    const hash = window.location.hash.replace('#', '');
    if (['dashboard', 'invoices', 'customers', 'products', 'reports', 'settings'].includes(hash)) {
        navigateTo(hash);
    } else {
        navigateTo('dashboard');
    }
});

// ==========================================
// Notifications Popover System
// ==========================================
function setupNotificationListeners() {
    document.addEventListener('click', (e) => {
        const dropdown = document.getElementById('notification-dropdown');
        const container = document.getElementById('notification-bell-container');
        if (dropdown && !dropdown.classList.contains('hidden')) {
            if (container && !container.contains(e.target)) {
                dropdown.classList.add('hidden');
            }
        }
    });
}

async function toggleNotificationDropdown(event) {
    if (event) event.stopPropagation();
    const dropdown = document.getElementById('notification-dropdown');
    if (!dropdown) return;
    const isOpening = dropdown.classList.contains('hidden');
    dropdown.classList.toggle('hidden');
    if (isOpening) {
        if (!state.allInvoices || state.allInvoices.length === 0) {
            try {
                const res = await fetch('/api/invoices');
                if (res.ok) state.allInvoices = await res.json();
            } catch (e) {
                console.error('Failed to pre-fetch invoices for notifications:', e);
            }
        }
        renderNotificationsDropdown();
    }
}

function closeNotificationDropdown() {
    const dropdown = document.getElementById('notification-dropdown');
    if (dropdown) dropdown.classList.add('hidden');
}

function dismissAllNotifications() {
    state.notificationsDismissed = true;
    const bellBadge = document.getElementById('bell-overdue-count');
    if (bellBadge) bellBadge.classList.add('hidden');
    const pill = document.getElementById('notif-badge-pill');
    if (pill) pill.classList.add('hidden');
    showToast('Notification badge dismissed', 'info');
}

function updateNotificationBadge() {
    const sourceList = (state.allInvoices && state.allInvoices.length > 0) ? state.allInvoices : state.invoices;
    const overdueCount = (state.dashboardStats && state.dashboardStats.overdueInvoicesCount != null)
        ? state.dashboardStats.overdueInvoicesCount
        : sourceList.filter(i => String(i.status).toUpperCase() === 'OVERDUE' || (i.dueDate && new Date(i.dueDate) < new Date() && String(i.status).toUpperCase() !== 'PAID')).length;

    const bellBadge = document.getElementById('bell-overdue-count');
    const pill = document.getElementById('notif-badge-pill');

    if (bellBadge) {
        if (overdueCount > 0 && !state.notificationsDismissed) {
            bellBadge.textContent = overdueCount;
            bellBadge.classList.remove('hidden');
        } else {
            bellBadge.classList.add('hidden');
        }
    }
    if (pill) {
        if (overdueCount > 0) {
            pill.textContent = `${overdueCount} Overdue`;
            pill.classList.remove('hidden');
        } else {
            pill.classList.add('hidden');
        }
    }
}

function renderNotificationsDropdown() {
    const container = document.getElementById('notification-items-list');
    if (!container) return;

    const sourceList = (state.allInvoices && state.allInvoices.length > 0) ? state.allInvoices : state.invoices;
    const overdueInvoices = sourceList.filter(i =>
        String(i.status).toUpperCase() === 'OVERDUE' ||
        (i.dueDate && new Date(i.dueDate) < new Date() && String(i.status).toUpperCase() !== 'PAID')
    );

    let html = '';

    if (overdueInvoices.length > 0) {
        overdueInvoices.forEach(inv => {
            html += `
                <div class="p-3.5 hover:bg-white/5 transition-colors flex items-start gap-3">
                    <div class="w-8 h-8 rounded-full bg-rose-500/20 text-rose-400 flex items-center justify-center shrink-0 mt-0.5">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
                    </div>
                    <div class="flex-1 min-w-0">
                        <div class="flex items-center justify-between gap-1">
                            <span class="font-bold text-xs text-[var(--text-main)] truncate">${escapeHtml(inv.invoiceNumber)} Overdue</span>
                            <span class="text-[10px] text-rose-400 font-semibold">${formatCurrency(inv.totalAmount)}</span>
                        </div>
                        <p class="text-[11px] text-[var(--text-muted)] mt-0.5 line-clamp-2">
                            ${escapeHtml(inv.customerName || 'Customer')} was due on ${formatDate(inv.dueDate)}. Please follow up for settlement.
                        </p>
                        <div class="flex items-center gap-2 mt-2">
                            <button class="btn-outline-theme !py-0.5 !px-2 text-[10px]" onclick="closeNotificationDropdown(); openPreviewModal(${inv.id}, '${inv.templateName}')">
                                View Bill
                            </button>
                            <button class="btn-outline-theme !py-0.5 !px-2 text-[10px] !text-[#25D366] !border-[#25D366]/40" onclick="sendInvoiceOnWhatsApp(${inv.id})">
                                WhatsApp Reminder
                            </button>
                        </div>
                    </div>
                </div>
            `;
        });
    } else {
        html += `
            <div class="p-6 text-center space-y-1">
                <div class="w-10 h-10 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center mx-auto mb-2">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>
                </div>
                <div class="text-xs font-bold text-[var(--text-main)]">All Invoices Settled</div>
                <p class="text-[11px] text-[var(--text-muted)]">No overdue bills or payment delays detected.</p>
            </div>
        `;
    }

    // System Status Notification
    html += `
        <div class="p-3 bg-[var(--table-header)]/60 flex items-center gap-3">
            <div class="w-8 h-8 rounded-full bg-blue-500/20 text-blue-400 flex items-center justify-center shrink-0">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
            </div>
            <div class="flex-1 min-w-0">
                <div class="text-xs font-bold text-[var(--text-main)]">Billing Engine Online</div>
                <div class="text-[10px] text-[var(--text-muted)]">ClearBill Solutions running in Permanent Dark Mode. All GST modules active.</div>
            </div>
        </div>
    `;

    container.innerHTML = html;
}

// ==========================================
// Sidebar Collapse & Mobile Drawer
// ==========================================
function toggleSidebarCollapse() {
    state.isSidebarCollapsed = !state.isSidebarCollapsed;
    const sidebar = document.getElementById('main-sidebar');
    const collapseIcon = document.getElementById('collapse-icon');
    if (!sidebar) return;

    if (state.isSidebarCollapsed) {
        sidebar.classList.remove('md:w-64');
        sidebar.classList.add('md:w-20');
        sidebar.querySelectorAll('.sidebar-label, .sidebar-group-title, .sidebar-text-container').forEach(el => el.classList.add('hidden'));
        if (collapseIcon) collapseIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" d="M13 5l7 7-7 7M5 5l7 7-7 7"/>';
    } else {
        sidebar.classList.remove('md:w-20');
        sidebar.classList.add('md:w-64');
        sidebar.querySelectorAll('.sidebar-label, .sidebar-group-title, .sidebar-text-container').forEach(el => el.classList.remove('hidden'));
        if (collapseIcon) collapseIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" d="M11 19l-7-7 7-7m8 14l-7-7 7-7"/>';
    }
}

function toggleMobileSidebar() {
    const sidebar = document.getElementById('main-sidebar');
    const overlay = document.getElementById('mobile-drawer-overlay');
    if (!sidebar || !overlay) return;

    const isClosed = sidebar.classList.contains('-translate-x-full');
    if (isClosed) {
        sidebar.classList.remove('-translate-x-full');
        overlay.classList.remove('hidden');
    } else {
        sidebar.classList.add('-translate-x-full');
        overlay.classList.add('hidden');
    }
}

// ==========================================
// Navigation & Breadcrumb
// ==========================================
function navigateTo(viewName) {
    state.currentView = viewName;
    window.location.hash = viewName;

    // Close mobile drawer if open
    const sidebar = document.getElementById('main-sidebar');
    const overlay = document.getElementById('mobile-drawer-overlay');
    if (sidebar && overlay && !sidebar.classList.contains('-translate-x-full') && window.innerWidth < 768) {
        sidebar.classList.add('-translate-x-full');
        overlay.classList.add('hidden');
    }

    document.querySelectorAll('main > section').forEach(sec => sec.classList.add('hidden'));

    const target = document.getElementById(`view-${viewName}`);
    if (target) {
        target.classList.remove('hidden');
    }

    document.querySelectorAll('#sidebar-nav .nav-item-btn').forEach(btn => {
        if (btn.getAttribute('data-view') === viewName) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });

    // Update Breadcrumb
    const bc = document.getElementById('breadcrumb-current');
    if (bc) {
        const titles = {
            dashboard: 'Dashboard',
            customers: 'Customers',
            products: 'Products',
            invoices: 'Invoices',
            'invoice-editor': 'Invoice Editor',
            reports: 'Reports',
            settings: 'Settings'
        };
        bc.textContent = titles[viewName] || viewName;
    }

    // Refresh view data
    if (viewName === 'dashboard') {
        loadDashboard();
    } else if (viewName === 'invoices') {
        loadInvoices(state.activeFilter);
    } else if (viewName === 'customers') {
        loadCustomers();
    } else if (viewName === 'products') {
        loadProducts();
    } else if (viewName === 'reports') {
        loadReports();
    } else if (viewName === 'settings') {
        loadSettings();
    }
}

// ==========================================
// Keyboard Shortcuts (Ctrl + /)
// ==========================================
function setupKeyboardShortcuts() {
    document.addEventListener('keydown', (e) => {
        if ((e.ctrlKey || e.metaKey) && e.key === '/') {
            e.preventDefault();
            openSearchModal();
        } else if (e.key === 'Escape') {
            closeSearchModal();
        }
    });
}

function openSearchModal() {
    const modal = document.getElementById('search-palette-modal');
    const input = document.getElementById('palette-search-input');
    if (modal) {
        modal.classList.remove('hidden');
        if (input) {
            input.value = '';
            input.focus();
            runGlobalPaletteSearch('');
        }
    }
}

function closeSearchModal() {
    const modal = document.getElementById('search-palette-modal');
    if (modal) modal.classList.add('hidden');
}

function closeSearchModalOnBackdrop(e) {
    if (e.target.id === 'search-palette-modal') {
        closeSearchModal();
    }
}

function runGlobalPaletteSearch(q) {
    const container = document.getElementById('palette-results');
    if (!container) return;
    const clean = (q || '').trim().toLowerCase();

    if (!clean) {
        container.innerHTML = `<div class="p-4 text-center text-[var(--text-muted)]">Type customer name, invoice #, or phone number to search...</div>`;
        return;
    }

    const matchedInvoices = state.invoices.filter(inv =>
        (inv.invoiceNumber && inv.invoiceNumber.toLowerCase().includes(clean)) ||
        (inv.customerName && inv.customerName.toLowerCase().includes(clean))
    );

    const matchedCustomers = state.customers.filter(c =>
        (c.name && c.name.toLowerCase().includes(clean)) ||
        (c.phone && c.phone.toLowerCase().includes(clean)) ||
        (c.city && c.city.toLowerCase().includes(clean))
    );

    let html = '';
    if (matchedInvoices.length > 0) {
        html += `<div class="px-3 py-1 font-bold text-[10px] text-[var(--text-muted)] uppercase tracking-wider bg-[var(--table-header)]">Invoices</div>`;
        matchedInvoices.forEach(inv => {
            html += `
                <div class="p-2.5 hover:bg-black/5 dark:hover:bg-white/5 cursor-pointer flex items-center justify-between rounded" onclick="closeSearchModal(); openPreviewModal(${inv.id}, '${inv.templateName}')">
                    <div>
                        <span class="font-bold text-[var(--text-main)]">${escapeHtml(inv.invoiceNumber)}</span>
                        <span class="text-[var(--text-muted)] ml-2">${escapeHtml(inv.customerName || 'N/A')}</span>
                    </div>
                    <span class="font-bold text-[var(--text-main)]">${formatCurrency(inv.totalAmount)}</span>
                </div>
            `;
        });
    }

    if (matchedCustomers.length > 0) {
        html += `<div class="px-3 py-1 font-bold text-[10px] text-[var(--text-muted)] uppercase tracking-wider bg-[var(--table-header)] mt-2">Customers</div>`;
        matchedCustomers.forEach(c => {
            html += `
                <div class="p-2.5 hover:bg-black/5 dark:hover:bg-white/5 cursor-pointer flex items-center justify-between rounded" onclick="closeSearchModal(); navigateTo('customers');">
                    <div>
                        <span class="font-bold text-[var(--text-main)]">${escapeHtml(c.name)}</span>
                        <span class="text-[var(--text-muted)] ml-2 text-[11px]">${escapeHtml(c.city || '')}, ${escapeHtml(c.state || '')}</span>
                    </div>
                    <span class="text-[var(--text-muted)] text-[11px]">${escapeHtml(c.phone || '')}</span>
                </div>
            `;
        });
    }

    if (!matchedInvoices.length && !matchedCustomers.length) {
        html = `<div class="p-4 text-center text-[var(--text-muted)]">${MESSAGES.empty.noResults}</div>`;
    }

    container.innerHTML = html;
}

// ==========================================
// Formatting Utilities
// ==========================================
function formatCurrency(amount) {
    const num = Number(amount) || 0;
    return `₹${num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function renderEmptyRow(colspan, message, actionText = null, actionFn = null) {
    return `<tr><td colspan="${colspan}" class="p-8">
        <div class="empty-state-box">
            <div class="empty-state-icon">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="1.75" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/></svg>
            </div>
            <p class="text-sm font-semibold text-[var(--text-main)] mb-1">${escapeHtml(message)}</p>
            <p class="text-xs text-[var(--text-muted)]">No records found. Get started by creating a new entry.</p>
            ${actionText && actionFn ? `<button class="btn-primary-gradient text-xs mt-3 !py-1 !px-3" onclick="${actionFn}">${escapeHtml(actionText)}</button>` : ''}
        </div>
    </td></tr>`;
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = `clearbill-toast toast-${type}`;

    let iconSvg = `<svg class="w-4 h-4 text-[#16A34A] shrink-0" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>`;
    if (type === 'error') {
        iconSvg = `<svg class="w-4 h-4 text-[#DC2626] shrink-0" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>`;
    } else if (type === 'info') {
        iconSvg = `<svg class="w-4 h-4 text-[#2563EB] shrink-0" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>`;
    }
    toast.innerHTML = `${iconSvg}<span>${escapeHtml(message)}</span>`;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.25s ease';
        setTimeout(() => toast.remove(), 250);
    }, 3500);
}

// ==========================================
// Dashboard View & Charts
// ==========================================
async function loadDashboard() {
    try {
        const [statsRes, chartsRes] = await Promise.all([
            fetch('/api/dashboard'),
            fetch('/api/dashboard/charts')
        ]);
        if (statsRes.ok) {
            state.dashboardStats = await statsRes.json();
            renderDashboardStats();
        }
        if (chartsRes.ok) {
            state.dashboardCharts = await chartsRes.json();
            renderRevenueChart();
            renderStatusDoughnut();
            renderSparklines();
        }
    } catch (e) {
        console.error('Failed to load dashboard:', e);
    }
}

function renderDashboardStats() {
    const s = state.dashboardStats;
    if (!s) return;

    // Stat cards values
    document.getElementById('stat-total-revenue').textContent = formatCurrency(s.totalRevenue);
    document.getElementById('stat-paid-amount').textContent = formatCurrency(s.paidAmount);
    document.getElementById('stat-pending-amount').textContent = formatCurrency(s.pendingAmount != null ? s.pendingAmount : s.unpaidAmount);
    document.getElementById('stat-overdue-amount').textContent = formatCurrency(s.overdueAmount != null ? s.overdueAmount : 0);

    // Sub-labels & % changes
    const revChange = s.revenueChangePercent || 0;
    const revEl = document.getElementById('stat-revenue-change');
    if (revEl) {
        revEl.innerHTML = revChange >= 0 ? `&uarr; +${revChange}%` : `&darr; ${revChange}%`;
        revEl.className = revChange >= 0 ? 'flex items-center gap-0.5 font-semibold text-emerald-200' : 'flex items-center gap-0.5 font-semibold text-rose-200';
    }

    const paidCountEl = document.getElementById('stat-paid-count');
    if (paidCountEl) paidCountEl.textContent = `${s.paidInvoicesCount} bills`;

    const pendingCountEl = document.getElementById('stat-pending-count');
    if (pendingCountEl) pendingCountEl.textContent = `${s.sentInvoicesCount + s.draftInvoicesCount} bills`;

    const overdueCountEl = document.getElementById('stat-overdue-count');
    if (overdueCountEl) overdueCountEl.textContent = `${s.overdueInvoicesCount} Overdue`;

    // Notification badge on top bar bell
    updateNotificationBadge();

    // Recent Invoices Table
    const tbody = document.getElementById('dashboard-recent-invoices-body');
    if (tbody) {
        if (!s.recentInvoices || s.recentInvoices.length === 0) {
            tbody.innerHTML = renderEmptyRow(6, MESSAGES.empty.recentInvoices, 'Create First Invoice', 'openInvoiceEditor()');
            return;
        }
        tbody.innerHTML = s.recentInvoices.map(inv => `
            <tr>
                <td class="font-bold text-[var(--text-main)]">${escapeHtml(inv.invoiceNumber)}</td>
                <td>${escapeHtml(inv.customerName || 'N/A')}</td>
                <td>${formatDate(inv.issueDate)}</td>
                <td class="font-bold">${formatCurrency(inv.totalAmount)}</td>
                <td>${renderStatusBadge(inv.status)}</td>
                <td class="text-right">
                    <button class="p-1 text-[var(--text-muted)] hover:text-blue-600" title="Preview" onclick="openPreviewModal(${inv.id}, '${inv.templateName}')">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                    </button>
                    <button class="p-1 text-[var(--text-muted)] hover:text-emerald-600" title="Download PDF" onclick="downloadPdf(${inv.id}, '${inv.templateName}')">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/></svg>
                    </button>
                    <button class="p-1 text-[#25D366] hover:opacity-80" title="WhatsApp" onclick="sendInvoiceOnWhatsApp(${inv.id})">
                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M12.031 6.172c-3.181 0-5.767 2.586-5.768 5.766-.001 1.298.38 2.27 1.019 3.287l-.711 2.598 2.668-.699c.969.587 1.771.884 2.792.884 3.179 0 5.767-2.587 5.768-5.766.001-3.187-2.575-5.77-5.768-5.77zm4.316 8.167c-.18.508-.901.938-1.246.999-.344.062-.77.106-2.434-.582-1.665-.688-2.73-2.39-2.812-2.5-.083-.11-.667-.887-.667-1.691 0-.804.421-1.2.571-1.363.15-.163.327-.204.436-.204.108 0 .217.001.312.006.1.005.234-.038.366.279.136.327.464 1.134.505 1.216.041.082.069.178.014.288-.055.109-.083.177-.164.273-.082.095-.172.213-.245.286-.082.082-.168.172-.072.336.095.163.424.701.91 1.134.626.557 1.154.729 1.318.811.163.082.259.068.355-.041.096-.11.409-.477.518-.641.109-.163.218-.136.367-.082.15.055.955.45 1.119.532.164.082.273.123.313.191.041.069.041.402-.139.91z"/></svg>
                    </button>
                </td>
            </tr>
        `).join('');
    }
}

function renderStatusBadge(status) {
    const s = String(status || '').toUpperCase();
    if (s === 'PAID') return `<span class="badge-paid px-2 py-0.5 rounded text-[11px] font-bold">Paid</span>`;
    if (s === 'SENT') return `<span class="badge-sent px-2 py-0.5 rounded text-[11px] font-bold">Sent</span>`;
    if (s === 'OVERDUE') return `<span class="badge-overdue px-2 py-0.5 rounded text-[11px] font-bold">Overdue</span>`;
    return `<span class="badge-draft px-2 py-0.5 rounded text-[11px] font-bold">Draft</span>`;
}

// 4 Stat Sparklines using Chart.js
function renderSparklines() {
    const s = state.dashboardStats;
    if (!s) return;

    createSparkline('sparkline-revenue', s.sparklineRevenue, '#FFFFFF');
    createSparkline('sparkline-paid', s.sparklinePaid, '#FFFFFF');
    createSparkline('sparkline-pending', s.sparklinePending, '#FFFFFF');
    createSparkline('sparkline-overdue', s.sparklineOverdue, '#FFFFFF');
}

function createSparkline(canvasId, dataPoints, strokeColor) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    if (sparklineInstances[canvasId]) {
        sparklineInstances[canvasId].destroy();
    }

    const data = (dataPoints && dataPoints.length) ? dataPoints.map(Number) : [0, 0, 0, 0, 0, 0];
    const labels = data.map((_, i) => `Point ${i + 1}`);

    sparklineInstances[canvasId] = new Chart(canvas, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                data,
                borderColor: strokeColor,
                borderWidth: 2,
                pointRadius: 0,
                fill: false,
                tension: 0.35
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false }, tooltip: { enabled: false } },
            scales: {
                x: { display: false },
                y: { display: false }
            }
        }
    });
}

// Month / Year toggle for revenue chart
function setChartPeriod(period) {
    state.chartPeriod = period;
    const btnMonth = document.getElementById('chart-toggle-month');
    const btnYear = document.getElementById('chart-toggle-year');
    if (period === 'month') {
        btnMonth.className = 'px-2.5 py-1 rounded-md font-semibold bg-[var(--card-bg)] text-blue-600 dark:text-blue-400 shadow-sm';
        btnYear.className = 'px-2.5 py-1 rounded-md font-semibold text-[var(--text-muted)] hover:text-[var(--text-main)]';
    } else {
        btnYear.className = 'px-2.5 py-1 rounded-md font-semibold bg-[var(--card-bg)] text-blue-600 dark:text-blue-400 shadow-sm';
        btnMonth.className = 'px-2.5 py-1 rounded-md font-semibold text-[var(--text-muted)] hover:text-[var(--text-main)]';
    }
    renderRevenueChart();
}

// Monthly / Yearly Chart (Invoiced vs Paid)
function renderRevenueChart() {
    const canvas = document.getElementById('monthly-revenue-chart');
    if (!canvas || !state.dashboardCharts) return;

    if (revenueChartInstance) {
        revenueChartInstance.destroy();
    }

    const isYear = state.chartPeriod === 'year';
    const c = state.dashboardCharts;
    const labels = isYear ? (c.yearlyLabels || ['2024', '2025', '2026']) : (c.monthlyLabels || []);
    const invoicedData = isYear ? (c.yearlyInvoiced || [0, 0, 0]) : (c.monthlyInvoiced || c.monthlyRevenue || []);
    const paidData = isYear ? (c.yearlyPaid || [0, 0, 0]) : (c.monthlyPaid || []);

    const gridColor = 'rgba(255, 255, 255, 0.08)';
    const textColor = '#94A3B8';

    const ctx = canvas.getContext('2d');
    const invoicedGradient = ctx.createLinearGradient(0, 0, 0, 300);
    invoicedGradient.addColorStop(0, 'rgba(37, 99, 235, 0.28)'); // Sapphire blue fill
    invoicedGradient.addColorStop(1, 'rgba(37, 99, 235, 0.01)');

    revenueChartInstance = new Chart(canvas, {
        type: 'line',
        data: {
            labels,
            datasets: [
                {
                    label: 'Invoiced (Billed)',
                    data: invoicedData.map(Number),
                    borderColor: '#2563EB',
                    backgroundColor: invoicedGradient,
                    borderWidth: 2.5,
                    fill: true,
                    tension: 0.35,
                    pointBackgroundColor: '#2563EB',
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Paid (Settled)',
                    data: paidData.map(Number),
                    borderColor: '#0D9488',
                    backgroundColor: 'transparent',
                    borderWidth: 2.5,
                    borderDash: [4, 4],
                    tension: 0.35,
                    pointBackgroundColor: '#0D9488',
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: { color: textColor, font: { family: 'Quicksand', size: 12 } }
                },
                tooltip: {
                    callbacks: {
                        label: (ctx) => ` ${ctx.dataset.label}: ₹${Number(ctx.raw).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`
                    }
                }
            },
            scales: {
                x: {
                    grid: { color: gridColor },
                    ticks: { color: textColor, font: { family: 'Quicksand' } }
                },
                y: {
                    grid: { color: gridColor },
                    ticks: {
                        color: textColor,
                        font: { family: 'Quicksand' },
                        callback: (v) => `₹${Number(v).toLocaleString('en-IN')}`
                    }
                }
            }
        }
    });
}

function downloadRevenueChartCsv() {
    if (!state.dashboardCharts) return;
    const isYear = state.chartPeriod === 'year';
    const c = state.dashboardCharts;
    const labels = isYear ? c.yearlyLabels : c.monthlyLabels;
    const invoiced = isYear ? c.yearlyInvoiced : (c.monthlyInvoiced || c.monthlyRevenue);
    const paid = isYear ? c.yearlyPaid : c.monthlyPaid;

    let csv = `Period,Invoiced (INR),Paid (INR)\n`;
    labels.forEach((lbl, idx) => {
        csv += `"${lbl}",${invoiced[idx] || 0},${paid[idx] || 0}\n`;
    });

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `clearbill_revenue_${state.chartPeriod}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    showToast(MESSAGES.toasts.revenueCsvDownloaded);
}

// Doughnut Chart (Paid vs Pending vs Overdue)
function renderStatusDoughnut() {
    const canvas = document.getElementById('status-doughnut-chart');
    if (!canvas || !state.dashboardCharts) return;

    if (statusDoughnutInstance) {
        statusDoughnutInstance.destroy();
    }

    const c = state.dashboardCharts;
    const amounts = (c.statusAmounts || [0, 0, 0]).map(Number);
    const labels = c.statusLabels || ['Paid', 'Pending', 'Overdue'];
    const colors = c.statusColors || ['#16A34A', '#D97706', '#DC2626'];

    // Update legend figures
    const paidEl = document.getElementById('doughnut-paid-val');
    const pendingEl = document.getElementById('doughnut-pending-val');
    const overdueEl = document.getElementById('doughnut-overdue-val');
    if (paidEl) paidEl.textContent = formatCurrency(amounts[0] || 0);
    if (pendingEl) pendingEl.textContent = formatCurrency(amounts[1] || 0);
    if (overdueEl) overdueEl.textContent = formatCurrency(amounts[2] || 0);

    const hasAny = amounts.some(a => a > 0);
    const dataVals = hasAny ? amounts : [1];
    const dataCols = hasAny ? colors : ['#CBD5E1'];

    statusDoughnutInstance = new Chart(canvas, {
        type: 'doughnut',
        data: {
            labels: hasAny ? labels : ['No Data'],
            datasets: [{
                data: dataVals,
                backgroundColor: dataCols,
                borderWidth: 2,
                borderColor: '#151D2F'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '72%',
            plugins: {
                legend: { display: false },
                tooltip: {
                    enabled: hasAny,
                    callbacks: {
                        label: (ctx) => ` ${ctx.label}: ₹${Number(ctx.raw).toLocaleString('en-IN')}`
                    }
                }
            }
        }
    });
}

// ==========================================
// Invoices List & Filters
// ==========================================
async function loadInvoices(filter = 'ALL') {
    state.activeFilter = filter;
    updateInvoiceFilterPills(filter);
    try {
        const url = filter && filter !== 'ALL' ? `/api/invoices?status=${filter}` : '/api/invoices';
        const res = await fetch(url);
        if (res.ok) {
            state.invoices = await res.json();
            if (filter === 'ALL') {
                state.allInvoices = state.invoices;
            }
            renderInvoicesTable(state.invoices);
            updateNotificationBadge();
        }
    } catch (e) {
        console.error('Failed to load invoices:', e);
    }
}

function updateInvoiceFilterPills(filter) {
    document.querySelectorAll('.invoice-filter-btn').forEach(btn => {
        if (btn.getAttribute('data-filter') === filter) {
            btn.className = 'invoice-filter-btn px-3 py-1 rounded font-semibold bg-[var(--card-bg)] text-blue-600 dark:text-blue-400 shadow-sm';
        } else {
            btn.className = 'invoice-filter-btn px-3 py-1 rounded font-semibold text-[var(--text-muted)]';
        }
    });
}

function filterInvoices(filter) {
    if (state.currentView !== 'invoices') {
        navigateTo('invoices');
    }
    loadInvoices(filter);
}

function handleInvoicesSearch(q) {
    const clean = (q || '').trim().toLowerCase();
    if (!clean) {
        renderInvoicesTable(state.invoices);
        return;
    }
    const filtered = state.invoices.filter(inv =>
        (inv.invoiceNumber && inv.invoiceNumber.toLowerCase().includes(clean)) ||
        (inv.customerName && inv.customerName.toLowerCase().includes(clean))
    );
    renderInvoicesTable(filtered);
}

function renderInvoicesTable(list) {
    const tbody = document.getElementById('invoices-table-body');
    if (!tbody) return;
    if (!list || list.length === 0) {
        tbody.innerHTML = renderEmptyRow(7, MESSAGES.empty.invoices, 'Create Invoice', 'openInvoiceEditor()');
        return;
    }

    tbody.innerHTML = list.map(inv => `
        <tr>
            <td class="font-bold text-[var(--text-main)]">${escapeHtml(inv.invoiceNumber)}</td>
            <td>
                <div class="font-semibold text-[var(--text-main)]">${escapeHtml(inv.customerName || 'N/A')}</div>
                <div class="text-[11px] text-[var(--text-muted)]">${escapeHtml(inv.customerState || '')}</div>
            </td>
            <td>${formatDate(inv.issueDate)}</td>
            <td>${formatDate(inv.dueDate)}</td>
            <td>${renderStatusBadge(inv.status)}</td>
            <td class="font-bold text-[var(--text-main)]">${formatCurrency(inv.totalAmount)}</td>
            <td class="text-right whitespace-nowrap">
                <button class="p-1.5 text-[var(--text-muted)] hover:text-blue-600" title="Preview" onclick="openPreviewModal(${inv.id}, '${inv.templateName}')">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                </button>
                <button class="p-1.5 text-[var(--text-muted)] hover:text-blue-600" title="Edit" onclick="openInvoiceEditor(${inv.id})">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                </button>
                <button class="p-1.5 text-[var(--text-muted)] hover:text-emerald-600" title="Download PDF" onclick="downloadPdf(${inv.id}, '${inv.templateName}')">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/></svg>
                </button>
                <button class="p-1.5 text-[#25D366] hover:opacity-80" title="WhatsApp" onclick="sendInvoiceOnWhatsApp(${inv.id})">
                    <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M12.031 6.172c-3.181 0-5.767 2.586-5.768 5.766-.001 1.298.38 2.27 1.019 3.287l-.711 2.598 2.668-.699c.969.587 1.771.884 2.792.884 3.179 0 5.767-2.587 5.768-5.766.001-3.187-2.575-5.77-5.768-5.77zm4.316 8.167c-.18.508-.901.938-1.246.999-.344.062-.77.106-2.434-.582-1.665-.688-2.73-2.39-2.812-2.5-.083-.11-.667-.887-.667-1.691 0-.804.421-1.2.571-1.363.15-.163.327-.204.436-.204.108 0 .217.001.312.006.1.005.234-.038.366.279.136.327.464 1.134.505 1.216.041.082.069.178.014.288-.055.109-.083.177-.164.273-.082.095-.172.213-.245.286-.082.082-.168.172-.072.336.095.163.424.701.91 1.134.626.557 1.154.729 1.318.811.163.082.259.068.355-.041.096-.11.409-.477.518-.641.109-.163.218-.136.367-.082.15.055.955.45 1.119.532.164.082.273.123.313.191.041.069.041.402-.139.91z"/></svg>
                </button>
                <button class="p-1.5 text-rose-600 hover:opacity-80" title="Delete" onclick="openDeleteModal('invoice', ${inv.id})">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                </button>
            </td>
        </tr>
    `).join('');
}

// ==========================================
// Invoice Editor & Live Preview (Step 7)
// ==========================================
async function autoGenerateInvoiceNumber() {
    try {
        const res = await fetch('/api/invoices/next-number');
        if (res.ok) {
            const data = await res.json();
            const nextNum = data.invoiceNumber || data.nextInvoiceNumber;
            if (nextNum) {
                document.getElementById('input-invoice-number').value = nextNum;
                triggerPreviewUpdate();
                return;
            }
        }
    } catch (e) {
        console.warn('Could not fetch auto invoice number:', e);
    }
    document.getElementById('input-invoice-number').value = `INV-${new Date().getFullYear()}-0001`;
    triggerPreviewUpdate();
}

async function openInvoiceEditor(invoiceId = null) {
    state.editingInvoiceId = invoiceId;
    navigateTo('invoice-editor');

    const titleEl = document.getElementById('invoice-editor-title');
    const container = document.getElementById('line-items-container');
    container.innerHTML = '';

    // Populate customer dropdown
    populateCustomerDropdown();
    populateQuickProductDropdown();

    if (invoiceId) {
        if (titleEl) titleEl.textContent = MESSAGES.headings.editInvoice;
        try {
            const res = await fetch(`/api/invoices/${invoiceId}`);
            if (res.ok) {
                const inv = await res.json();
                document.getElementById('input-invoice-number').value = inv.invoiceNumber || '';
                document.getElementById('input-issue-date').value = inv.issueDate || '';
                document.getElementById('input-due-date').value = inv.dueDate || '';
                document.getElementById('select-customer').value = inv.customerId || '';
                document.getElementById('select-status').value = inv.status || 'DRAFT';
                document.getElementById('input-discount-percent').value = inv.discountRate || 0;
                document.getElementById('input-invoice-notes').value = inv.notes || '';

                selectTemplate(inv.templateName || 'red');

                if (inv.items && inv.items.length) {
                    inv.items.forEach(it => addLineItemRow(it));
                } else {
                    addLineItemRow();
                }
            }
        } catch (e) {
            console.error('Failed to load invoice for editing:', e);
        }
    } else {
        if (titleEl) titleEl.textContent = MESSAGES.headings.newInvoice;
        await autoGenerateInvoiceNumber();

        const today = new Date().toISOString().split('T')[0];
        const due = new Date(Date.now() + 15 * 86400000).toISOString().split('T')[0];
        document.getElementById('input-issue-date').value = today;
        document.getElementById('input-due-date').value = due;
        document.getElementById('select-customer').value = '';
        document.getElementById('select-status').value = 'DRAFT';
        document.getElementById('input-discount-percent').value = '0';
        document.getElementById('input-invoice-notes').value = state.businessSettings.notesDefault || 'Thank you for your business!';

        selectTemplate('red');
        addLineItemRow();
    }

    triggerPreviewUpdate();
}

function selectTemplate(templateName) {
    const valid = ['red', 'blue', 'green', 'orange', 'black'];
    const chosen = valid.includes(templateName) ? templateName : 'red';
    state.selectedTemplate = chosen;

    document.querySelectorAll('#template-selector-container .template-pill-btn').forEach(btn => {
        if (btn.getAttribute('data-template') === chosen) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });

    triggerPreviewUpdate();
}

function addLineItemRow(item = null) {
    const container = document.getElementById('line-items-container');
    const rowId = `item-row-${Date.now()}-${Math.floor(Math.random() * 1000)}`;

    const desc = item ? item.description : '';
    const hsn = item ? (item.hsnCode || '') : '';
    const qty = item ? item.quantity : 1;
    const price = item ? item.unitPrice : 0;
    const gst = item ? (item.gstRate || item.taxRate || 18) : 18;

    const tr = document.createElement('tr');
    tr.id = rowId;
    tr.className = 'item-row';
    tr.innerHTML = `
        <td class="py-2 pr-2">
            <input type="text" class="form-input !py-1 text-xs item-desc" value="${escapeHtml(desc)}" placeholder="Item description" oninput="triggerPreviewUpdate()" required />
        </td>
        <td class="py-2 pr-2">
            <input type="text" class="form-input !py-1 text-xs item-hsn" value="${escapeHtml(hsn)}" placeholder="HSN/SAC" oninput="triggerPreviewUpdate()" />
        </td>
        <td class="py-2 pr-2">
            <input type="number" min="1" step="1" class="form-input !py-1 text-xs item-qty" value="${qty}" oninput="triggerPreviewUpdate()" />
        </td>
        <td class="py-2 pr-2">
            <input type="number" min="0" step="0.01" class="form-input !py-1 text-xs item-price" value="${price}" oninput="triggerPreviewUpdate()" />
        </td>
        <td class="py-2 pr-2">
            <select class="form-input !py-1 text-xs item-gst" onchange="triggerPreviewUpdate()">
                <option value="0" ${Number(gst) === 0 ? 'selected' : ''}>0%</option>
                <option value="5" ${Number(gst) === 5 ? 'selected' : ''}>5%</option>
                <option value="12" ${Number(gst) === 12 ? 'selected' : ''}>12%</option>
                <option value="18" ${Number(gst) === 18 ? 'selected' : ''}>18%</option>
                <option value="28" ${Number(gst) === 28 ? 'selected' : ''}>28%</option>
            </select>
        </td>
        <td class="py-2 pr-2 text-right font-bold item-line-total text-[var(--text-main)]">&#8377;0.00</td>
        <td class="py-2 text-right">
            <button type="button" class="text-[var(--text-muted)] hover:text-rose-600" onclick="removeLineItemRow('${rowId}')">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
            </button>
        </td>
    `;
    container.appendChild(tr);
    triggerPreviewUpdate();
}

function removeLineItemRow(rowId) {
    const row = document.getElementById(rowId);
    if (row) row.remove();
    const rows = document.querySelectorAll('#line-items-container .item-row');
    if (rows.length === 0) {
        addLineItemRow();
    }
    triggerPreviewUpdate();
}

function addPickedProduct() {
    const select = document.getElementById('quick-product-select');
    const val = select.value;
    if (!val) return;
    const p = state.products.find(item => String(item.id) === String(val));
    if (p) {
        addLineItemRow({
            description: p.name,
            hsnCode: p.hsnCode,
            quantity: 1,
            unitPrice: p.unitPrice,
            gstRate: p.gstRate || p.taxRate || 18
        });
        select.value = '';
    }
}

function onCustomerSelect(customerId) {
    triggerPreviewUpdate();
}

// Live HTML Preview Debounce
function triggerPreviewUpdate() {
    clearTimeout(state.previewTimeout);
    state.previewTimeout = setTimeout(fetchLivePreviewHtml, 200);
}

async function fetchLivePreviewHtml() {
    const frame = document.getElementById('live-preview-frame');
    if (!frame) return;

    const payload = buildInvoicePayload();
    if (!payload.customerId && state.customers.length > 0) {
        payload.customerId = state.customers[0].id; // Fallback so preview displays customer fields
    }

    try {
        const res = await fetch(`/api/invoices/preview?template=${state.selectedTemplate}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            const html = await res.text();
            frame.srcdoc = html;
        }
    } catch (e) {
        console.warn('Live preview fetch failed:', e);
    }
}

function buildInvoicePayload() {
    const items = [];
    document.querySelectorAll('#line-items-container .item-row').forEach(row => {
        const desc = row.querySelector('.item-desc').value.trim();
        const hsn = row.querySelector('.item-hsn').value.trim();
        const qty = parseFloat(row.querySelector('.item-qty').value) || 1;
        const price = parseFloat(row.querySelector('.item-price').value) || 0;
        const gst = parseFloat(row.querySelector('.item-gst').value) || 0;

        const total = qty * price;
        const totalSpan = row.querySelector('.item-line-total');
        if (totalSpan) totalSpan.textContent = formatCurrency(total);

        if (desc) {
            items.push({
                description: desc,
                hsnCode: hsn,
                quantity: qty,
                unitPrice: price,
                gstRate: gst,
                taxRate: gst
            });
        }
    });

    const rawNumber = (document.getElementById('input-invoice-number')?.value || '').trim();
    const invoiceNumber = (rawNumber && rawNumber.toLowerCase() !== 'undefined') ? rawNumber : null;

    return {
        invoiceNumber,
        customerId: document.getElementById('select-customer').value || null,
        issueDate: document.getElementById('input-issue-date').value || null,
        dueDate: document.getElementById('input-due-date').value || null,
        status: document.getElementById('select-status').value || 'DRAFT',
        template: state.selectedTemplate,
        templateName: state.selectedTemplate,
        discountPercent: parseFloat(document.getElementById('input-discount-percent').value) || 0,
        discountRate: parseFloat(document.getElementById('input-discount-percent').value) || 0,
        notes: document.getElementById('input-invoice-notes').value.trim(),
        items
    };
}

async function submitInvoiceForm() {
    const payload = buildInvoicePayload();
    if (!payload.customerId) {
        showToast(MESSAGES.toasts.selectCustomer, 'error');
        return;
    }
    if (!payload.items || payload.items.length === 0) {
        showToast(MESSAGES.toasts.itemRequired, 'error');
        return;
    }

    try {
        const isEdit = !!state.editingInvoiceId;
        const url = isEdit ? `/api/invoices/${state.editingInvoiceId}` : '/api/invoices';
        const method = isEdit ? 'PUT' : 'POST';

        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            showToast(isEdit ? MESSAGES.toasts.invoiceUpdated : MESSAGES.toasts.invoiceCreated);
            await loadInvoices();
            navigateTo('invoices');
        } else {
            const err = await res.json();
            showToast(err.message || MESSAGES.toasts.error, 'error');
        }
    } catch (e) {
        console.error('Invoice save failed:', e);
        showToast(MESSAGES.toasts.error, 'error');
    }
}

// ==========================================
// WhatsApp & PDF Actions (Step 2 & 7)
// ==========================================
async function openPreviewModal(invoiceId, templateName) {
    state.currentModalInvoiceId = invoiceId;
    state.currentModalInvoiceTemplate = templateName || 'red';

    const modal = document.getElementById('invoice-preview-modal');
    const frame = document.getElementById('modal-preview-frame');
    const titleEl = document.getElementById('modal-invoice-title');

    try {
        const res = await fetch(`/api/invoices/${invoiceId}`);
        if (res.ok) {
            const inv = await res.json();
            if (titleEl) titleEl.textContent = `Invoice ${inv.invoiceNumber} • ${inv.customerName || ''}`;
            frame.src = `/api/invoices/${invoiceId}/preview?template=${state.currentModalInvoiceTemplate}`;
            modal.classList.remove('hidden');
        }
    } catch (e) {
        console.error('Failed to open preview:', e);
    }
}

function closePreviewModal() {
    const modal = document.getElementById('invoice-preview-modal');
    if (modal) modal.classList.add('hidden');
}

function downloadCurrentModalPdf() {
    if (state.currentModalInvoiceId) {
        downloadPdf(state.currentModalInvoiceId, state.currentModalInvoiceTemplate);
    }
}

function downloadPdf(invoiceId, templateName) {
    const t = templateName || 'red';
    window.open(`/api/invoices/${invoiceId}/pdf?template=${t}`, '_blank');
}

function sendCurrentInvoiceOnWhatsApp() {
    if (state.currentModalInvoiceId) {
        sendInvoiceOnWhatsApp(state.currentModalInvoiceId);
    }
}

async function sendInvoiceOnWhatsApp(invoiceId) {
    try {
        const res = await fetch(`/api/invoices/${invoiceId}`);
        if (!res.ok) return;
        const inv = await res.json();

        let phone = (inv.customerPhone || '').replace(/\D/g, '');
        if (!phone) {
            showToast(MESSAGES.toasts.missingPhone, 'error');
            return;
        }

        // Auto-add +91 if Indian 10-digit number without country code
        if (phone.length === 10) {
            phone = '91' + phone;
        }

        const company = state.businessSettings.companyName || 'ClearBill';
        const msg = `Hello ${inv.customerName || 'Customer'},\n\nYour invoice *${inv.invoiceNumber}* for *${inv.formattedTotal || '₹' + inv.totalAmount}* has been generated.\nDue Date: *${formatDate(inv.dueDate)}*.\n\nPlease settle the payment before the due date.\nThank you,\n*${company}*`;

        const waUrl = `https://wa.me/${phone}?text=${encodeURIComponent(msg)}`;
        window.open(waUrl, '_blank');
    } catch (e) {
        console.error('WhatsApp error:', e);
        showToast(MESSAGES.toasts.error, 'error');
    }
}

// ==========================================
// Customer Management
// ==========================================
async function loadCustomers() {
    try {
        const res = await fetch('/api/customers');
        if (res.ok) {
            state.customers = await res.json();
            renderCustomersTable(state.customers);
            populateCustomerDropdown();
        }
    } catch (e) {
        console.error('Failed to load customers:', e);
    }
}

function populateCustomerDropdown() {
    const select = document.getElementById('select-customer');
    if (!select) return;
    select.innerHTML = '<option value="">-- Choose Customer --</option>' +
        state.customers.map(c => `<option value="${c.id}">${escapeHtml(c.name)} (${escapeHtml(c.state || 'India')})</option>`).join('');
}

function renderCustomersTable(list) {
    const tbody = document.getElementById('customers-table-body');
    if (!tbody) return;
    if (!list || list.length === 0) {
        tbody.innerHTML = renderEmptyRow(5, MESSAGES.empty.customers, 'Add Customer', 'openCustomerModal()');
        return;
    }
    tbody.innerHTML = list.map(c => `
        <tr>
            <td class="font-bold text-[var(--text-main)]">${escapeHtml(c.name)}</td>
            <td>
                <div>${escapeHtml(c.phone || 'N/A')}</div>
                <div class="text-[11px] text-[var(--text-muted)]">${escapeHtml(c.email || '')}</div>
            </td>
            <td>${escapeHtml(c.city || '')}, ${escapeHtml(c.state || '')}</td>
            <td><span class="font-mono text-xs">${escapeHtml(c.gstin || 'Unregistered')}</span></td>
            <td class="text-right whitespace-nowrap">
                <button class="p-1.5 text-[var(--text-muted)] hover:text-blue-600" title="Edit" onclick="openCustomerModal(${c.id})">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                </button>
                <button class="p-1.5 text-rose-600 hover:opacity-80" title="Delete" onclick="openDeleteModal('customer', ${c.id})">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                </button>
            </td>
        </tr>
    `).join('');
}

function handleCustomerSearch(q) {
    const clean = (q || '').trim().toLowerCase();
    if (!clean) {
        renderCustomersTable(state.customers);
        return;
    }
    const filtered = state.customers.filter(c =>
        (c.name && c.name.toLowerCase().includes(clean)) ||
        (c.phone && c.phone.toLowerCase().includes(clean)) ||
        (c.city && c.city.toLowerCase().includes(clean)) ||
        (c.state && c.state.toLowerCase().includes(clean))
    );
    renderCustomersTable(filtered);
}

function openCustomerModal(id = null) {
    const modal = document.getElementById('customer-modal');
    const form = document.getElementById('customer-form');
    form.reset();
    document.getElementById('customer-id').value = id || '';

    if (id) {
        const c = state.customers.find(item => item.id === id);
        if (c) {
            document.getElementById('customer-name').value = c.name || '';
            document.getElementById('customer-phone').value = c.phone || '';
            document.getElementById('customer-email').value = c.email || '';
            document.getElementById('customer-address').value = c.address || '';
            document.getElementById('customer-city').value = c.city || '';
            document.getElementById('customer-state').value = c.state || '';
            document.getElementById('customer-postal-code').value = c.postalCode || '';
            document.getElementById('customer-gstin').value = c.gstin || '';
        }
    }
    modal.classList.remove('hidden');
}

function closeCustomerModal() {
    const modal = document.getElementById('customer-modal');
    if (modal) modal.classList.add('hidden');
}

async function handleCustomerSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('customer-id').value;
    const payload = {
        name: document.getElementById('customer-name').value.trim(),
        phone: document.getElementById('customer-phone').value.trim(),
        email: document.getElementById('customer-email').value.trim(),
        address: document.getElementById('customer-address').value.trim(),
        city: document.getElementById('customer-city').value.trim(),
        state: document.getElementById('customer-state').value.trim(),
        postalCode: document.getElementById('customer-postal-code').value.trim(),
        gstin: document.getElementById('customer-gstin').value.trim().toUpperCase(),
        country: 'India'
    };

    try {
        const url = id ? `/api/customers/${id}` : '/api/customers';
        const method = id ? 'PUT' : 'POST';
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast(MESSAGES.toasts.customerSaved);
            closeCustomerModal();
            await loadCustomers();
        } else {
            const err = await res.json();
            showToast(err.message || MESSAGES.toasts.error, 'error');
        }
    } catch (e) {
        showToast(MESSAGES.toasts.error, 'error');
    }
}

// ==========================================
// Product Management
// ==========================================
async function loadProducts() {
    try {
        const res = await fetch('/api/products');
        if (res.ok) {
            state.products = await res.json();
            renderProductsTable(state.products);
            populateQuickProductDropdown();
        }
    } catch (e) {
        console.error('Failed to load products:', e);
    }
}

function populateQuickProductDropdown() {
    const select = document.getElementById('quick-product-select');
    if (!select) return;
    select.innerHTML = '<option value="">-- Select from Catalog --</option>' +
        state.products.map(p => `<option value="${p.id}">${escapeHtml(p.name)} (₹${p.unitPrice})</option>`).join('');
}

function renderProductsTable(list) {
    const tbody = document.getElementById('products-table-body');
    if (!tbody) return;
    if (!list || list.length === 0) {
        tbody.innerHTML = renderEmptyRow(5, MESSAGES.empty.products, 'Add Product', 'openProductModal()');
        return;
    }
    tbody.innerHTML = list.map(p => `
        <tr>
            <td>
                <div class="font-bold text-[var(--text-main)]">${escapeHtml(p.name)}</div>
                <div class="text-[11px] text-[var(--text-muted)]">${escapeHtml(p.description || '')}</div>
            </td>
            <td><span class="font-mono text-xs">${escapeHtml(p.hsnCode || '-')}</span></td>
            <td class="font-bold">${formatCurrency(p.unitPrice)}</td>
            <td><span class="px-2 py-0.5 rounded bg-[var(--table-header)] text-xs font-semibold">${p.gstRate || p.taxRate || 0}%</span></td>
            <td class="text-right whitespace-nowrap">
                <button class="p-1.5 text-[var(--text-muted)] hover:text-blue-600" title="Edit" onclick="openProductModal(${p.id})">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                </button>
                <button class="p-1.5 text-rose-600 hover:opacity-80" title="Delete" onclick="openDeleteModal('product', ${p.id})">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                </button>
            </td>
        </tr>
    `).join('');
}

function handleProductSearch(q) {
    const clean = (q || '').trim().toLowerCase();
    if (!clean) {
        renderProductsTable(state.products);
        return;
    }
    const filtered = state.products.filter(p =>
        (p.name && p.name.toLowerCase().includes(clean)) ||
        (p.hsnCode && p.hsnCode.toLowerCase().includes(clean))
    );
    renderProductsTable(filtered);
}

function openProductModal(id = null) {
    const modal = document.getElementById('product-modal');
    const form = document.getElementById('product-form');
    form.reset();
    document.getElementById('product-id').value = id || '';

    if (id) {
        const p = state.products.find(item => item.id === id);
        if (p) {
            document.getElementById('product-name').value = p.name || '';
            document.getElementById('product-description').value = p.description || '';
            document.getElementById('product-hsn').value = p.hsnCode || '';
            document.getElementById('product-price').value = p.unitPrice || 0;
            document.getElementById('product-tax-rate').value = String(p.gstRate || p.taxRate || 18);
        }
    }
    modal.classList.remove('hidden');
}

function closeProductModal() {
    const modal = document.getElementById('product-modal');
    if (modal) modal.classList.add('hidden');
}

async function handleProductSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('product-id').value;
    const rate = parseFloat(document.getElementById('product-tax-rate').value) || 0;
    const payload = {
        name: document.getElementById('product-name').value.trim(),
        description: document.getElementById('product-description').value.trim(),
        hsnCode: document.getElementById('product-hsn').value.trim(),
        unitPrice: parseFloat(document.getElementById('product-price').value) || 0,
        taxRate: rate,
        gstRate: rate
    };

    try {
        const url = id ? `/api/products/${id}` : '/api/products';
        const method = id ? 'PUT' : 'POST';
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast(MESSAGES.toasts.productSaved);
            closeProductModal();
            await loadProducts();
        } else {
            const err = await res.json();
            showToast(err.message || MESSAGES.toasts.error, 'error');
        }
    } catch (e) {
        showToast(MESSAGES.toasts.error, 'error');
    }
}

// ==========================================
// Reports & Tax View
// ==========================================
async function loadReports() {
    try {
        const res = await fetch('/api/invoices');
        if (!res.ok) return;
        const all = await res.json();

        let totalInvoiced = 0;
        let totalCgst = 0;
        let totalSgst = 0;
        let totalIgst = 0;

        const customerMap = {};

        all.forEach(inv => {
            const amt = Number(inv.totalAmount) || 0;
            totalInvoiced += amt;
            totalCgst += Number(inv.cgstAmount) || 0;
            totalSgst += Number(inv.sgstAmount) || 0;
            totalIgst += Number(inv.igstAmount) || 0;

            const cName = inv.customerName || 'Unknown Customer';
            if (!customerMap[cName]) {
                customerMap[cName] = {
                    name: cName,
                    state: inv.customerState || 'N/A',
                    gstin: inv.customerGstin || 'Unregistered',
                    count: 0,
                    total: 0,
                    paid: 0
                };
            }
            customerMap[cName].count += 1;
            customerMap[cName].total += amt;
            if (inv.status === 'PAID') {
                customerMap[cName].paid += amt;
            }
        });

        document.getElementById('report-total-invoiced').textContent = formatCurrency(totalInvoiced);
        document.getElementById('report-cgst').textContent = formatCurrency(totalCgst);
        document.getElementById('report-sgst').textContent = formatCurrency(totalSgst);
        document.getElementById('report-igst').textContent = formatCurrency(totalIgst);

        const tbody = document.getElementById('reports-customer-table-body');
        if (tbody) {
            const custs = Object.values(customerMap);
            if (!custs.length) {
                tbody.innerHTML = renderEmptyRow(6, MESSAGES.empty.reports);
                return;
            }
            tbody.innerHTML = custs.map(c => `
                <tr>
                    <td class="font-bold text-[var(--text-main)]">${escapeHtml(c.name)}</td>
                    <td>${escapeHtml(c.state)}</td>
                    <td><span class="font-mono text-xs">${escapeHtml(c.gstin)}</span></td>
                    <td>${c.count} bills</td>
                    <td class="font-bold">${formatCurrency(c.total)}</td>
                    <td class="text-right font-bold text-teal-600 dark:text-teal-400">${formatCurrency(c.paid)}</td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error('Reports load error:', e);
    }
}

function downloadFullReportCsv() {
    let csv = `Customer,State,GSTIN,Invoice Number,Date,Status,Taxable,CGST,SGST,IGST,Total\n`;
    state.invoices.forEach(inv => {
        csv += `"${inv.customerName || ''}","${inv.customerState || ''}","${inv.customerGstin || ''}","${inv.invoiceNumber}","${inv.issueDate}","${inv.status}",${inv.subtotal || 0},${inv.cgstAmount || 0},${inv.sgstAmount || 0},${inv.igstAmount || 0},${inv.totalAmount || 0}\n`;
    });

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `clearbill_tax_report_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    showToast(MESSAGES.toasts.taxCsvDownloaded);
}

// ==========================================
// Settings View
// ==========================================
async function loadSettings() {
    try {
        const res = await fetch('/api/settings');
        if (res.ok) {
            state.businessSettings = await res.json();
            const s = state.businessSettings;
            if (document.getElementById('setting-company-name')) {
                document.getElementById('setting-company-name').value = s.companyName || '';
                document.getElementById('setting-email').value = s.email || '';
                document.getElementById('setting-phone').value = s.phone || '';
                document.getElementById('setting-address').value = s.address || '';
                document.getElementById('setting-city').value = s.city || '';
                document.getElementById('setting-state').value = s.state || '';
                document.getElementById('setting-state-code').value = s.stateCode || '';
                document.getElementById('setting-postal-code').value = s.postalCode || '';
                document.getElementById('setting-gstin').value = s.gstin || '';
                document.getElementById('setting-upi-id').value = s.upiId || '';
                document.getElementById('setting-payee-name').value = s.payeeName || '';
                document.getElementById('setting-notes-default').value = s.notesDefault || '';
            }
        }
    } catch (e) {
        console.error('Failed to load settings:', e);
    }
}

async function submitSettingsForm() {
    const payload = {
        companyName: document.getElementById('setting-company-name').value.trim(),
        email: document.getElementById('setting-email').value.trim(),
        phone: document.getElementById('setting-phone').value.trim(),
        address: document.getElementById('setting-address').value.trim(),
        city: document.getElementById('setting-city').value.trim(),
        state: document.getElementById('setting-state').value.trim(),
        stateCode: document.getElementById('setting-state-code').value.trim(),
        postalCode: document.getElementById('setting-postal-code').value.trim(),
        gstin: document.getElementById('setting-gstin').value.trim().toUpperCase(),
        upiId: document.getElementById('setting-upi-id').value.trim(),
        payeeName: document.getElementById('setting-payee-name').value.trim(),
        notesDefault: document.getElementById('setting-notes-default').value.trim(),
        currencySymbol: '₹',
        country: 'India'
    };

    try {
        const res = await fetch('/api/settings', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            state.businessSettings = await res.json();
            showToast(MESSAGES.toasts.settingsSaved);
        } else {
            showToast(MESSAGES.toasts.error, 'error');
        }
    } catch (e) {
        showToast(MESSAGES.toasts.error, 'error');
    }
}

// ==========================================
// Delete Confirmation Modal
// ==========================================
function openDeleteModal(type, id) {
    state.deleteTarget = { type, id };
    const modal = document.getElementById('delete-modal');
    if (modal) modal.classList.remove('hidden');
}

function closeDeleteModal() {
    const modal = document.getElementById('delete-modal');
    if (modal) modal.classList.add('hidden');
    state.deleteTarget = { type: null, id: null };
}

async function executeDelete() {
    const { type, id } = state.deleteTarget;
    if (!type || !id) return;

    try {
        let endpoint = '';
        if (type === 'invoice') endpoint = `/api/invoices/${id}`;
        else if (type === 'customer') endpoint = `/api/customers/${id}`;
        else if (type === 'product') endpoint = `/api/products/${id}`;

        const res = await fetch(endpoint, { method: 'DELETE' });
        if (res.ok) {
            closeDeleteModal();
            if (type === 'invoice') {
                showToast(MESSAGES.toasts.invoiceDeleted);
                await loadInvoices();
                await loadDashboard();
            } else if (type === 'customer') {
                showToast(MESSAGES.toasts.customerDeleted);
                await loadCustomers();
            } else if (type === 'product') {
                showToast(MESSAGES.toasts.productDeleted);
                await loadProducts();
            }
        } else {
            showToast(MESSAGES.toasts.error, 'error');
        }
    } catch (e) {
        showToast(MESSAGES.toasts.error, 'error');
    }
}
