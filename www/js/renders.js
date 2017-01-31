var getIconClosure = function(number) {
  return function(r, n) {
    var set = r.set()
      .push(r.image("images/icons/" + number + ".png", 0, 0, 64, 64))
      .push(r.text(30, 80, n.label || n.id)
      .attr({"font-size": 12, "font-weight": "bold"}));
    return set;
  };
}

var COUNT_OF_ICONS = 26;
var renders = new Array(COUNT_OF_ICONS);

for (var i = 0; i < COUNT_OF_ICONS; i++) {
  renders[i] = getIconClosure(i);
}
