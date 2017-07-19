/**
 * 
 */

function sortTable(column, sorttype) {
	var table = null;
	var parent = column.parentNode;
	while ( table == null && parent != null ) {
		if ( parent.nodeName === "TABLE" ) {
			table = parent;
		} else {
			parent = parent.parentNode;
		}
	}
	
	if ( table != null ) {
		var colindex = column.cellIndex;
		var headers = column.parentNode.cells;
		for ( var i = 0; i < headers.length; i++ ) {
			if ( i != colindex ) {
				var classname = headers[i].className;
				if ( classname === "sortdesc" || classname === "sortasc" )
					headers[i].className = "sortable";
			}
		}
		var body = table.tBodies[0];
		var rows = Array.prototype.slice.call(body.rows);
		var direction = ( column.className === "sortasc" ? -1 : 1);
		column.className = ( direction === -1 ? "sortdesc" : "sortasc" );
		if ( sorttype === "number" ) {
			rows.sort(function (a,b) { 
				var avalue = getCellValue(a.cells[colindex]);
				var bvalue = getCellValue(b.cells[colindex]);
				var anumber = parseFloat(avalue);
				var bnumber = parseFloat(bvalue);
				if ( isNaN(anumber) ) anumber = 0;
				if ( isNaN(bnumber) ) bnumber = 0;
				var diff = anumber - bnumber;
				return direction * ( diff === 0 ? avalue.localeCompare(bvalue) : diff ); });
		} else if ( sorttype === "date" ) {
			rows.sort(function (a,b) { 
				var avalue = new Date(getCellValue(a.cells[colindex]));
				var bvalue = new Date(getCellValue(b.cells[colindex]));
				return 	direction * ( avalue.getTime() - bvalue.getTime()) ; });					
		} else {
			rows.sort(function (a,b) { 
				var avalue = getCellValue(a.cells[colindex]);
				var bvalue = getCellValue(b.cells[colindex]);
				return direction * ( avalue.localeCompare(bvalue)) ; });					
		}
		body.innerHTML = "";
		for ( var i = 0; i < rows.length; i++ ) {
			body.appendChild(rows[i]);
		}
	}
}

function getCellValue(cell) {
	var child = cell.childNodes[0];
	if ( child === undefined ) {
		return null;
	}
	if ( child.nodeName === "A" ) {
		return child.innerHTML;
	} else {
		return cell.innerHTML;
	}
}
