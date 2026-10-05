// Vehicle Rental Management System - Role Isolated Logic
const API_BASE = '/api';

let currentUser = null;
let allVehicles = [];
let revenueChartInstance = null;

document.addEventListener('DOMContentLoaded', () => {
  // Start on login portal
  selectLoginRole('CUSTOMER');
  showPortalView();
});

// Select Role on Login Screen
function selectLoginRole(role) {
  document.getElementById('selectedRoleInput').value = role;

  const btnCustomer = document.getElementById('roleBtnCustomer');
  const btnManager = document.getElementById('roleBtnManager');
  const btnAdmin = document.getElementById('roleBtnAdmin');
  const submitBtn = document.getElementById('loginSubmitBtn');
  const credsText = document.getElementById('demoCredsText');
  const regToggleBox = document.getElementById('regToggleBox');

  btnCustomer.classList.remove('active');
  btnManager.classList.remove('active');
  btnAdmin.classList.remove('active');

  if (role === 'CUSTOMER') {
    btnCustomer.classList.add('active');
    submitBtn.innerHTML = `<i class="fa-solid fa-right-to-bracket"></i> Sign In to Customer Dashboard`;
    credsText.innerHTML = `Customer Username: <code>john_doe</code> | Password: <code>user123</code>`;
    if (regToggleBox) regToggleBox.style.display = 'block';
  } else if (role === 'RENTAL_MANAGER') {
    btnManager.classList.add('active');
    submitBtn.innerHTML = `<i class="fa-solid fa-clipboard-check"></i> Sign In to Manager Dashboard`;
    credsText.innerHTML = `Manager Username: <code>manager</code> | Password: <code>manager123</code>`;
    if (regToggleBox) regToggleBox.style.display = 'none';
  } else if (role === 'ADMIN') {
    btnAdmin.classList.add('active');
    submitBtn.innerHTML = `<i class="fa-solid fa-user-gear"></i> Sign In to Admin Control Center`;
    credsText.innerHTML = `Admin Username: <code>admin</code> | Password: <code>admin123</code>`;
    if (regToggleBox) regToggleBox.style.display = 'none';
  }

  toggleRegisterMode(false);
}

function toggleRegisterMode(isRegister) {
  const loginForm = document.getElementById('authLoginForm');
  const regForm = document.getElementById('authRegisterForm');
  if (isRegister) {
    loginForm.style.display = 'none';
    regForm.style.display = 'block';
  } else {
    loginForm.style.display = 'block';
    regForm.style.display = 'none';
  }
}

// Perform Role-Based Login with Password Authentication
async function submitRoleLogin() {
  const username = document.getElementById('loginUsername').value.trim();
  const password = document.getElementById('loginPassword').value.trim();
  const expectedRole = document.getElementById('selectedRoleInput').value;

  if (!username || !password) {
    alert('Please enter both username and password.');
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    const data = await res.json();

    if (data.success) {
      if (data.role !== expectedRole) {
        alert(`Access Denied: Account '${data.username}' has role '${data.role}', but you are attempting to login as '${expectedRole}'. Please select the correct role button.`);
        return;
      }

      currentUser = data;
      updateHeaderNav();
      renderRoleDashboard(data.role);
    } else {
      alert(data.message || 'Invalid username or password.');
    }
  } catch (err) {
    alert('Authentication error: ' + err.message);
  }
}

// Customer Registration
async function submitCustomerRegistration() {
  const req = {
    name: document.getElementById('regName').value.trim(),
    username: document.getElementById('regUsername').value.trim(),
    password: document.getElementById('regPassword').value.trim(),
    phone: document.getElementById('regPhone').value.trim(),
    address: document.getElementById('regAddress').value.trim(),
    drivingLicence: document.getElementById('regLicence').value.trim()
  };

  if (!req.username || !req.password || !req.name || !req.drivingLicence) {
    alert('Please fill out all required fields.');
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(req)
    });
    const data = await res.json();

    if (data.success) {
      alert('Registration successful! Logging in to Customer Dashboard...');
      currentUser = data;
      updateHeaderNav();
      renderRoleDashboard('CUSTOMER');
    } else {
      alert(data.message || 'Registration failed.');
    }
  } catch (err) {
    alert('Registration error: ' + err.message);
  }
}

