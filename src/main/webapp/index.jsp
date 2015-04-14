<html>
<body>
<h1>Hello Rest of The World!</h1>
<h2>Use these tools</h2>
Use the firefox addon RESTClient to play with this...
<br>
and the site: http://www.jsoneditoronline.org/ might also help
<br>
http://130.238.171.39:8080/therest/rest/record/place/place:XXX
<br>
<h2>Use this information for update</h2>
Content-Type application/uub+record+json
<br>

{"authority":{"children":[{"recordInfo":{"children":[{"id":"place:193749367321419"},{"type":"place"},{"createdBy":"userId"}]}},{"datePeriod":{"attributes":{"eventType":"existence"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]}},{"date":{"attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]}},{"description":"76 - 76"}]}},{"name":{"attributes":{"type":"person","nameform":"authorized"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olov"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"McKie"}]}},{"namepart":{"attributes":{"type":"number"},"children":[{"name":"II"}]}},{"namepart":{"attributes":{"type":"addition"},"children":[{"name":"Ett tillägg"}]}},{"datePeriod":{"attributes":{"eventType":"valid"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]}},{"description":"Namn som gift"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}}]}},{"other":"some other stuff"},{"other":"second other stuff"},{"other":"third other stuff"},{"othercol":"yes"}],"actionLinks":{"read":{"requestMethod":"GET","rel":"read","contentType":"application/uub+record+json","url":"http://localhost:8080/therest/rest/record/place/place:193749367321419","accept":"application/uub+record+json"},"update":{"requestMethod":"POST","rel":"update","contentType":"application/uub+record+json","url":"http://localhost:8080/therest/rest/record/place/place:193749367321419","accept":"application/uub+record+json"},"delete":{"requestMethod":"DELETE","rel":"delete","contentType":"application/uub+record+json","url":"http://localhost:8080/therest/rest/record/place/place:193749367321419","accept":"application/uub+record+json"}},"attributes":{"type":"place"}}}
<h2>Use this information for create</h2>
Content-Type application/uub+record+json
<br>
{"authority":{"children":[{"datePeriod":{"attributes":{"eventType":"existence"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]}},{"date":{"attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]}},{"description":"76 - 76"}]}},{"name":{"attributes":{"type":"person","nameform":"authorized"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olov"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"McKie"}]}},{"namepart":{"attributes":{"type":"number"},"children":[{"name":"II"}]}},{"namepart":{"attributes":{"type":"addition"},"children":[{"name":"Ett tillägg"}]}},{"datePeriod":{"attributes":{"eventType":"valid"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]}},{"description":"Namn som gift"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}}]}},{"other":"some other stuff"},{"other":"second other stuff"},{"other":"third other stuff"},{"othercol":"yes"}],"attributes":{"type":"place"}}}
<h2>Also try to delete, see links in data...</h2>
</body>
</html>
