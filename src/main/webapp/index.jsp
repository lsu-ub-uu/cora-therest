<html>
<body>
	<h1>Hello Rest of The World!</h1>
	When "the rest" starts up, the
	<b>SystemInitializer</b> class is started through a @WebListener. It
	will create a new
	<b>SystemOneDependencyProvider</b> (implementing
	SpiderDependencyProvider).
	<b>SystemOneDependencyProvider</b> initializes the needed dependencies
	for the system:
	<br> recordStorage (used by spider) currently as
	<b>RecordStorageInMemory</b>
	<i>(this is the same class that also provides metadataStorage)</i>
	<br> idGenerator (used by spider) currently as
	<b>TimeStampIdGenerator</b>
	<br> keyCalculator (used by spider) currently as
	<b>RecordPermissionKeyCalculator</b>
	<br> metadataStorage (used by metadataformat) currently as
	<b>RecordStorageInMemory</b>
	<i>(this is the same class that also provides recordStorage)</i>
	<br> dataValidatorImp (used by metadataformat) currently as
	<b>DataValidatorImp</b>
	<br> authorizator (used by beefeater) currently as
	<b>AuthorizatorImp</b>
	<br>
	<br>
	<b>SystemOneDependencyProvider</b> also adds to storage, the basic
	metadata that is needed for the system to work. The needed metadata is:
	<br>Metadata groups including all subgroups and variables for,
	<b>metadataNew</b>,
	<b>recordType</b> and
	<b>RecordTypeNew</b>
	<br>
	<br>
	<b>SystemOneDependencyProvider</b> also adds to storage, the basic
	recordtypes that is needed for the system to work. The needed
	recordTypes are:
	<br><b>Metadata</b> and
	<b>RecordType</b>
	<br>
	<h2>Use these tools</h2>
	Use the firefox addon RESTClient to play with this...
	<br> and the site: http://www.jsoneditoronline.org/ might also
	help
	<br>
	http://130.238.171.39:8080/therest/rest/record/place/place:XXX
	<br>

	<h2>Use this information to create a new metadata(generatedIdNew)</h2>
	http://localhost:8080/delivery/rest/record/metadata
	<br> Content-Type application/uub+record+json
	<br>{"metadata":{"attributes":{"type":"group"},"children":[{"recordInfo":{"children":[{"id":"generatedIdNew"}]}},{"dataId":"generatedId"},{"textId":"generatedIdTextId"},{"defTextId":"generatedIdDefTextId"},{"childReferences":{"children":[{"childReference":{"children":[{"ref":"id"},{"repeatMin":"1"},{"repeatMax":"1"}]}}]}}]}}

	<h2>Use this information to create a new metadata(generatedId)</h2>
	http://localhost:8080/delivery/rest/record/metadata
	<br> Content-Type application/uub+record+json
	<br>{"metadata":{"attributes":{"type":"group"},"children":[{"recordInfo":{"children":[{"id":"generatedId"}]}},{"dataId":"generatedId"},{"textId":"generatedIdTextId"},{"defTextId":"generatedIdDefTextId"},{"childReferences":{"children":[{"childReference":{"children":[{"ref":"recordInfo"},{"repeatMin":"1"},{"repeatMax":"1"}]}},{"childReference":{"children":[{"ref":"id"},{"repeatMin":"1"},{"repeatMax":"1"}]}}]}}]}}


	<h2>Use this information to create a new recordType(testdatatype)</h2>
	http://localhost:8080/delivery/rest/record/recordType
	<br> Content-Type application/uub+record+json
	<br>
	{"recordType":{"children":[{"recordInfo":{"children":[{"id":"testdatatype"}]}},{"metadataId":"testdatatype"},{"presentationViewId":"pgTestDataTypeView"},{"presentationFormId":"pgTestDataTypeForm"},{"newMetadataId":"testdatatypeNew"},{"newPresentationFormId":"pgTestDataTypeFormNew"},{"listPresentationViewId":"pgTestDataTypeList"},{"searchMetadataId":"testdatatypeSearch"},{"searchPresentationFormId":"pgTestDataTypeSearchForm"},{"userSuppliedId":"false"},{"selfPresentationViewId":"testdatatypeSelfPresentation"},{"permissionKey":"RECORDTYPE_TESTDATATYPE"}]}}

	<h2>Use this information to create a new data(testdatatype)</h2>
	http://localhost:8080/delivery/rest/record/testdatatype
	<br> Content-Type application/uub+record+json
	<br>
	{"testdatatype":{"children":[{"recordInfo":{"children":[{"id":"testdatatype"}]}},{"id":"A
	new id"}]}}

	<br> -----------------------
	<h2>Use this information to create a new metadata(suppliedIdNew)</h2>
	http://localhost:8080/delivery/rest/record/metadata
	<br> Content-Type application/uub+record+json
	<br>{"metadata":{"attributes":{"type":"group"},"children":[{"recordInfo":{"children":[{"id":"suppliedIdNew"}]}},{"dataId":"suppliedId"},{"textId":"suppliedIdNewTextId"},{"defTextId":"suppliedIdNewTextId"},{"childReferences":{"children":[{"childReference":{"children":[{"ref":"recordInfoNew"},{"repeatMin":"1"},{"repeatMax":"1"}]}},{"childReference":{"children":[{"ref":"id"},{"repeatMin":"1"},{"repeatMax":"1"}]}}]}}]}}

	<h2>Use this information to create a new metadata(suppliedId)</h2>
	http://localhost:8080/delivery/rest/record/metadata
	<br> Content-Type application/uub+record+json
	<br>{"metadata":{"attributes":{"type":"group"},"children":[{"recordInfo":{"children":[{"id":"suppliedId"}]}},{"dataId":"suppliedId"},{"textId":"suppliedIdTextId"},{"defTextId":"suppliedIdTextId"},{"childReferences":{"children":[{"childReference":{"children":[{"ref":"recordInfo"},{"repeatMin":"1"},{"repeatMax":"1"}]}},{"childReference":{"children":[{"ref":"id"},{"repeatMin":"1"},{"repeatMax":"1"}]}}]}}]}}


	<h2>Use this information to create a new recordType(suppliedId)</h2>
	http://localhost:8080/delivery/rest/record/recordType
	<br> Content-Type application/uub+record+json
	<br>
	{"recordType":{"children":[{"recordInfo":{"children":[{"id":"suppliedId"}]}},{"metadataId":"suppliedId"},{"presentationViewId":"pgSuppliedIdView"},{"presentationFormId":"pgSuppliedIdForm"},{"newMetadataId":"suppliedIdNew"},{"newPresentationFormId":"pgSuppliedIdFormNew"},{"listPresentationViewId":"pgSuppliedIdList"},{"searchMetadataId":"suppliedIdSearch"},{"searchPresentationFormId":"pgSuppliedIdSearchForm"},{"userSuppliedId":"true"},{"selfPresentationViewId":"suppliedIdSelfPresentation"},{"permissionKey":"RECORDTYPE_SUPPLIEDID"}]}}

	<h2>Use this information to create a new data(suppliedId)</h2>
	http://localhost:8080/delivery/rest/record/suppliedId
	<br> Content-Type application/uub+record+json
	<br>{"suppliedId":{"children":[{"recordInfo":{"children":[{"id":"ANID"}]}},{"id":"ABC"}]}} 

	<h2>Use this information to update data(suppliedId/ANID)</h2>
	http://localhost:8080/delivery/rest/record/suppliedId/ANID
	<br> Content-Type application/uub+record+json
	<br>{"suppliedId":{"children":[{"recordInfo":{"children":[{"id":"ANID"},{"type":"suppliedId"},{"createdBy":"userId"}]}},{"id":"ABC2222222"}]}} 

	<br> -----------------------

	<h2>Use this information to create a new recordType(place)</h2>
	http://localhost:8080/delivery/rest/record/recordType
	<br> Content-Type application/uub+record+json
	<br>
	{"recordType":{"children":[{"recordInfo":{"children":[{"id":"place"}]}},{"metadataId":"place"},{"presentationViewId":"pgPlaceView"},{"presentationFormId":"pgPlaceForm"},{"newMetadataId":"placeNew"},{"newPresentationFormId":"pgPlaceFormNew"},{"listPresentationViewId":"pgPlaceList"},{"searchMetadataId":"placeSearch"},{"searchPresentationFormId":"pgPlaceSearchForm"},{"userSuppliedId":"true"},{"selfPresentationViewId":"metadataSelfPresentation"},{"permissionKey":"RECORDTYPE_PLACE"}]}}

	<h2>Use this information for update a recordType(place)</h2>
	http://localhost:8080/delivery/rest/record/recordType/place
	<br> Content-Type application/uub+record+json
	<br>
	{"recordType":{"children":[{"recordInfo":{"children":[{"id":"place"},{"type":"recordType"},{"createdBy":"userId"}]}},{"metadataId":"place"},{"presentationViewId":"pgPlaceView"},{"presentationFormId":"pgPlaceForm"},{"newMetadataId":"placeNew"},{"newPresentationFormId":"pgPlaceFormNew"},{"listPresentationViewId":"pgPlaceList"},{"searchMetadataId":"placeSearch"},{"searchPresentationFormId":"pgPlaceSearchForm"},{"userSuppliedId":"true"},{"selfPresentationViewId":"metadataSelfPresentation"},{"permissionKey":"RECORDTYPE_PLACE"}]}}

	<h2>Use this information to create a new metadata</h2>
	http://localhost:8080/delivery/rest/record/metadata
	<br> Content-Type application/uub+record+json
	<br>
	{"metadata":{"attributes":{"type":"group"},"children":[{"recordInfo":{"children":[{"id":"authority"}]}},{"dataId":"authority"},{"textId":"authorityTextId"},{"defTextId":"authorityDefTextId"},{"attributeReferences":{"children":[{"ref":"authorityType"}]}},{"childReferences":{"children":[{"childReference":{"children":[{"ref":"autocomplete"},{"repeatMin":"1"},{"repeatMax":"1"}]}},{"childReference":{"children":[{"ref":"existenceDatePeriod"},{"repeatMin":"1"},{"repeatMax":"1"}]}},{"childReference":{"children":[{"ref":"name"},{"repeatMin":"1"},{"repeatMax":"X"}]}},{"childReference":{"children":[{"ref":"other"},{"repeatMin":"1"},{"repeatMax":"3"}]}},{"childReference":{"children":[{"ref":"otherCollection"},{"repeatMin":"1"},{"repeatMax":"3"}]}}]}}]}}


	<h2>Use this information for update</h2>
	Content-Type application/uub+record+json
	<br>
	{"authority":{"children":[{"recordInfo":{"children":[{"id":"place:193749367321419"},{"type":"place"},{"createdBy":"userId"}]}},{"datePeriod":{"attributes":{"eventType":"existence"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]}},{"date":{"attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]}},{"description":"76
	-
	76"}]}},{"name":{"attributes":{"type":"person","nameform":"authorized"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olov"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"McKie"}]}},{"namepart":{"attributes":{"type":"number"},"children":[{"name":"II"}]}},{"namepart":{"attributes":{"type":"addition"},"children":[{"name":"Ett
	tillägg"}]}},{"datePeriod":{"attributes":{"eventType":"valid"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]}},{"description":"Namn
	som
	gift"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}}]}},{"other":"some
	other stuff"},{"other":"second other stuff"},{"other":"third other
	stuff"},{"othercol":"yes"}],"attributes":{"type":"place"}}}

	<h2>Use this information for create</h2>
	Content-Type application/uub+record+json
	<br>
	{"authority":{"children":[{"datePeriod":{"attributes":{"eventType":"existence"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"1976"},{"month":"07"},{"day":"22"}]}},{"date":{"attributes":{"datePointEventType":"end"},"children":[{"year":"2076"},{"month":"12"},{"day":"31"}]}},{"description":"76
	-
	76"}]}},{"name":{"attributes":{"type":"person","nameform":"authorized"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olov"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"McKie"}]}},{"namepart":{"attributes":{"type":"number"},"children":[{"name":"II"}]}},{"namepart":{"attributes":{"type":"addition"},"children":[{"name":"Ett
	tillägg"}]}},{"datePeriod":{"attributes":{"eventType":"valid"},"children":[{"date":{"attributes":{"datePointEventType":"start"},"children":[{"year":"2008"},{"month":"06"},{"day":"28"}]}},{"description":"Namn
	som
	gift"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson"}]}}]}},{"name":{"attributes":{"type":"person","nameform":"alternative"},"children":[{"namepart":{"attributes":{"type":"givenname"},"children":[{"name":"Olle2"}]}},{"namepart":{"attributes":{"type":"familyname"},"children":[{"name":"Nilsson2"}]}}]}},{"other":"some
	other stuff"},{"other":"second other stuff"},{"other":"third other
	stuff"},{"othercol":"yes"}],"attributes":{"type":"place"}}}
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
