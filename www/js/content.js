function getContent() {
    $.ajax({
        url: "getcontent",
        dataType: "html",
        success: function (data, textStatus) {
            $("#content").html(data);
        },
        statusCode: {
            403: function() {error("<b>Error 403:</b> Forbidden.")},
            404: function() {error("<b>Error 404:</b> Template not found.")}
        }
    });
}
