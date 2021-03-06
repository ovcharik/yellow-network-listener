function alert(msg, color) {
    this.color = color || "rgba(150, 200, 255, 0.7)";
    $("#content").append("<div id=\"alert\">" + msg + "</div>");
    $("#alert").css("background", this.color);
    
    setTimeout(function() {
        $("#alert").remove();
    }, 3000);
}

function error(msg) {
    alert(msg, "rgba(255, 150, 150, 0.7)");
}

function event(msg) {
    alert(msg, "rgba(150, 255, 150, 0.7)");
}
