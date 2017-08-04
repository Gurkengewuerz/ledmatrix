/* jshint browser: true */
/* globals d3, RXB, alert */
// defaults

// https://github.com/bahamas10/ryb

var borderwidth = 10;
var bordercolor = 0;
var bordercolors = [
    '#777',
    '#444'
];
var brightness = 0;
var divisions = 128;
var divisionvariance = 1;
var isinterpolated = false;
var lastmasktype = '';
var margin = borderwidth + 10;
var maskcolor = '#333';
var maskrotation = 0;
var maskspread = 0;
var mousedown = false;
var radius = 400;
var rings = 28;
var ringvariance = 1;
var rotation = 0;
var strokecolor = '#333';
var strokewidth = 0;

// svg element
var svg;

// d3 elements
var last_element_clicked;

// page loaded
window.addEventListener('load', init);

function init() {
    // links should open in new tabs
    d3.selectAll('a').attr('target', '_blank');

    // lifting the mouseup anywhere should affect everything
    document.onmouseup = function () {
        mousedown = false;
    };

    // make the color wheel
    create();
}

// destroy and recreate the color wheel
function create() {
    var i;
    try {
        // regenerate the wheel on create
        document.getElementsByTagName('svg')[0].remove();
    } catch (e) {
    }

    // we generate a rainbow using the divisions set, and map that to a colors
    // ryb representation and its corresponding neutrals
    var data = RXB.rainbow(divisions).map(function (ryb) {
        return {ryb: ryb, neutrals: RXB.neutrals(ryb, 0, rings * 2 - 1)};
    });

    // create the SVG
    svg = d3.select('#picker').append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('viewBox', (-margin) + ' ' + (-margin) + ' ' + (radius * 2 + margin * 2) + ' ' + (radius * 2 + margin * 2))
        .append('g')
        .attr('transform', 'translate(' + radius + ',' + radius + ') rotate(' + rotation + ')');

    var defs = svg.append('defs');
    var grad1 = defs.append('radialGradient')
        .attr('id', 'grad1');
    grad1.append('stop')
        .attr('offset', '90%')
        .attr('stop-color', d3.rgb(bordercolors[0]).darker(bordercolor / 10));
    grad1.append('stop')
        .attr('offset', '100%')
        .attr('stop-color', d3.rgb(bordercolors[1]).darker(bordercolor / 10));

    // figure out the arc size
    var arcsizes = [0];
    // generate arc sizes in the form of [0, 1, 2, 1, 2, ...]
    for (i = 0; i < rings; i++)
        arcsizes.push(Math.floor(Math.random() * ringvariance) + 1);

    var arcsizessum = arcsizes.reduce(function (a, b) {
        return a + b;
    });
    var ringunitsize = radius / arcsizessum;

    var arcradius = [radius];
    for (i = 1; i <= rings; i++)
        arcradius[i] = arcradius[i - 1] - (arcsizes[i] * ringunitsize);

    // make the outermost ring first (the outline / border)
    svg.append('circle')
        .attr('cx', 0)
        .attr('cy', 0)
        .attr('r', radius + borderwidth)
        .attr('fill', 'url(#grad1)');

    // create an arc for each ring
    for (i = 0; i < rings; i++) {
        pie = d3.layout.pie()
            .sort(null)
            .value(function (d) {
                return Math.floor(Math.random() * divisionvariance + 1);
            });

        arc = d3.svg.arc()
            .innerRadius(arcradius[i])
            .outerRadius(arcradius[i + 1]);

        svg.selectAll('g')
            .data(pie(data))
            .enter()
            .append('path')
            .attr('d', arc)
            .attr('class', 'color-wedge')
            .attr('stroke', strokecolor)
            .attr('stroke-width', strokewidth + 'px')
            .attr('shape-rendering', strokewidth === 0 ? 'crispEdges' : 'auto')
            .attr('fill', function (d, j) {
                // figure out the background color
                this.style.cursor = 'crosshair';
                var d3this = d3.select(this);
                d3this.attr('ring', i);
                d3this.attr('division', j);
                var ryb = d.data.neutrals[i];
                var color = RXB.stepcolor(ryb, brightness / 255, 255);
                if (isinterpolated)
                    return d3.rgb.apply(d3, RXB.ryb2rgb(color));
                else
                    return d3.rgb.apply(d3, color);
            }).on('click', onclick)
            .on('mousedown', function () {
                mousedown = true;
            }).on('mouseup', function () {
            mousedown = false;
        }).on('mouseover', function (d) {
            if (mousedown) {
                // simulate click
                onclick.call(this);
            }
        }).append('svg:title').text(function () {
            // tooltip
            return this.parentNode.getAttribute('fill');
        });

        function onclick() {
            var d3this = d3.select(this);

            last_element_clicked = d3this;

            var rgb = this.getAttribute('fill');
            var vals = hexToRgb(rgb);

            $.ajax({
                url: "/control/set",
                type: "post",
                data: {
                    red: vals.r,
                    green: vals.g,
                    blue: vals.b
                },
                success: function (response, status) {
                    // alert("Data: " + response + "\nStatus: " + status);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(textStatus, errorThrown);
                }
            });

            // set the color preview and input field with the RGB value
            console.log(rgb);
            console.log(vals.r + " " + vals.g + " " + vals.b);
        }

        function touchStatus() {
            var d3this = d3.touches(this);

            last_element_clicked = d3this;

            var rgb = this.getAttribute('fill');
            var vals = hexToRgb(rgb);

            $.ajax({
                url: "/control/set",
                type: "post",
                data: {
                    red: vals.r,
                    green: vals.g,
                    blue: vals.b
                },
                success: function (response, status) {
                    // alert("Data: " + response + "\nStatus: " + status);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(textStatus, errorThrown);
                }
            });

            // set the color preview and input field with the RGB value
            console.log(rgb);
            console.log(vals.r + " " + vals.g + " " + vals.b);
        }
    }
    apply_mask(lastmasktype);
}

