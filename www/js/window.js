YeSGraph.Loads.Window = function(g) {
    this.graph = g;
}

YeSGraph.Loads.Window.prototype = {

    Show: function(id) {
        if (!this.graph) {
            return;
        }
        var g = this.graph;
        $.ajax({
            url: "getload?getWindow=",
            success: function (data) {
                
                var newElementId = "w_" + id;
                
                if ($("#" + newElementId).length > 0) {
                    return;
                }
                
                $("#content").append(data);
                $("#w_node").attr("id", newElementId);
                            
                var n = g.data[id];
                
                var content = "";
                content += "<div class=\"allPorts\">";
                content += "<p class=\"title2\">Информация об узле:</p>\n";
                content += "<img class=\"typeimg\" src=\"images/icons/" + n.type + ".png\" width=\"128\" alt=\"\" />"
                content += "<div class=\"info\"><p class=\"key\">Node Id:</p><p class=\"value\">" + id + "</p></div>"
                content += "<div class=\"info\"><p class=\"key\">IP address:</p><p class=\"value\">" + n.ip + "</p></div>"
                content += "<div class=\"info\"><p class=\"key\">Device name:</p><p class=\"value\">" + n.name + "</p></div>"
                content += "<div class=\"label\"><a href=\"#\" onclick=\"addNodeDelete(\'" + n.ip + "\'); $(\'#" + newElementId + "\').remove();\">Удалить узел</a></div>";
                content += "<p class=\"title2\">Порты узла:</p>\n";
                
                content += "<div class=\"table\">\n";
                for (var i in n.ports) {
                    var port = n.ports[i];
                    if (!port) {
                        continue;
                    }
                    content += "<div class=\"row\">\n";
                    
                    content += "<div class=\"portId\">" + i + "</div>";
                    content += "<div class=\"descr\">" + port.descr + "<br/>" + port.ip + "</div>";
                    content += "<div class=\"vals\" id=\"lv_" + id + "_" + i + "\">Rx: 0.00 B/s<br/>Tx: 0.00 B/s</div>";
                    content += "<div class=\"graph\"><canvas id=\"lc_" + id + "_" + i + "\" width=\"200\" height=\"50\"></canvas></div>";
                    content += "<div class=\"history\"><p onclick=\"graph.loads.window.ShowHistory(" + id + ", " + i + ")\">></p></div>";
                    
                    content += "</div>\n";
                    
                    //clearGraphLoad(port.nodeId, port.portId);
                }
                content += "</div>";
                content += "</div>";
                content += "<div class=\"historyPort\" style=\"display: none\"></div>";
                $("#" + newElementId + " .content .overflow").append(content);
                
                if (g.loads) g.loads.Update();
            },
            statusCode: {
                403: function() {error("<b>Error 403:</b> Forbidden.")},
                404: function() {error("<b>Error 404:</b> Form tamplate not found.")},
                405: function() {error("<b>Error:</b> Database does not respond.")}
            }
        });
    },
    
    ShowHistory: function(n, p) {
        if (!this.graph || !this.graph.data[n].ports[p]) {
            return;
        }
        if (!this.graph.loads.history.date || !this.graph.loads.history.date[n] || !this.graph.loads.history.date[n][p]) {
            alert("Истории недоступна, возможно она еще не накопилась. Попробуйте обновить страницу и посмотреть историю позднее.");
            return;
        }
        if ($("#w_" + n).length > 0) {
            $("#w_" + n + " .allPorts").css("display", "none");
            $("#w_" + n + " .historyPort").css("display", "block");
            
            $("#w_" + n + " .title").html("<div class=\"back-buttom\" onclick=\"graph.loads.window.HideHistory(" + n + ")\">\<</div>История нагрузки порта");
            
            var port = this.graph.data[n].ports[p];
            var load = this.graph.loads.history.date[n][p];
            
            var content = "";
            content += "<div class=\"title2\">Информация о порте</div>";
            
            content += "<div class=\"info\"><p class=\"key\">#:</p><p class=\"value\">" + p + "</p></div>";
            content += "<div class=\"info\"><p class=\"key\">Descriptor:</p><p class=\"value\">" + port.descr + "</p></div>";
            content += "<div class=\"info\"><p class=\"key\">IP address:</p><p class=\"value\">" + port.ip + "</p></div>";
            content += "<div class=\"info\"><p class=\"key\">Max speed:</p><p class=\"value\">" + port.speed + " B/s</p></div>";
            
            content += "<div class=\"title2\">Период</div>";
            
            content += "<div class=\"time\">";
            
            content += "<div class=\"year\">";
            content += "<p class=\"label\">Год:</p>";
            content += "<select size=\"1\" name=\"year\" onchange=\"loadWindowYUpdate(" + n + ", " + p + ")\">";
            
            var selected = "";
            for (var y in load) {
                if (load.length - 1 == y) {
                    selected = "selected";
                }
                content += "<option value=\"" + y + "\" " + selected + ">" + y + "</option>";
            }
            content += "</select>";
            content += "</div>";
            
            content += "</div>";
            
            content += "<div class=\"title2\">График</div>";
            content += "<canvas id=\"lhc_" + n + "_" + p + "\" width=\"480\" height=\"200\"></canvas>";
            
            $("#w_" + n + " .historyPort").html(content);
            
            loadWindowYUpdate(n, p);
        }
    },
    
    HideHistory: function(n) {
        if ($("#w_" + n).length > 0) {
            $("#w_" + n + " .allPorts").css("display", "block");
            $("#w_" + n + " .historyPort").css("display", "none");
            $("#w_" + n + " .title").html("Нагрузка на узел");
        }
    }
}

