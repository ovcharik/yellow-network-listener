function addNodeShow() {
    addNodeHide();
    var request = $.ajax({
        url: "addnode?getForm=",
        success: function (data) {
            $("#content").append(data);
        },
        statusCode: {
            403: function() {error("<b>Error 403:</b> Forbidden.")},
            404: function() {error("<b>Error 404:</b> Form tamplate not found.")},
            405: function() {error("<b>Error:</b> Database does not respond.")}
        }
    });
}

function addNodeHide() {
    $("#addNodeForm").remove();
}

function getNodeInfo(ip) {
    document.getElementById("addNodeSubmit").disabled = true;
    var snmpver = "2c";
    if ($("#addNodeForm select[name=addNodeSnmpV]").val() == "2") {
        //snmpver = $("#addNodeForm select[name=addNodeSnmpV]").val();
    }
    var request = $.ajax({
        type: "POST",
        url: "addnode?getNodeInfo=" + ip,
        data: {
            snmpv: snmpver,
            snmpc: $("#addNodeForm #addNodeSnmpC input").val(),
            snmpu: $("#addNodeForm #addNodeSnmpU input").val(),
            snmpp: $("#addNodeForm #addNodeSnmpP input").val()
        },
        dataType: "json",
        success: function (data) {
            addNodeInfo(data);
            addPortsInfo(data.ports);
            document.getElementById("addNodeSubmit").disabled = false;
        },
        statusCode: {
            403: function() {error("<b>Error 403:</b> Forbidden.")},
            404: function() {error("<b>Error 404:</b> Node not found.")},
            405: function() {error("<b>Error:</b> Database does not respond.")}
        }
    });
}

function addNodeInfo(nodeInfo) {
    $("#addNodeForm .content .nodeInfo").html("<div class=\"info\"><p class=\"key\">IP address:</p><p class=\"value\">" + nodeInfo.ip + "</p></div>");
    $("#addNodeForm .content .nodeInfo").append("<div class=\"info\"><p class=\"key\">Device name:</p><p class=\"value\">" + nodeInfo.name + "</p></div>");
}

function addPortsInfo(ports) {
    $("#addNodeForm .content .portsInfo").text("");
    if (ports.length == 1) {
        $("#addNodeForm .content .portsInfo").text("Нету портов для отображения");
        return;
    }
    var s  = "<div class=\"port head\">";
    s += "<div class=\"port portId\">ID</div>";
    s += "<div class=\"port descriptor\">Descriptor</div>";
    s += "<div class=\"port maxSpeed\">Speed</div>";
    s += "<div class=\"port macAddress\">Mac</div>";
    s += "<div class=\"port ipAddress\">IP</div>";
    s += "</div>\n";
    $("#addNodeForm .content .portsInfo").append(s);
    
    for (var i = 0; i < ports.length - 1; i++) {
        var port = ports[i];
        var s  = "<div class=\"port row\">";
        s += "<div class=\"port portId\">" + port.portId + "</div>";
        s += "<div class=\"port descriptor\">" + port.descriptor + "</div>";
        s += "<div class=\"port maxSpeed\">" + port.maxSpeed + "</div>";
        s += "<div class=\"port macAddress\">" + port.macAddress + "</div>";
        s += "<div class=\"port ipAddress\"><input type=\"text\" class=\"input\" value=\"" + port.ipAddress + "\" /></div>";
        s += "<div class=\"port delete\" onclick=\"portDelete(this)\">x</div>";
        s += "</div>\n";
        
        $("#addNodeForm .content .portsInfo").append(s);
    }
}

function portDelete(el) {
    $(el).parent().remove();
}

function addNodeSend() {
    var ports = "";
    $("#addNodeForm .portsInfo .row").each(function (i) {
        if ($(this).children(".portId").text()) {
            if (ports) {
                ports += ",";
            }
            ports += $(this).children(".portId").text() + "=" + $(this).children(".ipAddress").children("input").val();
        }
    });
    
    var snmpver = "2c";
    if ($("#addNodeForm select[name=addNodeSnmpV]").val() == "2") {
        //snmpver = $("#addNodeForm select[name=addNodeSnmpV]").val();
    }
    
    var request = $.ajax({
        type: "POST",
        url: "addnode?add=",
        data: {
            ports: ports,
            ip: $("#addNodeForm input:text[name=addNodeIP]").val(),
            type: $("#addNodeForm select[name=addNodeType]").val(),
            snmpv: snmpver,
            snmpc: $("#addNodeForm #addNodeSnmpC input").val(),
            snmpu: $("#addNodeForm #addNodeSnmpU input").val(),
            snmpp: $("#addNodeForm #addNodeSnmpP input").val()
        },
        success: function (data) {
            addNodeHide();
            graph.GetData();
        },
        statusCode: {
            403: function() {error("<b>Error 403:</b> Forbidden.")},
            404: function() {error("<b>Error 404:</b> Node not found.")},
            405: function() {error("<b>Error:</b> Database does not respond.")},
            406: function() {error("<b>Error:</b> Unknow error.")},
            
            200: function() {event("<b>Success:</b> Node added.")}
        }
        
    });
}

function addNodeSnmpVChange() {
    if ($("#addNodeForm select[name=addNodeSnmpV]").val() == "1") {
        $("#addNodeForm #addNodeSnmpC").css("display", "inline-block");
        $("#addNodeForm #addNodeSnmpU").css("display", "none");
        $("#addNodeForm #addNodeSnmpP").css("display", "none");
    }
    else {
        $("#addNodeForm #addNodeSnmpC").css("display", "none");
        $("#addNodeForm #addNodeSnmpU").css("display", "inline-block");
        $("#addNodeForm #addNodeSnmpP").css("display", "inline-block");
    }
}

function addNodeDelete(ip) {
    $.ajax({
        type: "GET",
        url: "addnode?delete=" + ip,
        dataType: "json",
        success: function (data) {
            graph.GetData();
        },
        statusCode: {
            403: function() {error("<b>Error 403:</b> Forbidden.")},
            404: function() {error("<b>Error 404:</b> Node not found.")},
            405: function() {error("<b>Error:</b> Database does not respond.")}
        }
    });
}
