document.addEventListener("DOMContentLoaded", () => {
  const slides = document.querySelectorAll('.relative.w-full');
  const slideContainer = document.querySelector('.slides');
  const indicators = document.querySelectorAll('.indicator-btn');
  const prevBtn = document.getElementById('prevBtn');
  const nextBtn = document.getElementById('nextBtn');

  let currentSlide = 0;

  function showSlide(index) {
    const slideWidth = slides[0].clientWidth;
    slideContainer.style.transform = `translateX(-${index * slideWidth}px)`;
    indicators.forEach(btn => btn.classList.remove('active'));
    if (indicators[index]) indicators[index].classList.add('active');
  }

  function nextSlide() {
    currentSlide = (currentSlide + 1) % slides.length;
    showSlide(currentSlide);
  }

  function prevSlide() {
    currentSlide = (currentSlide - 1 + slides.length) % slides.length;
    showSlide(currentSlide);
  }

  nextBtn.addEventListener('click', nextSlide);
  prevBtn.addEventListener('click', prevSlide);

  indicators.forEach((btn, i) => {
    btn.addEventListener('click', () => {
      currentSlide = i;
      showSlide(i);
    });
  });

  setInterval(nextSlide, 6000);
  showSlide(currentSlide);
});
