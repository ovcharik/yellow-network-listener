var YeSGraph = function(id) {
    this.id = id;
    this.jObject = $("#" + id);
    this.EventInit();
    this.loads = new YeSGraph.Loads(this);
    this.loads.window = new YeSGraph.Loads.Window(this);
}

YeSGraph.prototype = {
    
    data: false,
    
    scale: {
        current: 1,
        prev: 1,
        
        min: 1,
        max: 3,
        
        deltaP: 0.25,
        deltaM: 0.20
    },
    
    mouse: {
        pressed: false,
        x: 0,
        y: 0
    },
    
    /**
     * Ajax request
     */
    GetData: function() {
        var g = this;
        $.ajax({
            url: "getgraph",
            dataType: "json",
            success: function(data) {
                g.data = g.TranslateAjaxData(data);
                g.loads.Run();
                g.Draw();
                listNodeUpdate();
                g.loads.UpdateHistory();
            },
            statusCode: {
                403: function() {error("<b>Error 403:</b> Forbidden.")}
            }
        });
    },
    
    /**
     * init mouse event
     */
    EventInit: function() {
        var g = this;
        
        g.jObject.mousewheel(function (e, d) {
            var left = 0;
            var top = 0;
            
            if (d != 0) {
            
                g.scale.prev = g.scale.current;
                
                var ds = g.scale.current;
                if (d > 0) {
                    while (d > 0) {
                        g.scale.current += g.scale.deltaP * g.scale.current;
                        d--;
                        if (g.scale.current > g.scale.max) {
                            g.scale.current = g.scale.max;
                            break;
                        }
                    }
                }
                else {
                    while (d < 0) {
                        g.scale.current -= g.scale.deltaM * g.scale.current;
                        d++;
                        if (g.scale.current < g.scale.min) {
                            g.scale.current = g.scale.min;
                            break;
                        }
                    }
                }
                ds = g.scale.current / ds;
                
                if (g.scale.prev == g.scale.current) {
                    return;
                }
                
                left = e.pageX - (e.pageX - g.jObject.children("svg").offset().left) * ds;
                top  = e.pageY - (e.pageY - g.jObject.children("svg").offset().top ) * ds;
            }
            
            g.jObject.children("svg")
            .css("-webkit-transform", "scale(" + g.scale.current + ")")
            .css("-moz-transform", "scale(" + g.scale.current + ")")
            .css("-o-transform", "scale(" + g.scale.current + ")")
            .css("transform", "scale(" + g.scale.current + ")");
        
            g.jObject.children("svg").offset(g.GetOffset(left, top));
        });
        
        g.jObject.mousedown(function (event) {
            if (event.button == 0 && (event.target.nodeName == "DIV" || event.target.nodeName == "svg")) {
                
                g.mouse.pressed = true;
                g.mouse.x = event.pageX;
                g.mouse.y = event.pageY;
            }
        });
        
        g.jObject.mousemove(function (event) {
            if (g.mouse.pressed) {
                var x = g.jObject.children("svg").offset().left + event.pageX - g.mouse.x;
                var y = g.jObject.children("svg").offset().top + event.pageY - g.mouse.y;
                
                g.jObject.children("svg").offset(g.GetOffset(x, y));
                
                g.mouse.x = event.pageX;
                g.mouse.y = event.pageY;
            }
        });
        
        g.jObject.mouseup(function () {
            g.mouse.pressed = false;
        });
    },
    
    /**
     * Check position and return rigth offset
     */
    GetOffset: function(left, top) {
        
        var ox = this.jObject.offset().left;
        var oy = this.jObject.offset().top;
        var w = this.jObject.width();
        var h = this.jObject.height();
        var ds = this.scale.current - this.scale.min;
        
        if (left > ox) left = ox;
        if (top  > oy) top  = oy;
        
        if (left < ox - w * ds) left = ox - w * ds;
        if (top  < oy - h * ds) top  = oy - h * ds;
        
        return {left: left, top: top};
    },
    
    GetMaxSpeed: function(nodeId, portId) {
        if (data && data[nodeId] && data[nodeId][portId]) {
            return data[nodeId][portId].speed;
        }
        return false;
    },
    
    TranslateAjaxData: function(data) {
        if (!data.nodes) {
            return false;
        }
        
        var response = new Array();
        var target = new Array();
        
        for (var i in data.nodes) {
            var node = data.nodes[i];
            if (!node) continue;
            
            response[node.id] = {
                ip:     node.ip,
                name:   node.name,
                type:   parseInt(node.type),
                ports:  new Array()
            }
            
            for (var j in node.ports) {
                var port = node.ports[j];
                if (!port) continue;
                
                response[node.id].ports[port.portId] = {
                    descr:  port.descriptor,
                    ip:     port.ipAddress,
                    mac:    port.macAddress,
                    speed:  parseInt(port.maxSpeed),
                    
                    targetNode: false,
                    targetPort: false
                }
                
                if (port.ipAddress) {
                    target.push({
                        ip:     port.ipAddress,
                        node:   node.id,
                        port:   port.portId
                    });
                }
            }
        }
        
        for (var i in response) {
            var node = response[i];
            
            for (var k in target) {
                var t = target[k];
                if (t.ip == node.ip && response[t.node] && response[t.node].ports[t.port]) {
                    response[t.node].ports[t.port].targetNode = i;
                }
            }
        }
        
        for (var i in response) {
            var node = response[i];
            
            for (var j in node.ports) {
                port = node.ports[j];
                if (port.targetNode) {
                    for (var k in response[port.targetNode].ports) {
                        var p = response[port.targetNode].ports[k];
                        if (p.targetNode == i) {
                            p.targetPort = j;
                        }
                    }
                }
            }
        }

        
        return response;
    },
    
    Draw: function() {
        if (!this.data) {
            return false;
        }
        
        var w = this.jObject.parent().width();
        var h = this.jObject.parent().height();
        
        this.jObject.html("");
        
        var graph = new Graph();
        graph.scale = this.scale;
        
        var links = new Array();
        var ips = new Array();
        var l1 = new Array();
        var l2 = new Array();
        var p1 = new Array();
        var s1 = new Array();
        
        var edges = new Array();
        
        var nodesCount = 0;
        
        // Added nodes, and creating edges table
        for (var i in this.data) {
            var self = this;
            var node = this.data[i];
            if (!node.name) {
                node.name = "node";
            }
            var label = node.name + "\n(" + node.ip + ")";
            
            var img = node.type;
            if (0 > img || img > 25) img = 0;
            
            graph.addNode(i, {render: renders[img], label: label, yesGraph: self});
            nodesCount++;
        }
        
        var edges = new Array();
        
        for (var i in this.data) {
            var node = this.data[i];
            
            for (var j in node.ports) {
                var port = node.ports[j];
                
                var sourceNode = i;
                var targetNode = port.targetNode;
                
                if (!edges[targetNode]) {
                    edges[targetNode] = new Array();
                }
                if (!edges[sourceNode]) {
                    edges[sourceNode] = new Array();
                }
                
                if (!targetNode || (edges[sourceNode][targetNode])) {
                    continue;
                }
                
                var n = i;
                var p = j;
                var s = port.speed;
                
                if (port.targetPort && s > this.data[port.targetNode].ports[port.targetPort].speed) {
                    n = port.targetNode;
                    p = port.targetPort;
                    s = this.data[port.targetNode].ports[port.targetPort].speed;
                }
                
                edges[sourceNode][targetNode] = true;
                edges[targetNode][sourceNode] = true;
                
                graph.addEdge(sourceNode, targetNode, {port: p, node: n, label: "0.00% - 0.00%", "font-size": "14px", fill : "#0E0"});
            }
        }
        
        var ro = w * h / nodesCount;
        if (ro < 50000) {
            this.scale.current = ro / 50000;
        }
        else {
            this.scale.current = 1;
        }
        this.scale.min = this.scale.current;
        
        var sc = this.scale.current;
        
        var layouter = new Graph.Layout.Spring(graph);
        layouter.layout();
        
        var renderer = new Graph.Renderer.Raphael(this.id, graph, w/sc, h/sc);
        renderer.draw();
        
        //loadGetCurrent();
        
        this.jObject.children("svg")
        .css("-webkit-transform", "scale(" + sc + ")")
        .css("-moz-transform", "scale(" + sc + ")")
        .css("-o-transform", "scale(" + sc + ")")
        .css("transform", "scale(" + sc + ")");
        
        this.jObject.children("svg").offset(this.GetOffset(0, 0));
    },
    
    NodeClick: function(id) {
        this.loads.window.Show(id);
    }
}
