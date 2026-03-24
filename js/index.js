const profileBtn = document.getElementById('profileBtn');
const profileMenu = document.getElementById('profileMenu');

profileBtn.addEventListener('click', (e) => {
  e.stopPropagation(); // prevent document click from instantly closing it
  profileMenu.classList.toggle('show');
  profileBtn.classList.toggle('active');
});

profileMenu.addEventListener('click', (e) => {
  e.stopPropagation(); // clicking inside menu won't close it immediately
});

document.addEventListener('click', () => {
  profileMenu.classList.remove('show');
  profileBtn.classList.remove('active');
});