// Render Only the Authorized Role Dashboard
function renderRoleDashboard(role) {
  document.getElementById('loginPortalView').style.display = 'none';

  // Hide all dashboard views first
  document.getElementById('customerDashboardView').style.display = 'none';
  document.getElementById('managerDashboardView').style.display = 'none';
  document.getElementById('adminDashboardView').style.display = 'none';

  if (role === 'CUSTOMER') {
    document.getElementById('customerDashboardView').style.display = 'block';
    loadAllVehicles();
    loadCustomerBookings();
  } else if (role === 'RENTAL_MANAGER') {
    document.getElementById('managerDashboardView').style.display = 'block';
    loadManagerData();
    loadExpiryAlerts();
  } else if (role === 'ADMIN') {
    document.getElementById('adminDashboardView').style.display = 'block';
    loadAdminData();
    loadAnalytics();
  }
}

function showPortalView() {
  currentUser = null;
  document.getElementById('userHeaderNav').style.display = 'none';
  document.getElementById('loginPortalView').style.display = 'block';

  document.getElementById('customerDashboardView').style.display = 'none';
  document.getElementById('managerDashboardView').style.display = 'none';
  document.getElementById('adminDashboardView').style.display = 'none';
}

function updateHeaderNav() {
  if (currentUser) {
    document.getElementById('userHeaderNav').style.display = 'flex';
    document.getElementById('navUserName').innerText = currentUser.name || currentUser.username;

    const roleEl = document.getElementById('navUserRole');
    roleEl.innerText = currentUser.role;
    roleEl.className = `role-pill role-${currentUser.role.toLowerCase().replace('_', '')}`;
  } else {
    document.getElementById('userHeaderNav').style.display = 'none';
  }
}

function logoutUser() {
  showPortalView();
  alert('Logged out successfully.');
}

function closeModal(id) {
  document.getElementById(id).classList.remove('active');
}

// ================= CUSTOMER DASHBOARD FUNCTIONS =================
async function loadAllVehicles() {
  try {
    const res = await fetch(`${API_BASE}/vehicles`);
    allVehicles = await res.json();
    renderVehiclesGrid(allVehicles);
  } catch (err) {
    console.error('Failed to load vehicles:', err);
  }
}

function filterVehicles() {
  const type = document.getElementById('searchType').value.toLowerCase();
  const keyword = document.getElementById('searchKeyword').value.toLowerCase();
  const maxRate = parseFloat(document.getElementById('searchMaxRate').value) || Infinity;
  const avail = document.getElementById('searchAvailability').value;

  const filtered = allVehicles.filter(v => {
    const matchType = !type || v.vehicleType.toLowerCase().includes(type);
    const matchKeyword = !keyword || v.brand.toLowerCase().includes(keyword) || v.model.toLowerCase().includes(keyword);
    const matchRate = v.dailyRate <= maxRate;
    const matchAvail = !avail || v.availability === avail;
    return matchType && matchKeyword && matchRate && matchAvail;
  });

  renderVehiclesGrid(filtered);
}

function resetVehicleSearch() {
  document.getElementById('searchType').value = '';
  document.getElementById('searchKeyword').value = '';
  document.getElementById('searchMaxRate').value = '';
  document.getElementById('searchAvailability').value = 'AVAILABLE';
  renderVehiclesGrid(allVehicles);
}

function renderVehiclesGrid(vehicles) {
  const container = document.getElementById('vehiclesGridContainer');
  if (!vehicles || vehicles.length === 0) {
    container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: var(--text-muted);"><i class="fa-solid fa-car-side" style="font-size: 3rem; margin-bottom: 1rem;"></i><br>No vehicles found matching your criteria.</div>';
    return;
  }

  container.innerHTML = vehicles.map(v => {
    const statusClass = `status-${v.availability.toLowerCase()}`;
    const defaultImg = 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600&auto=format&fit=crop';
    const isAvail = v.availability === 'AVAILABLE';

    return `
      <div class="vehicle-card">
        <div class="vehicle-img-wrapper">
          <img src="${v.imageUrl || defaultImg}" class="vehicle-img" alt="${v.brand} ${v.model}">
          <span class="vehicle-status-badge ${statusClass}">${v.availability}</span>
        </div>
        <div class="vehicle-body">
          <div class="vehicle-title">${v.brand} ${v.model}</div>
          <div class="vehicle-subtitle"><i class="fa-solid fa-tag"></i> Reg: ${v.registrationNo} | ${v.vehicleType}</div>
          <div class="vehicle-specs">
            <span><i class="fa-solid fa-gas-pump"></i> ${v.fuelType || 'Petrol'}</span>
            <span><i class="fa-solid fa-user"></i> ${v.seatingCapacity || 5} Seats</span>
          </div>
          <div class="vehicle-price-tag">$${v.dailyRate.toFixed(2)} <span>/ day</span></div>
          <div class="vehicle-footer">
            ${isAvail ? `
              <button class="btn btn-primary btn-sm" style="width: 100%;" onclick="openBookingModal(${v.vehicleId})">
                <i class="fa-solid fa-calendar-plus"></i> Reserve Now
              </button>
            ` : `
              <button class="btn btn-secondary btn-sm" style="width: 100%;" disabled>
                <i class="fa-solid fa-ban"></i> Currently ${v.availability}
              </button>
            `}
          </div>
        </div>
      </div>
    `;
  }).join('');
}