function loadWindowYUpdate(n, p) {
    if (!graph) {
        return;
    }
    if ($("#w_" + n).length > 0) {
        var load = graph.loads.history.date[n][p];
        var y = $("#w_" + n + " select[name=year]").val();
        if (!y) return;
        
        $("#w_" + n + " .month").remove();
            
        var c = "";
        c += "<div class=\"month\">";
        c += "<p class=\"label\">Месяц:</p>";
        c += "<select size=\"1\" name=\"month\" onchange=\"loadWindowMUpdate(" + n + ", " + p + ")\">";
        
        var selected = "";
        for (var m in load[y]) {
            if (load[y].length - 1 == m) {
                selected = "selected";
            }
            c += "<option value=\"" + m + "\" " + selected + ">" + m + "</option>";
        }
        
        c += "</select>";
        c += "</div>";
            
        $("#w_" + n + " .time").append(c);
        
        loadWindowMUpdate(n, p);
    }
}

function loadWindowMUpdate(n, p) {
    if (!graph) {
        return;
    }
    if ($("#w_" + n).length > 0) {
        var load = graph.loads.history.date[n][p];
        var y = $("#w_" + n + " select[name=year]").val();
        var m = $("#w_" + n + " select[name=month]").val();
        if (!y || !m) return;
        
        $("#w_" + n + " .day").remove();
            
        var c = "";
        c += "<div class=\"day\">";
        c += "<p class=\"label\">День:</p>";
        c += "<select size=\"1\" name=\"day\" onchange=\"loadWindowGraphDraw(" + n + ", " + p + ")\">";
        
        c += "<option value=\"0\" selected>---</option>";
        for (var d in load[y][m]) {
            c += "<option value=\"" + d + "\">" + d + "</option>";
        }
        
        c += "</select>";
        c += "</div>";
            
        $("#w_" + n + " .time").append(c);
        
        loadWindowGraphDraw(n, p);
    }
}

