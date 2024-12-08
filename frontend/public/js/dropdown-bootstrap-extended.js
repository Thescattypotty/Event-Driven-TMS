/*Dropdown Extend Init*/
window.addEventListener("show.bs.dropdown", e => {
 if (!e.target.hasAttribute("data-dropdown-animation")) return;
  const i = e.target.nextElementSibling;
  i.style.opacity = 0, setTimeout(() => {
      i.style.transform = i.style.transform + " translateY(10px)"
  }), setTimeout(() => {
      i.style.transform = i.style.transform + " translateY(-10px)", i.style.transition = "transform 300ms, opacity 300ms", i.style.opacity = 1
  }, 100)
}), window.addEventListener("hide.bs.dropdown", e => {
  if (!e.target.hasAttribute("data-dropdown-animation")) return;
  const i = e.target.nextElementSibling;
  setTimeout(() => {
      i.style.removeProperty("transform"), i.style.removeProperty("transition")
  })
});