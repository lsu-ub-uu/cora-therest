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
{"authority":{"children":[{"recordInfo":{"children":[{"id":"place:193749367321419"},{"type":"place"},{"createdBy":"userId"}]}},{"datePeriod":{"attributes":{"eventType":"existence"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]}},{"date":{"attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]}},{"description":"76 - 76"}]}},{"name":{"attributes":{"type":"person","nameform":"authorized"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olov"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"McKie"}]}},{"namepart":{"attributes":{"type":"number"},"children":[{"name":"II"}]}},{"namepart":{"attributes":{"type":"addition"},"children":[{"name":"Ett tillägg"}]}},{"datePeriod":{"attributes":{"eventType":"valid"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]}},{"description":"Namn som gift"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}}]}},{"other":"some other stuff"},{"other":"second other stuff"},{"other":"third other stuff"},{"othercol":"yes"}],"attributes":{"type":"place"}}}
<h2>Use this information for create</h2>
Content-Type application/uub+record+json
<br>
{"authority":{"children":[{"datePeriod":{"attributes":{"eventType":"existence"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]}},{"date":{"attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]}},{"description":"76 - 76"}]}},{"name":{"attributes":{"type":"person","nameform":"authorized"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olov"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"McKie"}]}},{"namepart":{"attributes":{"type":"number"},"children":[{"name":"II"}]}},{"namepart":{"attributes":{"type":"addition"},"children":[{"name":"Ett tillägg"}]}},{"datePeriod":{"attributes":{"eventType":"valid"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]}},{"description":"Namn som gift"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}}]}},{"other":"some other stuff"},{"other":"second other stuff"},{"other":"third other stuff"},{"othercol":"yes"}],"attributes":{"type":"place"}}}
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
