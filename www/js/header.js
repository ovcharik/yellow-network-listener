function getHeader() {
    $("#header").html("");
    $.ajax({
        url: "getheader",
        dataType: "html",
        success: function (data, testStatus) {
            $("#header").html(data);
        },
        statusCode: {
            403: function() {error("<b>Error 403:</b> Forbidden.")},
            404: function() {error("<b>Error 404:</b> Template not found.")}
        }
    });
}