// create a mask
function apply_mask(mask) {
    lastmasktype = mask;

    svg.selectAll('g.mask').remove();

    var data = [];

    // figure out what to do
    switch (mask) {
        case 'monochromatic':
            data = [1, 0, 0, 0, 0, 0];
            break;
        case 'analogous':
            data = [1, 0, 0];
            break;
        case 'complementary':
            data = [1, 0, 0, 1, 0, 0];
            break;
        case 'double-complementary':
            data = [1, 1, 0, 0, 0, 0];
            break;
        case 'split-complementary':
            data = [1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0];
            break;
        case 'tetradic':
            data = [1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0];
            break;
        case 'square':
            data = [1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0];
            break;
        case 'diadic':
            data = [1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0];
            break;
        case 'triadic':
            data = [1, 0, 1, 0, 1, 0];
            break;
        default:
            // "None" mask will fall through
            break;
    }

    var pie = d3.layout.pie()
        .sort(null)
        .value(function (d, i) {
            return 1 + (d * maskspread / 10);
        });

    var arc = d3.svg.arc()
        .innerRadius(0)
        .outerRadius(radius + 1);

    var g = svg
        .append('g')
        .attr('transform', 'rotate(' + maskrotation + ')')
        .attr('class', 'mask');

    g.selectAll('g')
        .data(pie(data))
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('class', 'mask')
        .attr('stroke', maskcolor)
        .on('mouseup', function () {
            mousedown = false;
        })
        .attr('fill', function (d, i) {
            this.style.visibility = d.data ? 'hidden' : 'visible';
            this.style.cursor = d.data ? 'crosshair' : 'auto';
            return maskcolor;
        });
}

function hexToRgb(hex) {
    // Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
    var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
    hex = hex.replace(shorthandRegex, function (m, r, g, b) {
        return r + r + g + g + b + b;
    });

    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}