// Booking & Dynamic Price Calculator (FR4 & FR6)
function openBookingModal(vehicleId) {
  const vehicle = allVehicles.find(v => v.vehicleId === vehicleId);
  if (!vehicle) return;

  document.getElementById('bookingVehicleId').value = vehicle.vehicleId;
  document.getElementById('bookingVehicleSummary').innerHTML = `
    <strong>${vehicle.brand} ${vehicle.model}</strong> (${vehicle.vehicleType})
    <br><span style="color: var(--text-muted); font-size: 0.85rem;">Reg No: ${vehicle.registrationNo} | Base Rate: $${vehicle.dailyRate.toFixed(2)} / day</span>
  `;

  const today = new Date().toISOString().split('T')[0];
  const next3 = new Date(Date.now() + 3*86400000).toISOString().split('T')[0];

  document.getElementById('bookingStartDate').value = today;
  document.getElementById('bookingEndDate').value = next3;
  document.getElementById('bookingModal').classList.add('active');

  calculateDynamicPrice();
}

async function calculateDynamicPrice() {
  const vehicleId = document.getElementById('bookingVehicleId').value;
  const startDate = document.getElementById('bookingStartDate').value;
  const endDate = document.getElementById('bookingEndDate').value;
  const isWeekendOrFestival = document.getElementById('bookingWeekendFlag').checked;

  if (!vehicleId || !startDate || !endDate) return;

  try {
    const res = await fetch(`${API_BASE}/bookings/calculate-price`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ vehicleId, startDate, endDate, isWeekendOrFestival })
    });
    const data = await res.json();
    if (res.ok) {
      document.getElementById('calcDays').innerText = `${data.rentalDays} Days`;
      document.getElementById('calcRate').innerText = `$${data.dailyRate.toFixed(2)}`;
      document.getElementById('calcDynamic').innerText = `${data.dynamicMultiplier}x ($${data.dynamicPrice.toFixed(2)} / day)`;
      document.getElementById('calcTotal').innerText = `$${data.totalRentalCharge.toFixed(2)}`;
    }
  } catch (err) {
    console.error('Error calculating price:', err);
  }
}

async function submitBooking() {
  if (!currentUser || !currentUser.customerId) {
    alert('Please login as a Customer to make a reservation!');
    showPortalView();
    return;
  }

  const req = {
    customerId: currentUser.customerId,
    vehicleId: parseInt(document.getElementById('bookingVehicleId').value),
    rentalStartDate: document.getElementById('bookingStartDate').value,
    rentalEndDate: document.getElementById('bookingEndDate').value,
    isWeekendOrFestival: document.getElementById('bookingWeekendFlag').checked
  };

  try {
    const res = await fetch(`${API_BASE}/bookings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(req)
    });
    const data = await res.json();
    if (res.ok) {
      alert(`Booking #${data.bookingId} submitted successfully! Status: PENDING (Awaiting Rental Manager Approval).`);
      closeModal('bookingModal');
      loadAllVehicles();
      loadCustomerBookings();
    } else {
      alert(data.error || 'Booking failed');
    }
  } catch (err) {
    alert('Error submitting booking: ' + err.message);
  }
}