function loadWindowGraphDraw(n, p) {
    if (!graph || $("#lhc_" + n + "_" + p).length <= 0) {
        return;
    }
    
    var canvas = document.getElementById("lhc_" + n + "_" + p);
    var ctx = canvas.getContext('2d');
    var w = canvas.width;
    var h = canvas.height;
    
    ctx.clearRect(0, 0, w, h);
    
    var y = $("#w_" + n + " select[name=year]").val();
    var m = $("#w_" + n + " select[name=month]").val();
    var d = $("#w_" + n + " select[name=day]").val();
    
    var load = false;
    var nodes = 0;
    if (d != 0) {
        load = graph.loads.history.date[n][p][y][m][d].hours;
        nodes = 25;
    }
    else {
        load = graph.loads.history.date[n][p][y][m];
        nodes = 32;
    }
    
    if (!load) {
        return;
    }
    
    var chartIn = new Array(nodes);
    var chartOut = new Array(nodes);
    
    for (var i = 0; i < nodes; i++) {
        if (load[i]) {
            chartIn[i]  = load[i].in /load[i].count;
            chartOut[i] = load[i].out/load[i].count;
        }
        else {
            chartIn[i]  = 0;
            chartOut[i] = 0;
        }
    }
    
    var max = 0;
    for (var i = 0; i < nodes; i++) {
        if (chartIn[i]  > max) max = chartIn[i];
        if (chartOut[i] > max) max = chartOut[i];
    }
    
    max = Math.ceil(max);
    
    var lTop, lBottom;
    {
        var v = ["B", "KB", "MB", "GB", "TB"];
        var m = max;
        var i = 0;
        while (m > 1000 && i < 5) {
            m /= 1000;
            i++;
        }
        m = Math.ceil(m);
        lTop = Math.ceil(m) + " " + v[i] + "/s";
        lBottom = "0 " + v[i] + "/s";
        max = m;
        for (var j = 0; j < i; j++) {
            max *= 1000;
        }
    }
    
    ctx.lineWidth = 1;
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    ctx.miterLimit = 10.0;
    
    
    ctx.strokeStyle = 'red';
    ctx.fillStyle = 'rgba(255, 0, 0, 0.1)';
    
    ctx.beginPath();
    ctx.moveTo(0, h);
    
    for (var i in chartIn) {
        var x = Math.round(w / (chartIn.length - 1) * i);
        var y = Math.round(h - (chartIn[i] / max * h));
        ctx.lineTo(x, y);
    }
    
    ctx.lineTo(w, h);
    ctx.lineTo(0, h);
    
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
    
    
    ctx.strokeStyle = 'green';
    ctx.fillStyle = 'rgba(0, 255, 0, 0.1)';
    
    ctx.beginPath();
    ctx.moveTo(0, h);
    
    for (var i in chartOut) {
        var x = Math.round(w / (chartOut.length - 1) * i);
        var y = Math.round(h - (chartOut[i] / max * h));
        ctx.lineTo(x, y);
    }
    
    ctx.lineTo(w, h);
    ctx.lineTo(0, h);
    
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
    
    
    ctx.lineWidth = 1;
    ctx.strokeStyle = 'rgba(0, 0, 0, 0.1)';
    for (var i = 0; i <= nodes; i++) {
        var x = Math.round(w / (nodes - 1) * i);
        ctx.moveTo(x, 0);
        ctx.lineTo(x, h);
    }
    ctx.stroke();
    
    
    ctx.lineWidth = 2;
        
    ctx.strokeStyle = 'red';
    
    ctx.beginPath();
    ctx.moveTo(w - 30, Math.round(h / 2 - h / 6));
    ctx.lineTo(w - 20, Math.round(h / 2 - h / 6));
    ctx.closePath();
    ctx.stroke();
    
    ctx.strokeStyle = 'green';
    
    ctx.beginPath();
    ctx.moveTo(w - 30, Math.round(h / 2 + h / 6));
    ctx.lineTo(w - 20, Math.round(h / 2 + h / 6));
    ctx.closePath();
    ctx.stroke();
    
    ctx.font = '10px sans-serif';
    ctx.textBaseline = 'middle';
    ctx.fillStyle = "black";
    ctx.fillText('Rx', w - 18, Math.round(h / 2 - h / 6));
    ctx.fillText('Tx', w - 18, Math.round(h / 2 + h / 6));
    
    ctx.textBaseline = 'bottom';
    ctx.fillText(lBottom, 0, h);
    
    ctx.textBaseline = 'top';
    ctx.fillText(lTop, 0, 0);
    
    $("#lhc_" + n + "_" + p).mousemove(function(e) {
        if (!graph) {
            return;
        }
        var x = e.pageX;
        var y = e.pageY;
        
        var left = $("#lhc_" + n + "_" + p).offset().left;
        var top  = $("#lhc_" + n + "_" + p).offset().top;
        
        var node = 0;
        
        var year = $("#w_" + n + " select[name=year]").val();
        var month = $("#w_" + n + " select[name=month]").val();
        var day = $("#w_" + n + " select[name=day]").val();
        var hour = "";
        
        var width = canvas.width;
        
        if (day == 0) {
            day = Math.round((x - left) / (width / (nodes - 2))) + 1;
            node = day;
            
        }
        else {
            hour = Math.round((x - left) / (width / (nodes - 2)));
            node = hour + 1;
            hour += ":00";
        }
        
        var input = chartIn[node];
        var output = chartOut[node];
        
        {
            var v = ["B", "KB", "MB", "GB", "TB"];
            if (input >= 0 && output >= 0) {
                var i = 0;
                var j = 0;
                while (input > 1000 && i < 5) {
                    input /= 1000;
                    i++;
                }
                while (output > 1000 && j < 5) {
                    output /= 1000;
                    j++;
                }
                input = input.toFixed(2) + " " + v[i] + "/s";
                output = output.toFixed(2) + " " + v[j] + "/s";;
            }
        }
        
        var date = day + "." + month + "." + year + " " + hour;
        
        $("#hint").remove();
        $("body").append("<div id=\"hint\">" + date + "<br/>Rx: " + input + "<br/>Tx: " + output + "</div>");
        $("#hint").offset({left: x - $("#hint").width() / 2, top: y - $("#hint").height() - 15});
    });
    
    $("#lhc_" + n + "_" + p).mouseleave(function(e) {
        $("#hint").remove();
    });
}

