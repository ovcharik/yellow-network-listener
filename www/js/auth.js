var authClearUFlag = true;
var authClearPFlag = true;

function authSend() {
    var request = $.ajax({
        type: "POST",
        url: "auth",
        data: {
            username: $("form input:text[name=username]").val(),
            password: $("form input:password[name=password]").val()
        },
        statusCode: {
            403: function () {
                $("form input:text[name=username]").css("color", "red");
                $("form input:password[name=password]").css("color", "red");
                authClearUFlag = true;
                authClearPFlag = true;
                error("<b>Error 403:</b> Forbidden.");
            },
            200: function () {
                getHeader();
                getContent();
                authClearUFlag = true;
                authClearPFlag = true;
            },
            405: function() {error("<b>Error:</b> Database does not respond.")}
        }
        
    });
    
    request.fail(function(jqXHR, textStatus) {
    });
}

function authClearU() {
    if (authClearUFlag) {
        $("form input:text[name=username]").val("");
        $("form input:text[name=username]").css("color", "black");
        authClearUFlag = false;
    }
}

function authClearP() {
    if (authClearPFlag) {
        $("form input:password[name=password]").val("");
        $("form input:password[name=password]").css("color", "black");
        authClearPFlag = false;
    }
}

function authLogout() {
    $.ajax({
        url: "auth?logout=",
        success: function () {
            graph.loads.Stop();
            $("#header").html("");
            $("#content").html("");
            getContent();
        }
    });
}
