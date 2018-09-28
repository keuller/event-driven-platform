## User Module

This module is demonstrate how to implement a 'Microservice' into platform and simulates some operations.

### Authentication

To authenticate an user, you must make a HTTP request using **POST** method with this payload.

```json
{
	"type": "security",
	"command": "auth",
	"version": 1,
	"payload": {
		"username": "eduardo",
		"password": "12345"
	}
}
```

If the user exists in memory you must receive a reply like this:

```json
{
	"username": "eduardo",
	"role": "manager",
	"email": "edu.magalhaes@gmail.com"
}
```

### Finding All Users

To find all users registered, you must make an HTTP request using **POST** method with this payload.

```json
{
	"type": "security",
	"version": 1,
	"command": "findAll",
	"payload": {}
}
```

### Registering an User

To register a new user, you must make an HTTP request using **POST** method with this payload.

```json
{
	"type": "security",
	"command": "create",
	"version": 1,
	"payload": {
		"username": "giovanna",
		"password": "123456",
		"role": "admin",
		"email": "gio.magalhaes@gmail.com"
	}
}
```

You must get a reply like this:

```json
{
	"message": "User has been created."
}
```
