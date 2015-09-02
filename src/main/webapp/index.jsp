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

<h2>Use this information to create a new recordType(place)</h2>
http://localhost:8080/delivery/rest/record/recordType
Content-Type application/uub+record+json
<br>
{"recordType":{"children":[{"recordInfo":{"children":[{"id":"place"}]}},{"metadataId":"place"},{"presentationViewId":"pgPlaceView"},{"presentationFormId":"pgPlaceForm"},{"newMetadataId":"placeNew"},{"newPresentationFormId":"pgPlaceFormNew"},{"listPresentationViewId":"pgPlaceList"},{"searchMetadataId":"placeSearch"},{"searchPresentationFormId":"pgPlaceSearchForm"},{"userSuppliedId":"true"},{"selfPresentationViewId":"metadataSelfPresentation"},{"permissionKey":"RECORDTYPE_PLACE"}]}}

<h2>Use this information for update a recordType(place)</h2>
http://localhost:8080/delivery/rest/record/recordType/place
Content-Type application/uub+record+json
<br>
{"recordType":{"children":[{"recordInfo":{"children":[{"id":"place"},{"type":"recordType"},{"createdBy":"userId"}]}},{"metadataId":"place"},{"presentationViewId":"pgPlaceView"},{"presentationFormId":"pgPlaceForm"},{"newMetadataId":"placeNew"},{"newPresentationFormId":"pgPlaceFormNew"},{"listPresentationViewId":"pgPlaceList"},{"searchMetadataId":"placeSearch"},{"searchPresentationFormId":"pgPlaceSearchForm"},{"userSuppliedId":"true"},{"selfPresentationViewId":"metadataSelfPresentation"},{"permissionKey":"RECORDTYPE_PLACE"}]}}

<h2>Use this information to create a new metadata</h2>
http://localhost:8080/delivery/rest/record/metadata
<br>
Content-Type application/uub+record+json
<br>
{"metadata":{"attributes":{"type":"group"},"children":[{"recordInfo":{"children":[{"id":"authority"}]}},{"dataId":"authority"},{"textId":"authorityTextId"},{"defTextId":"authorityDefTextId"},{"attributeReferences":{"children":[{"ref":"authorityType"}]}},{"childReferences":{"children":[{"childReference":{"children":[{"ref":"autocomplete"},{"repeatMin":"1"},{"repeatMax":"1"}]}},{"childReference":{"children":[{"ref":"existenceDatePeriod"},{"repeatMin":"1"},{"repeatMax":"1"}]}},{"childReference":{"children":[{"ref":"name"},{"repeatMin":"1"},{"repeatMax":"X"}]}},{"childReference":{"children":[{"ref":"other"},{"repeatMin":"1"},{"repeatMax":"3"}]}},{"childReference":{"children":[{"ref":"otherCollection"},{"repeatMin":"1"},{"repeatMax":"3"}]}}]}}]}}