async function loadCustomerBookings() {
  if (!currentUser || !currentUser.customerId) return;

  try {
    const res = await fetch(`${API_BASE}/bookings/customer/${currentUser.customerId}`);
    const bookings = await res.json();
    const tbody = document.getElementById('customerBookingsTable');

    if (!bookings || bookings.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No bookings found in your account history.</td></tr>';
      return;
    }

    tbody.innerHTML = bookings.map(b => `
      <tr>
        <td>#${b.bookingId}</td>
        <td><strong>${b.vehicle.brand} ${b.vehicle.model}</strong><br><small style="color:var(--text-muted)">${b.vehicle.registrationNo}</small></td>
        <td>${b.rentalStartDate} to ${b.rentalEndDate}</td>
        <td>${b.rentalDays} Days</td>
        <td style="color: var(--accent-cyan); font-weight: 700;">$${b.totalCharge.toFixed(2)}</td>
        <td><span class="role-pill role-${getBookingStatusColor(b.bookingStatus)}">${b.bookingStatus}</span></td>
        <td>
          ${b.bookingStatus === 'APPROVED' ? `
            <button class="btn btn-success btn-sm" onclick="payNow(${b.bookingId}, ${b.totalCharge})"><i class="fa-solid fa-credit-card"></i> Pay Now</button>
          ` : ''}
          ${(b.bookingStatus === 'APPROVED' || b.bookingStatus === 'ACTIVE' || b.bookingStatus === 'EXTENDED') ? `
            <button class="btn btn-warning btn-sm" onclick="openExtendModal(${b.bookingId}, '${b.rentalEndDate}')"><i class="fa-solid fa-clock"></i> Extend</button>
          ` : ''}
          <button class="btn btn-secondary btn-sm" onclick="viewInvoice(${b.bookingId})"><i class="fa-solid fa-file-invoice"></i> Invoice</button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    console.error('Failed to load customer bookings:', err);
  }
}

function getBookingStatusColor(status) {
  switch (status) {
    case 'APPROVED': case 'ACTIVE': return 'customer';
    case 'PENDING': return 'manager';
    case 'COMPLETED': return 'customer';
    case 'REJECTED': case 'CANCELLED': return 'admin';
    default: return 'customer';
  }
}

async function payNow(bookingId, amount) {
  try {
    const res = await fetch(`${API_BASE}/payments/process`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookingId, paymentMethod: 'ONLINE_CARD', amount })
    });
    if (res.ok) {
      alert(`Payment of $${amount.toFixed(2)} completed successfully! Booking is now ACTIVE.`);
      loadCustomerBookings();
    }
  } catch (err) {
    alert('Payment processing failed: ' + err.message);
  }
}

function openExtendModal(bookingId, currentEndDate) {
  document.getElementById('extendBookingId').value = bookingId;
  document.getElementById('extendCurrentEndDate').value = currentEndDate;

  const dt = new Date(currentEndDate);
  dt.setDate(dt.getDate() + 2);
  document.getElementById('extendNewEndDate').value = dt.toISOString().split('T')[0];

  document.getElementById('extendModal').classList.add('active');
}

async function submitExtension() {
  const bookingId = document.getElementById('extendBookingId').value;
  const newEndDate = document.getElementById('extendNewEndDate').value;

  try {
    const res = await fetch(`${API_BASE}/bookings/${bookingId}/extend`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ newEndDate })
    });
    const data = await res.json();
    if (res.ok) {
      alert(`Rental duration extended to ${newEndDate}! Updated Total: $${data.totalCharge.toFixed(2)}`);
      closeModal('extendModal');
      loadCustomerBookings();
    } else {
      alert(data.error || 'Extension failed');
    }
  } catch (err) {
    alert('Error extending rental: ' + err.message);
  }
}

async function viewInvoice(bookingId) {
  try {
    const res = await fetch(`${API_BASE}/payments/invoice/${bookingId}`);
    const inv = await res.json();
    if (!res.ok) throw new Error(inv.error || 'Failed to fetch invoice');

    const box = document.getElementById('invoiceContent');
    box.innerHTML = `
      <div style="border-bottom: 2px solid #e2e8f0; padding-bottom: 1rem; margin-bottom: 1rem; display: flex; justify-content: space-between;">
        <div>
          <h2 style="color: #4f46e5; margin-bottom: 0.2rem;">DriveEase Rental Invoice</h2>
          <span style="color: #64748b; font-size: 0.85rem;">Invoice #: ${inv.invoiceNo} | Date: ${inv.invoiceDate.split('T')[0]}</span>
        </div>
        <div style="text-align: right;">
          <span style="background: #10b981; color: white; padding: 0.3rem 0.8rem; border-radius: 6px; font-weight: bold;">${inv.paymentStatus}</span>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem; font-size: 0.9rem;">
        <div>
          <strong style="color: #334155;">Customer Details:</strong><br>
          Name: ${inv.customerName}<br>
          Phone: ${inv.customerPhone}<br>
          Driving Licence: ${inv.customerLicence}
        </div>
        <div>
          <strong style="color: #334155;">Vehicle Rented:</strong><br>
          Vehicle: ${inv.vehicleDetails}<br>
          Category: ${inv.vehicleType}<br>
          Duration: ${inv.rentalStartDate} to ${inv.rentalEndDate} (${inv.rentalDays} Days)
        </div>
      </div>

      <table style="width: 100%; border-collapse: collapse; margin-bottom: 1.5rem; font-size: 0.9rem;">
        <thead>
          <tr style="background: #f1f5f9; text-align: left;">
            <th style="padding: 0.5rem;">Item Description</th>
            <th style="padding: 0.5rem; text-align: right;">Rate / Multiplier</th>
            <th style="padding: 0.5rem; text-align: right;">Amount</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style="padding: 0.5rem; border-bottom: 1px solid #e2e8f0;">Base Daily Rental Rate</td>
            <td style="padding: 0.5rem; text-align: right; border-bottom: 1px solid #e2e8f0;">$${inv.dailyRate.toFixed(2)} x ${inv.rentalDays} days</td>
            <td style="padding: 0.5rem; text-align: right; border-bottom: 1px solid #e2e8f0;">$${(inv.dailyRate * inv.rentalDays).toFixed(2)}</td>
          </tr>
          <tr>
            <td style="padding: 0.5rem; border-bottom: 1px solid #e2e8f0;">Dynamic Pricing Factor</td>
            <td style="padding: 0.5rem; text-align: right; border-bottom: 1px solid #e2e8f0;">${inv.dynamicPricingFactor}x multiplier</td>
            <td style="padding: 0.5rem; text-align: right; border-bottom: 1px solid #e2e8f0;">Included</td>
          </tr>
          <tr>
            <td style="padding: 0.5rem; border-bottom: 1px solid #e2e8f0;">Extra / Repair / Late Charges</td>
            <td style="padding: 0.5rem; text-align: right; border-bottom: 1px solid #e2e8f0;">-</td>
            <td style="padding: 0.5rem; text-align: right; border-bottom: 1px solid #e2e8f0;">$${inv.extraCharges.toFixed(2)}</td>
          </tr>
          <tr style="font-weight: bold; font-size: 1.05rem; background: #f8fafc;">
            <td style="padding: 0.75rem;">Total Amount Paid</td>
            <td style="padding: 0.75rem;"></td>
            <td style="padding: 0.75rem; text-align: right; color: #4f46e5;">$${inv.totalAmount.toFixed(2)}</td>
          </tr>
        </tbody>
      </table>

      <div style="font-size: 0.8rem; color: #64748b; text-align: center;">
        Payment Method: ${inv.paymentMethod} | Transaction Ref: ${inv.transactionId}<br>
        Thank you for choosing DriveEase Vehicle Rentals!
      </div>
    `;

    document.getElementById('invoiceModal').classList.add('active');
  } catch (err) {
    alert('Error generating invoice: ' + err.message);
  }
}

function printInvoice() {
  window.print();
}

// ================= RENTAL MANAGER DASHBOARD FUNCTIONS =================
async function loadManagerData() {
  try {
    const res = await fetch(`${API_BASE}/bookings`);
    const bookings = await res.json();

    const pending = bookings.filter(b => b.bookingStatus === 'PENDING');
    const active = bookings.filter(b => b.bookingStatus === 'APPROVED' || b.bookingStatus === 'ACTIVE' || b.bookingStatus === 'EXTENDED');

    document.getElementById('mgrPendingCount').innerText = pending.length;
    document.getElementById('mgrActiveRentalsCount').innerText = active.length;

    const pendingTbody = document.getElementById('managerPendingTable');
    if (pending.length === 0) {
      pendingTbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No pending booking approvals.</td></tr>';
    } else {
      pendingTbody.innerHTML = pending.map(b => `
        <tr>
          <td>#${b.bookingId}</td>
          <td><strong>${b.customer.name}</strong><br><small style="color:var(--text-muted)">Licence: ${b.customer.drivingLicence}</small></td>
          <td><strong>${b.vehicle.brand} ${b.vehicle.model}</strong> (${b.vehicle.registrationNo})</td>
          <td>${b.rentalStartDate} to ${b.rentalEndDate}</td>
          <td>$${b.dailyRate}/day &rarr; <strong>$${b.totalCharge.toFixed(2)}</strong></td>
          <td><span class="role-pill role-manager">${b.bookingStatus}</span></td>
          <td>
            <button class="btn btn-success btn-sm" onclick="approveBooking(${b.bookingId})"><i class="fa-solid fa-check"></i> Approve</button>
            <button class="btn btn-danger btn-sm" onclick="rejectBooking(${b.bookingId})"><i class="fa-solid fa-xmark"></i> Reject</button>
          </td>
        </tr>
      `).join('');
    }

    const activeTbody = document.getElementById('managerActiveTable');
    if (active.length === 0) {
      activeTbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No active vehicle rentals.</td></tr>';
    } else {
      activeTbody.innerHTML = active.map(b => `
        <tr>
          <td>#${b.bookingId}</td>
          <td><strong>${b.customer.name}</strong> (${b.customer.phone})</td>
          <td><strong>${b.vehicle.brand} ${b.vehicle.model}</strong> (${b.vehicle.registrationNo})</td>
          <td>${b.rentalStartDate} to ${b.rentalEndDate}</td>
          <td><span class="role-pill role-customer">${b.bookingStatus}</span></td>
          <td>
            <button class="btn btn-danger btn-sm" onclick="openReturnModal(${b.bookingId}, '${b.rentalEndDate}')"><i class="fa-solid fa-arrow-right-to-bracket"></i> Inspect & Process Return</button>
          </td>
        </tr>
      `).join('');
    }

    loadMaintenanceList();
  } catch (err) {
    console.error('Failed to load manager data:', err);
  }
}

async function approveBooking(id) {
  try {
    const res = await fetch(`${API_BASE}/bookings/${id}/approve`, { method: 'POST' });
    if (res.ok) {
      alert(`Booking #${id} approved! Vehicle status set to BOOKED.`);
      loadManagerData();
    }
  } catch (err) {
    alert('Error approving booking: ' + err.message);
  }
}

