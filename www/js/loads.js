YeSGraph.Loads = function(g) {
    this.graph = g;
    this.chartNodes = 10;
}

YeSGraph.Loads.prototype = {
    
    timeout: false,
    loads: new Array(),
    
    timer: false,
    updateTimer: true,
    
    chartData: false,
    
    GetCurrent: function() {
        var l = this;
        $.ajax({
            url: "getload?current=",
            dataType: "json",
            success: function(data) {
                l.TranslateAjaxData(data);
                l.Update();
            },
            statusCode: {
                403: function() {error("<b>Error 403:</b> Forbidden.")}
            }
        });
    },
    
    TranslateAjaxData: function(data) {        
        this.updateTimer = (this.timeout != data.timeout);
        this.timeout = data.timeout;
        
        for (var i in data.loads) {
            var load = data.loads[i];
            
            if (!graph.data || !graph.data[load.nodeId] || !graph.data[load.nodeId].ports[load.portId]) {
                continue;
            }
            var port = graph.data[load.nodeId].ports[load.portId];
            
            if (!this.loads[load.nodeId]) {
                this.loads[load.nodeId] = new Array();
            }
            
            this.loads[load.nodeId][load.portId] = {
                in:    parseInt(load.in),
                out:   parseInt(load.out),
                date:  parseInt(load.date),
                speed: port.speed,
                
                persentIn:  0,
                persentOut: 0,
                color:      "rgb(0, 255, 0)"
            };
            
            if (this.loads[load.nodeId][load.portId].speed > 0) {
                this.loads[load.nodeId][load.portId].persentIn  = (parseInt(load.in)  / port.speed * 100).toFixed(2);
                this.loads[load.nodeId][load.portId].persentOut = (parseInt(load.out) / port.speed * 100).toFixed(2);
                
                var persent = (parseInt(load.in) + parseInt(load.out)) / port.speed * 100;
                var r = 0, g = 0, b = 0;
                if (persent < 30 && persent >= 0) {
                    g = 255;
                    r = Math.round(255 * persent / 30);
                }
                else if (persent > 30) {
                    r = 255;
                    g = 255 - Math.round(255 * (persent - 30) / 70);
                    if (g < 0) g = 0;
                    if (g > 255) g = 255;
                }
                this.loads[load.nodeId][load.portId].color = "rgb(" + r + ", " + g + ", " + b + ")";
            }
            
            if (!this.chartData) {
                this.chartData = new Array();
            }
            if (!this.chartData[load.nodeId]) {
                this.chartData[load.nodeId] = new Array();
            }
            if (!this.chartData[load.nodeId][load.portId]) {
                this.chartData[load.nodeId][load.portId] = this.GetClearChartData();
            }
            this.chartData[load.nodeId][load.portId].unshift({
                in:  this.loads[load.nodeId][load.portId].in,
                out: this.loads[load.nodeId][load.portId].out
            });
            this.chartData[load.nodeId][load.portId].pop();
        }
    },
    
    GetClearChartData: function() {
        var response = new Array();
        for (var i = 0; i < this.chartNodes; i++) {
            response[i] = {
                in:  0,
                out: 0,
            };
        }
        return response;
    },
    
    Update: function() {
        for (var i in this.loads) {
            for (var j in this.loads[i]) {
                if ($("#l_" + i + "_" + j).length > 0) {
                    $("#l_" + i + "_" + j + " tspan").text(this.loads[i][j].persentIn + "% - " + this.loads[i][j].persentOut + "%");
                }
                if ($("#p_" + i + "_" + j).length > 0) {
                    $("#p_" + i + "_" + j).attr("stroke", this.loads[i][j].color);
                }
                if ($("#lc_" + i + "_" + j).length > 0) {
                    this.DrawPortChart(i, j)
                }
            }
        }
        
        if (this.updateTimer) {
            this.updateTimer = false;
            if (this.timer) {
                clearInterval(this.timer);
                this.timer = false;
            }
            
            var l = this;
            if (this.timeout > 1000) this.timer = setInterval(function () {
                l.Run();
            }, this.timeout);
        }
    },
    
    Run: function() {
        this.GetCurrent();
    },
    
    Stop: function() {
        this.updateTimer = false;
        if (this.timer) {
            clearInterval(this.timer);
            this.timer = false;
        }
    },
    
    DrawPortChart: function(node, port) {
        if (!this.chartData
            || !this.chartData[node]
            || !this.chartData[node][port]
            || $("#lc_" + node + "_" + port).length <= 0
        ) {
            return;
        }
        
        var canvas = document.getElementById("lc_" + node + "_" + port);
        var ctx = canvas.getContext('2d');
        var w = canvas.width;
        var h = canvas.height;
        
        ctx.clearRect(0, 0, w, h);
        
        var max = 0;
        var chart = this.chartData[node][port];
        
        for (var i in chart) {
            if (chart[i].in > max) max = chart[i].in;
            if (chart[i].out > max) max = chart[i].out;
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
        
        var stX = 50;
        var gpW = w - 100;
        
        ctx.lineWidth = 1;
        ctx.lineCap = "round";
        ctx.lineJoin = "round";
        ctx.miterLimit = 10.0;
        
        ctx.strokeStyle = 'red';
        ctx.fillStyle = 'rgba(255, 0, 0, 0.1)';
        
        ctx.beginPath();
        ctx.moveTo(stX, h + 5);
        
        for (var i in chart) {
            var y = h + 5;
            var x = stX + Math.round(gpW / (chart.length - 1) * i);
            
            if (chart[i].in > 0) {
                y = Math.round(h - (chart[i]['in'] / max * h));
            }
            ctx.lineTo(x, y);
        }
        
        ctx.lineTo(stX + gpW, h + 5);
        ctx.lineTo(stX, h + 5);
        
        ctx.closePath();
        ctx.fill();
        ctx.stroke();
        
        
        ctx.strokeStyle = 'green';
        ctx.fillStyle = 'rgba(0, 255, 0, 0.1)';
        
        ctx.beginPath();
        ctx.moveTo(stX, h + 5);
        
        for (var i in chart) {
            var y = h + 5;
            var x = stX + Math.round(gpW / (chart.length - 1) * i);
            
            if (chart[i].out > 0) {
                y = Math.round(h - (chart[i].out / max * h));
            }
            ctx.lineTo(x, y);
        }
        
        ctx.lineTo(stX + gpW, h + 5);
        ctx.lineTo(stX, h + 5);
        
        ctx.closePath();
        ctx.fill();
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
        
        if ($("#lv_" + node + "_" + port).length > 0) {
            var v = ["B", "KB", "MB", "GB", "TB"];
            var input = chart[0].in;
            var output = chart[0].out;
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
                $("#lv_" + node + "_" + port).html("Rx: " + input + "<br/>" + "Tx: " + output);
            }
        }
    },
    
    history: {
        date: false,
        all:  false,
    },
    
    UpdateHistory: function() {
        var l = this;
        for (var id in l.graph.data) {
            $.ajax({
                url: "getload?history=" + id,
                dataType: "json",
                success: function(data) {
                    for (var i in data.loads) {
                        var load = data.loads[i];
                        
                        var port = load.portId;
                        var node = load.nodeId;
                        var date = parseInt(load.date);
                        var sIn  = parseInt(load.out);
                        var sOut = parseInt(load.in);
                        
                        if (!l.history.all) {
                            l.history.all = new Array();
                        }
                        
                        if (!l.history.all[node]) {
                            l.history.all[node] = new Array();
                        }
                        
                        if (!l.history.all[node][port]) {
                            l.history.all[node][port] = new Array();
                        }
                        
                        l.history.all[node][port].push({
                            in: sIn,
                            out: sOut,
                            date: date
                        });
                    }
                    for (var i in l.history.all) {
                        for (var j in l.history.all[i]) {
                        
                            if (!l.history.date) {
                                l.history.date = new Array();
                            }
                            if (!l.history.date[i]) {
                                l.history.date[i] = new Array();
                            }
                            if (!l.history.date[i][j]) {
                                l.history.date[i][j] = new Array();
                            }
                            
                            var port = l.history.date[i][j];
                            
                            for (var k in l.history.all[i][j]) {
                                var load = l.history.all[i][j][k];
                                var date = new Date(load.date);
                                
                                var d = date.getDate();
                                var m = date.getMonth() + 1;
                                var y = date.getFullYear();
                                var h = date.getHours() + 1;
                                
                                if (!port[y]) {
                                    port[y] = new Array();
                                }
                                if (!port[y][m]) {
                                    port[y][m] = new Array();
                                }
                                if (!port[y][m][d]) {
                                    port[y][m][d] = {
                                        count: 0,
                                        in: 0,
                                        out: 0,
                                        hours: new Array()
                                    };
                                }
                                port[y][m][d].in += load.in;
                                port[y][m][d].out += load.out;
                                port[y][m][d].count++;
                                
                                if (!port[y][m][d].hours[h]) {
                                    port[y][m][d].hours[h] = {
                                        count: 0,
                                        in: 0,
                                        out: 0
                                    };
                                }
                                
                                port[y][m][d].hours[h].in  += load.in;
                                port[y][m][d].hours[h].out += load.out;
                                port[y][m][d].hours[h].count++;
                            }
                        }
                    }
                },
                statusCode: {
                    403: function() {error("<b>Error 403:</b> Forbidden.")}
                }
            });
        }
    }
}
