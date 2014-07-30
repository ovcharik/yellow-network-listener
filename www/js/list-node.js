function listNodeShow() {
    if (!graph || $("#listNodeForm").length > 0) {
        return;
    }
    
    var content = "<div class=\"window\" id=\"listNodeForm\">";
    content += "<div class=\"content\">";
    content += "<div class=\"window-title\" onmousedown=\"windowMoveOn(event, $(this).parent().parent())\">";
    content += "<div class=\"close-buttom\" onclick=\"$(this).parent().parent().parent().remove()\">x</div>"
    content += "<p class=\"title\">Список узлов</p>";
    content += "</div>";
    content += "<div class=\"overflow\">";
    
    content += "<p class=\"label\">Фильтр:</p>";
    content += "<input name=\"filtr\" class=\"input\" onkeyup=\"listNodeUpdate()\"/>";
    
    content += "<p class=\"label\"></p>";
    
    content += "<div id=\"listNodeTable\"></div>"
    
    content += "</div></div></div>";
    $("#content").append(content);
    
    listNodeUpdate();
}

function listNodeUpdate() {
    if (!graph || $("#listNodeForm").length <= 0) {
        return;
    }
    
    $("#listNodeTable").html("");
    
    var re = new RegExp($("#listNodeForm input:text[name=filtr]").val());
    
    var rows = "";
    for (var i in graph.data) {
        if (re.test(i) || re.test(graph.data[i].name) || re.test(graph.data[i].ip)) {
            rows += "<div class=\"row\" onclick=\"graph.loads.window.Show(" + i + ")\">";
            
            rows += "<div class=\"id\">" + i + "</div>";
            rows += "<div class=\"name\">" + graph.data[i].name + "</div>";
            rows += "<div class=\"ip\">" + graph.data[i].ip + "</div>";
            
            rows += "</div>";
        }
    }
    
    $("#listNodeTable").append(rows);
}