async function rejectBooking(id) {
  const reason = prompt('Enter rejection reason:');
  try {
    const res = await fetch(`${API_BASE}/bookings/${id}/reject`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reason })
    });
    if (res.ok) {
      alert(`Booking #${id} rejected.`);
      loadManagerData();
    }
  } catch (err) {
    alert('Error rejecting booking: ' + err.message);
  }
}

function openReturnModal(bookingId, endDate) {
  document.getElementById('returnBookingId').value = bookingId;
  document.getElementById('returnDate').value = new Date().toISOString().split('T')[0];
  document.getElementById('returnModal').classList.add('active');
}

async function submitReturnInspection() {
  const req = {
    bookingId: parseInt(document.getElementById('returnBookingId').value),
    returnDate: document.getElementById('returnDate').value,
    fuelLevel: document.getElementById('returnFuel').value,
    odometerReading: parseInt(document.getElementById('returnOdometer').value) || 0,
    inspectorName: document.getElementById('returnInspector').value,
    damageDescription: document.getElementById('returnDamageDesc').value,
    repairCost: parseFloat(document.getElementById('returnRepairCost').value) || 0
  };

  try {
    const res = await fetch(`${API_BASE}/damage/return`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(req)
    });
    const data = await res.json();
    if (res.ok) {
      alert(`Vehicle Return Completed!\nFinal Bill: $${data.finalTotalBill.toFixed(2)}\nLate Fee: $${data.lateFee}\nRepair Cost: $${data.repairCost}\nVehicle Availability: ${data.vehicleAvailability}`);
      closeModal('returnModal');
      loadManagerData();
    } else {
      alert(data.error || 'Return processing failed');
    }
  } catch (err) {
    alert('Error processing return: ' + err.message);
  }
}

