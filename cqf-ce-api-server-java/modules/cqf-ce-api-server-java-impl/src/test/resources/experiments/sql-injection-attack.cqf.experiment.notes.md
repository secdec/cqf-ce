# SQL injection attack scenario (experiment)

## parameter_bindings

Values for the following parameter bindings will be injected automatically by the archetype:
- attacker_host
- attackee_host
- attackee_port
- attackee_database_host
- attackee_database_port

The default values for each of the host parameters are: 
"attacker", "attackee", and "attackee_database", respectively. 

There is no default value for attackee_port;
it depends on the particular attackee (viz. dotCMS), which see.

There is no default value for attackee_database_port;
it depends on the particular attackee database (viz. MySQL), which see.

On each testbed machine in the attack scenario, an entry in /etc/hosts will exist
corresponding to the value of each host parameter above. 

## children[object_role="attacker"]

### parameter_bindings[name="attack_config_file_as_blob"]

This parameter holds the contents of the attack configuration file.
The archetype arranges to copy it in decoded form to a file on the testbed machine.
The pathname of the destination file is specified by another parameter:
attack_config_file_destination_pathname.

Open question(s):
- What is the default value for attack_config_file_destination_pathname?

Sample value for ESM 7 (before encoding):
```
	loginURL='http://${cqf.parameter.attackee_host}:${cqf.parameter.attackee_port}/admin'
	
	sqliURL='http://${cqf.parameter.attackee_host}:${cqf.parameter.attackee_port}/JSONTags?start=0&count=10&sort=tagname'
	
	sqliParam='sort'
```

## children[object_role="attackee"]

### parameter_bindings[name="database_connection_url"]

This parameter specifies the database host and port, and also the login credentials.

There is no default value for database_connection_url; it depends on the particular attackee (viz. dotCMS), which see.

Sample value for MySQL (before encoding):
```
	jdbc:mysql://address=(protocol=tcp)(host=${cqf.parameter.attackee_database_host})(port=3306)(user=me)(password=foo)/db
```
## children[object_role="attackee"]

### parameter_bindings[name="schema_file_as_blob"]

This parameter holds the contents of the attackee database schema file.
The archetype arranges to copy it in decoded form to a file on the testbed machine.
The pathname of the destination file is specified by another parameter:
schema_file_destination_pathname.

Open question(s):
- What is the default value for schema_file_destination_pathname?