var windowMoveFlag = false;
var windowMoveX;
var windowMoveY;
var windowMoveMouseX;
var windowMoveMouseY;
var windowMoveElement;

function showSea() {
    if (!graph || $("#seaWindow").length > 0) {
        return;
    }
    
    var content = "<div class=\"window\" id=\"seaWindow\">";
    content += "<div class=\"content\" style=\"width: 500px; left: -250px\">";
    content += "<div class=\"window-title\" onmousedown=\"windowMoveOn(event, $(this).parent().parent())\">";
    content += "<div class=\"close-buttom\" onclick=\"$(this).parent().parent().parent().remove()\">x</div>"
    content += "<p class=\"title\">Море</p>";
    content += "</div>";
    content += "<div class=\"overflow\">";
    
    content += "<img src=\"./images/sea.jpg\" style=\"width: 100%\">"
    
    content += "</div></div></div>";
    $("#content").append(content);
}

function windowMoveOn(e, element) {
    if (element.length > 0) {
        windowMoveElement = element;
        
        $(".window").css("z-index", "0");
        windowMoveElement.css("z-index", "1");
        
        windowMoveFlag = true;
        windowMoveX = windowMoveElement.position().left;
        windowMoveY = windowMoveElement.position().top;
        windowMoveMouseX = e.clientX;
        windowMoveMouseY = e.clientY;
        
        $("*").css("-moz-user-select", "-moz-none")
        .css("-o-user-select", "none")
        .css("-khtml-user-select", "none")
        .css("-webkit-user-select", "none")
        .css("user-select", "none");
    }

    $("#content").mouseup(function () {
        if (windowMoveFlag) {
            windowMoveFlag = false;
            
            $("*").css("-moz-user-select", "-moz-auto")
            .css("-o-user-select", "auto")
            .css("-khtml-user-select", "auto")
            .css("-webkit-user-select", "auto")
            .css("user-select", "auto");
        }
    });
    
    $("#content").mousemove(function (e) {
        if (windowMoveFlag && windowMoveElement.length > 0) {
            var x, y;
            x = windowMoveMouseX - e.clientX;
            y = windowMoveMouseY - e.clientY;
            
            windowMoveX -= x;
            windowMoveY -= y;
            if (windowMoveY < 0) windowMoveY = 0;
            
            windowMoveMouseX = e.clientX;
            windowMoveMouseY = e.clientY;
            
            windowMoveElement.css("left", windowMoveX + "px");
            windowMoveElement.css("top", windowMoveY + "px");
        }
    });
}

function windowInit() {
    
}