async function loadMaintenanceList() {
  try {
    const res = await fetch(`${API_BASE}/maintenance`);
    const list = await res.json();

    const inMaint = list.filter(m => m.maintenanceStatus !== 'COMPLETED').length;
    document.getElementById('mgrMaintCount').innerText = inMaint;

    const tbody = document.getElementById('managerMaintenanceTable');
    if (!list || list.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No maintenance records logged.</td></tr>';
      return;
    }

    tbody.innerHTML = list.map(m => `
      <tr>
        <td>#${m.maintenanceId}</td>
        <td><strong>${m.vehicle.brand} ${m.vehicle.model}</strong> (${m.vehicle.registrationNo})</td>
        <td>${m.lastServiceDate || 'N/A'}</td>
        <td>${m.nextServiceDate}</td>
        <td><span class="role-pill role-${m.maintenanceStatus === 'COMPLETED' ? 'customer' : 'admin'}">${m.maintenanceStatus}</span></td>
        <td>${m.serviceNotes || '-'}</td>
        <td>
          ${m.maintenanceStatus !== 'COMPLETED' ? `
            <button class="btn btn-success btn-sm" onclick="completeMaintenance(${m.maintenanceId})"><i class="fa-solid fa-check-double"></i> Mark Completed</button>
          ` : '<span style="color:var(--text-muted);">Completed</span>'}
        </td>
      </tr>
    `).join('');
  } catch (err) {
    console.error('Failed to load maintenance:', err);
  }
}

async function openMaintenanceModal() {
  const res = await fetch(`${API_BASE}/vehicles`);
  const vehicles = await res.json();

  const select = document.getElementById('maintVehicleSelect');
  select.innerHTML = vehicles.map(v => `<option value="${v.vehicleId}">${v.brand} ${v.model} (${v.registrationNo})</option>`).join('');

  const next2w = new Date(Date.now() + 14*86400000).toISOString().split('T')[0];
  document.getElementById('maintNextDate').value = next2w;
  document.getElementById('maintenanceModal').classList.add('active');
}