<h2>Use this information for update</h2>
Content-Type application/uub+record+json
<br>
{"name":"authority","children":[{"name":"recordInfo","children":[{"id":"place:193749367321419"},{"type":"place"},{"createdBy":"userId"}]},{"name":"datePeriod","attributes":{"eventType":"existence"},"children":[{"name":"date","attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]},{"name":"date","attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]},{"description":"76 - 76"}]},{"name":"name","attributes":{"type":"person","nameform":"authorized"},"children":[{"name":"namepart","attributes":{"type":"givenname"},"children":[{"name":"Olov"}]},{"name":"namepart","attributes":{"type":"familyname"},"children":[{"name":"McKie"}]},{"name":"namepart","attributes":{"type":"number"},"children":[{"name":"II"}]},{"name":"namepart","attributes":{"type":"addition"},"children":[{"name":"Ett tillägg"}]},{"name":"datePeriod","attributes":{"eventType":"valid"},"children":[{"name":"date","attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]},{"description":"Namn som gift"}]}]},{"name":"name","attributes":{"type":"person","nameform":"alternative"},"children":[{"name":"namepart","attributes":{"type":"givenname"},"children":[{"name":"Olle"}]},{"name":"namepart","attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}]},{"name":"name","attributes":{"type":"person","nameform":"alternative"},"children":[{"name":"namepart","attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]},{"name":"namepart","attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}]},{"other":"some other stuff"},{"other":"second other stuff"},{"other":"third other stuff"},{"othercol":"yes"}],"attributes":{"type":"place"}}

<h2>Use this information for create</h2>
Content-Type application/uub+record+json
<br>
{"name":"authority","children":[{"name":"datePeriod","attributes":{"eventType":"existence"},"children":[{"name":"date","attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]},{"name":"date","attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]},{"description":"76 - 76"}]},{"name":"name","attributes":{"type":"person","nameform":"authorized"},"children":[{"name":"namepart","attributes":{"type":"givenname"},"children":[{"name":"Olov"}]},{"name":"namepart","attributes":{"type":"familyname"},"children":[{"name":"McKie"}]},{"name":"namepart","attributes":{"type":"number"},"children":[{"name":"II"}]},{"name":"namepart","attributes":{"type":"addition"},"children":[{"name":"Ett till�gg"}]},{"name":"datePeriod","attributes":{"eventType":"valid"},"children":[{"name":"date","attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]},{"description":"Namn som gift"}]}]},{"name":"name","attributes":{"type":"person","nameform":"alternative"},"children":[{"name":"namepart","attributes":{"type":"givenname"},"children":[{"name":"Olle"}]},{"name":"namepart","attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}]},{"name":"name","attributes":{"type":"person","nameform":"alternative"},"children":[{"name":"namepart","attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]},{"name":"namepart","attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}]},{"other":"some other stuff"},{"other":"second other stuff"},{"other":"third other stuff"},{"othercol":"yes"}],"attributes":{"type":"place"}}
<h2>Also try to delete, see links in data...</h2>
<pre>
{
  "recordType" : {
    "children" : [
      {
	"id" : "place"
      },
      {
	"metadataId" : "metadata:place"
      },
      {
	"presentationViewId" : "presentation:pgPlaceView"
      },
      {
	"presentationFormId" : "pgPlaceForm"
      },
      {
	"newMetadataId" : "placeNew"
      },
      {
	"newPresentationFormId" : "pgPlaceFormNew"
      },
      {
	"listPresentationViewId" : "pgPlaceList"
      },
      {
	"searchMetadataId" : "placeSearch"
      },
      {
	"searchPresentationViewId" : "pgPlaceSearchForm"
      },
      {
	"userSuppliedId" : "false"
      }
    ]
  }
}
metadata
presentation
text
recordType


{
  "recordList":{
    "totalNo":"2",
    "fromNo" : "0",
    "toNo" : "2",
    "containRecordOfType": "metadata",
    "records":[
      {
      "record": {
	  "data": {
	      "metadata": {
		"attributes" : {
		  "type": "group"
		},
		"children" : [
		  {
		    "recordInfo": {
		      "children": [
			{
			  "id": "metadataGroup:authority"
			},
			{
			  "type": "group"
			},
			{
			  "createdBy": "userId"
			}
		      ]
		    }
		  },
		  {"dataId":"authority"},
		  {"explanation": "Authority group"},
		  {
		    "attributeRefrence": {
		      "children" : [
			{"ref": "authorityType"}
		      ]
		    }
		  }, 
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "autocomplete"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMax": "1"
			}
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "existenceDatePeriod"
			},
			{  
			  "repeatMin": "0"
			},
			{  
			  "repeatMax": "1"
			}
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "name"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMinKey": "AUTHORITY_NAME_REPEAT_MIN"
			},
			{  
			  "repeatMax": "X"
			},
			{  
			  "secret": "true"
			},
			{  
			  "secretKey": "AUTHORITY_NAME_SECRECT"
			},
			{  
			  "readOnly": "true"
			},
			{  
			  "readOnlyKey": "AUTHORITY_NAME_READ_ONLY"
			}
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "other"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMax": "3"
			}
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "otherCollection"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMax": "3"
			}
		      ]
		    }
		  }			
		]
	      }
	  },
	  "keys" : [
	    "KEY1", "KEY2"
	  ]
	}
      },
      {
      "record": {
	  "data": {
	      "metadata": {
		"attributes" : {
		  "type": "group"
		},
		"children" : [
		  {
		    "recordInfo": {
		      "children": [
			{
			  "id": "metadataGroup:authority"
			},
			{
			  "type": "group"
			},
			{
			  "createdBy": "userId"
			}
		      ]
		    }
		  },
		  {"dataId":"authority"},
		  {"explanation": "Authority group"},
		  {
		    "attributeRefrence": {
		      "children" : [
			{"ref": "authorityType"}
		      ]
		    }
		  }, 
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "autocomplete"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMax": "1"
			}
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "existenceDatePeriod"
			},
			{  
			  "repeatMin": "0"
			},
			{  
			  "repeatMax": "1"
			}
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "name"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMinKey": "AUTHORITY_NAME_REPEAT_MIN"
			},
			{  
			  "repeatMax": "X"
			},
			{  
			  "secret": "true"
			},
			{  
			  "secretKey": "AUTHORITY_NAME_SECRECT"
			},
			{  
			  "readOnly": "true"
			},
			{  
			  "readOnlyKey": "AUTHORITY_NAME_READ_ONLY"
			},
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "other"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMax": "3"
			}
		      ]
		    }
		  },
		  {
		    "childrenReference":{
		      "children": [
			{		  
			  "ref": "otherCollection"
			},
			{  
			  "repeatMin": "1"
			},
			{  
			  "repeatMax": "3"
			}
		      ]
		    }
		  },			
		]
	      }
	  },
	  "keys" : [
	    "KEY1", "KEY2"
	  ]
	}
      }
    ]
  }
}
{
  "recordList":{
    "totalNo":"2",
    "fromNo" : "0",
    "toNo" : "2",
    "containRecordOfType": "metadata",
    "records":[
      {
      "record": {
	  "data": {
	      "groupDataId": {
		}			
		
	      }
	  }
	} 
     ]  
  }
}
</pre>
</body>
</html>
