let cart = JSON.parse(localStorage.getItem('cart')) || [];

function toggleCart() {
  const panel = document.getElementById('cart-panel');
  panel.classList.toggle('show');
  panel.classList.toggle('hidden');
  renderCart();
}

function addToCart(name, price, image) {
  const existing = cart.find(item => item.name === name);
  if (existing) {
    existing.quantity++;
  } else {
    cart.push({ name, price, quantity: 1, image });
  }
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartCount();
  showToast();
  renderCart();
}

function updateCartCount() {
  const count = cart.reduce((acc, item) => acc + item.quantity, 0);
  document.getElementById('cart-count').innerText = count;
}

function renderCart() {
  const container = document.getElementById('cart-items');
  container.innerHTML = '';

  if (cart.length === 0) {
    container.innerHTML = '<p class="text-gray-500 text-sm">Tu carrito está vacío.</p>';
    return;
  }

  cart.forEach((item, index) => {
    const div = document.createElement('div');
    div.className = "flex items-center mb-4 gap-3";
    div.innerHTML = `
      <img src="${item.image}" alt="${item.name}" class="w-16 h-16 object-cover rounded border">
      <div class="flex-1">
        <p class="font-medium">${item.name}</p>
        <div class="flex items-center gap-2 mt-1">
          <button onclick="decreaseQuantity(${index})" class="px-2 text-sm bg-gray-200 rounded hover:bg-gray-300">-</button>
          <span class="text-sm">${item.quantity}</span>
          <button onclick="increaseQuantity(${index})" class="px-2 text-sm bg-gray-200 rounded hover:bg-gray-300">+</button>
        </div>
        <p class="text-sm text-orange-600 font-semibold mt-1">$${(item.price * item.quantity).toLocaleString()}</p>
      </div>
      <button onclick="removeFromCart(${index})" class="text-red-600 hover:text-red-800 text-sm">
        <i class="fas fa-trash"></i>
      </button>
    `;
    container.appendChild(div);
  });

  const total = cart.reduce((acc, item) => acc + item.price * item.quantity, 0);
  const totalDiv = document.createElement('div');
  totalDiv.className = "mt-4 pt-4 border-t text-right font-semibold text-lg text-orange-600";
  totalDiv.innerHTML = `Total: $${total.toLocaleString()}`;
  container.appendChild(totalDiv);
}

function removeFromCart(index) {
  cart.splice(index, 1);
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartCount();
  renderCart();
}

function increaseQuantity(index) {
  cart[index].quantity++;
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartCount();
  renderCart();
}

function decreaseQuantity(index) {
  if (cart[index].quantity > 1) {
    cart[index].quantity--;
  } else {
    cart.splice(index, 1);
  }
  localStorage.setItem('cart', JSON.stringify(cart));
  updateCartCount();
  renderCart();
}

function showToast() {
  const toast = document.getElementById('toast');
  toast.classList.remove('hidden');
  setTimeout(() => {
    toast.classList.add('hidden');
  }, 3000);
}

// Inicializar al cargar
document.addEventListener('DOMContentLoaded', () => {
  updateCartCount();
  renderCart();
});