async function submitMaintenance() {
  const req = {
    vehicleId: parseInt(document.getElementById('maintVehicleSelect').value),
    nextServiceDate: document.getElementById('maintNextDate').value,
    maintenanceStatus: document.getElementById('maintStatus').value,
    serviceNotes: document.getElementById('maintNotes').value
  };

  try {
    const res = await fetch(`${API_BASE}/maintenance/schedule`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(req)
    });
    if (res.ok) {
      alert('Maintenance scheduled successfully! Vehicle status set to MAINTENANCE.');
      closeModal('maintenanceModal');
      loadManagerData();
    }
  } catch (err) {
    alert('Error scheduling maintenance: ' + err.message);
  }
}

async function completeMaintenance(id) {
  try {
    const res = await fetch(`${API_BASE}/maintenance/${id}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: 'COMPLETED' })
    });
    if (res.ok) {
      alert('Maintenance marked as COMPLETED. Vehicle is now AVAILABLE!');
      loadManagerData();
    }
  } catch (err) {
    alert('Error updating status: ' + err.message);
  }
}

async function loadExpiryAlerts() {
  try {
    const res = await fetch(`${API_BASE}/maintenance/expiry-alerts`);
    const alerts = await res.json();
    const container = document.getElementById('expiryAlertContainer');

    if (!alerts || alerts.length === 0) {
      container.innerHTML = '';
      return;
    }

    container.innerHTML = alerts.map(a => `
      <div class="alert-banner ${a.isExpired ? 'alert-danger' : 'alert-warning'}">
        <i class="fa-solid fa-triangle-exclamation" style="font-size: 1.2rem;"></i>
        <div>
          <strong>${a.type} Warning:</strong> ${a.vehicleDetails} ${a.isExpired ? 'EXPIRED' : 'expires soon'} on <strong>${a.expiryDate}</strong>.
        </div>
      </div>
    `).join('');
  } catch (err) {
    console.error('Failed to load expiry alerts:', err);
  }
}

// ================= ADMIN DASHBOARD FUNCTIONS =================
async function loadAdminData() {
  try {
    const resV = await fetch(`${API_BASE}/vehicles`);
    const vehicles = await resV.json();
    const tbodyV = document.getElementById('adminVehiclesTable');

    tbodyV.innerHTML = vehicles.map(v => `
      <tr>
        <td>#${v.vehicleId}</td>
        <td>${v.vehicleType}</td>
        <td><strong>${v.brand} ${v.model}</strong></td>
        <td><code>${v.registrationNo}</code></td>
        <td>$${v.dailyRate.toFixed(2)}</td>
        <td><span class="vehicle-status-badge status-${v.availability.toLowerCase()}" style="position:static;">${v.availability}</span></td>
        <td><small style="color:var(--text-muted)">Ins: ${v.insuranceExpiry || 'N/A'}<br>Pol: ${v.pollutionExpiry || 'N/A'}</small></td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="editVehicle(${v.vehicleId})"><i class="fa-solid fa-pen"></i> Edit</button>
          <button class="btn btn-danger btn-sm" onclick="deleteVehicle(${v.vehicleId})"><i class="fa-solid fa-trash"></i></button>
        </td>
      </tr>
    `).join('');

    const resC = await fetch(`${API_BASE}/customers`);
    const customers = await resC.json();
    const tbodyC = document.getElementById('adminCustomersTable');

    tbodyC.innerHTML = customers.map(c => `
      <tr>
        <td>#${c.customerId}</td>
        <td><strong>${c.name}</strong></td>
        <td>${c.phone}</td>
        <td>${c.address}</td>
        <td><code style="color:var(--accent-cyan)">${c.drivingLicence}</code></td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="validateLicence('${c.drivingLicence}')"><i class="fa-solid fa-shield-check"></i> Verify Licence</button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    console.error('Failed to load admin data:', err);
  }
}

function openAddVehicleModal() {
  document.getElementById('vehicleFormId').value = '';
  document.getElementById('vBrand').value = '';
  document.getElementById('vModel').value = '';
  document.getElementById('vRegNo').value = '';
  document.getElementById('vDailyRate').value = '50.0';
  document.getElementById('vAvailability').value = 'AVAILABLE';
  document.getElementById('vImageUrl').value = '';
  document.getElementById('vehicleFormModal').classList.add('active');
}

async function editVehicle(id) {
  const res = await fetch(`${API_BASE}/vehicles/${id}`);
  const v = await res.json();
  if (!v) return;

  document.getElementById('vehicleFormId').value = v.vehicleId;
  document.getElementById('vType').value = v.vehicleType;
  document.getElementById('vBrand').value = v.brand;
  document.getElementById('vModel').value = v.model;
  document.getElementById('vRegNo').value = v.registrationNo;
  document.getElementById('vDailyRate').value = v.dailyRate;
  document.getElementById('vAvailability').value = v.availability;
  document.getElementById('vInsuranceExpiry').value = v.insuranceExpiry || '';
  document.getElementById('vPollutionExpiry').value = v.pollutionExpiry || '';
  document.getElementById('vImageUrl').value = v.imageUrl || '';

  document.getElementById('vehicleFormModal').classList.add('active');
}

async function saveVehicle() {
  const id = document.getElementById('vehicleFormId').value;
  const vehicle = {
    vehicleType: document.getElementById('vType').value,
    brand: document.getElementById('vBrand').value,
    model: document.getElementById('vModel').value,
    registrationNo: document.getElementById('vRegNo').value,
    dailyRate: parseFloat(document.getElementById('vDailyRate').value),
    availability: document.getElementById('vAvailability').value,
    insuranceExpiry: document.getElementById('vInsuranceExpiry').value || null,
    pollutionExpiry: document.getElementById('vPollutionExpiry').value || null,
    imageUrl: document.getElementById('vImageUrl').value || null
  };

  const url = id ? `${API_BASE}/vehicles/${id}` : `${API_BASE}/vehicles`;
  const method = id ? 'PUT' : 'POST';

  try {
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(vehicle)
    });
    if (res.ok) {
      alert('Vehicle saved successfully!');
      closeModal('vehicleFormModal');
      loadAdminData();
    } else {
      const data = await res.json();
      alert(data.error || 'Failed to save vehicle');
    }
  } catch (err) {
    alert('Error saving vehicle: ' + err.message);
  }
}

