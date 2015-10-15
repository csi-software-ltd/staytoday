<html>
  <head>
    <title>${infotext['title'+context?.lang]?:''}</title>
    <meta name="layout" content="print" />
  </head>
  <body onload="window.print()">
            <tr>
              <td width="980" colspan="2" style="background:#fff;padding:10px">    
                <g:rawHtml>${text?:''}</g:rawHtml>    
              </td>
            </tr>
  </body>
</html>
