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
	<br> dataValidator (used by metadataformat) currently as
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
	<br>
	<b>Metadata</b> and
	<b>RecordType</b>
	<br>
	<h2>Use these tools</h2>
	Use the firefox addon RESTClient to play with this...
	<br> and the site: <a href="http://www.jsoneditoronline.org/">http://www.jsoneditoronline.org/</a> might also
	help
	<br>


	<h1>Examples</h1>

	Examples can be found in our <a href="http://epc.ub.uu.se/fitnesse/TheRestTests">acceptance tests</a>
	<h3>Create</h3>
	To create data, use: <br> POST http://epc.ub.uu.se/cora/rest/record/theTypeYouWantToCreate
	<br>
	<br>
	Examples of what the body should look like can be found here: <br>
	<a href="http://epc.ub.uu.se/fitnesse/TheRestTests.CallThroughJavaCode.RecordTypeTests.AbstractRecordType">AbstractRecordType</a>
	<br>
	<h3>Read</h3>
	To read a list of types, use: <br> GET http://epc.ub.uu.se/cora/rest/record/theTypeYouWantToRead
	<br>
	<br>
	To read an instance of a type, use: <br> GET http://epc.ub.uu.se/cora/rest/record/theTypeYouWantToRead/theIdOfTheInstanceYouWantToRead
	<br>
	<h3>Update</h3>
	To update data use: <br> POST http://epc.ub.uu.se/cora/rest/record/theTypeYouWantToUpdate/theIdOfTheDataYouWantToUpdate
	<br>
	<br>
	Examples of what the body should look like can be found here: <br>
	<a href="http://epc.ub.uu.se/fitnesse/TheRestTests.CallThroughJavaCode.RecordTypeTests.AbstractRecordType">AbstractRecordType</a>
	<br>
	<h3>Delete</h3>
	To delete data use: <br> DELETE http://epc.ub.uu.se/cora/rest/record/theTypeYouWantToUpdate/theIdOfTheDataYouWantToDelete
	<br>
	<br>
	<br>

</body>
</html>