async function deleteVehicle(id) {
  if (!confirm('Are you sure you want to delete this vehicle?')) return;
  try {
    const res = await fetch(`${API_BASE}/vehicles/${id}`, { method: 'DELETE' });
    if (res.ok) {
      alert('Vehicle deleted successfully');
      loadAdminData();
    }
  } catch (err) {
    alert('Error deleting vehicle: ' + err.message);
  }
}

async function validateLicence(licence) {
  try {
    const res = await fetch(`${API_BASE}/customers/validate-licence`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ drivingLicence: licence })
    });
    const data = await res.json();
    alert(`Driving Licence Validation (${licence}): ${data.isValid ? 'VALID FORMAT' : 'INVALID'}`);
  } catch (err) {
    alert('Validation error: ' + err.message);
  }
}

// Analytics Summary & Chart Rendering (FR13 & Feature 5)
async function loadAnalytics() {
  try {
    const res = await fetch(`${API_BASE}/analytics/summary`);
    const data = await res.json();

    document.getElementById('adminTotalRev').innerText = `$${data.totalRevenue.toFixed(2)}`;
    document.getElementById('adminTotalVehicles').innerText = data.totalVehicles;
    document.getElementById('adminUtilRate').innerText = `${data.utilizationRate}%`;
    document.getElementById('adminCustomerCount').innerText = data.totalCustomers;

    const topV = document.getElementById('topVehiclesList');
    if (data.mostRentedVehicles && data.mostRentedVehicles.length > 0) {
      topV.innerHTML = data.mostRentedVehicles.map((v, i) => `
        <div style="display: flex; justify-content: space-between; padding: 0.6rem 0; border-bottom: 1px solid var(--border-glass); font-size: 0.88rem;">
          <span><strong>#${i+1} ${v.vehicleDetails}</strong></span>
          <span style="color: var(--accent-cyan); font-weight: bold;">${v.bookingCount} Bookings</span>
        </div>
      `).join('');
    } else {
      topV.innerHTML = '<span style="color:var(--text-muted)">No data available</span>';
    }

    renderChart(data.monthlyRevenueTrend);
  } catch (err) {
    console.error('Failed to load analytics summary:', err);
  }
}

function renderChart(trendData) {
  const ctx = document.getElementById('revenueChart');
  if (!ctx) return;

  if (revenueChartInstance) {
    revenueChartInstance.destroy();
  }

  const labels = trendData.map(t => t.month);
  const revs = trendData.map(t => t.revenue);

  revenueChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: 'Monthly Revenue ($)',
        data: revs,
        borderColor: '#06b6d4',
        backgroundColor: 'rgba(6, 182, 212, 0.15)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#6366f1'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { labels: { color: '#94a3b8' } }
      },
      scales: {
        x: { ticks: { color: '#94a3b8' }, grid: { color: 'rgba(255,255,255,0.05)' } },
        y: { ticks: { color: '#94a3b8' }, grid: { color: 'rgba(255,255,255,0.05)' } }
      }
    }
  });
}
