var contentW;
var contentH;

function resize() {
    var h = document.body.offsetHeight;
    h -= document.getElementById("header").offsetHeight;
    
    //if (h < 500) h = 500;
    document.getElementById("content").style.height = h + "px";
    
    contentH = document.getElementById("content").offsetHeight - 20;
    contentW = document.getElementById("content").offsetWidth - 20;
    
    graph.Draw();
}
