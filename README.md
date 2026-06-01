# Vulnerapp

-- A Vulnerable Sample Spring Boot Application

This application uses a relatively modern stack but is still vulnerable to a set of attacks.
Featuring:

- [XSS](https://portswigger.net/web-security/cross-site-scripting)
- [SQLi](https://portswigger.net/web-security/sql-injection)
- [CSRF](https://portswigger.net/web-security/csrf)
- [SSRF](https://portswigger.net/web-security/ssrf)
- Fake Logins
- Info Exposure
- Plain Passwords
- ...

Either start it via IDE or start it with the following command (it will hang). Then visit http://localhost:8080/

```console
./gradlew bootRun
```
# Mitigationen
- REST Endpunkte -> GET Darf NUR lesen, POST -> Create (Etwas neues), DELETE -> löschen
- Session Based Auth -> sessionStorage wurde durch Formlogin mit einen JSSESSIONID und UserDetailService ersetzt. 
- RBAC + MethodSecurity -> Roles sind jetzt im einem Enum gespeichert was jetzt erlaubt @EnableMethodSecurity und Preauthorize -> Jetzt entweder im Controllers (RBAC) oder beim Services (MBAC brauchbar)
- CSRF -> browser jetzt schicken einen nonce der er von Back-End bekommen hat und er ist durch same-origin gespeichert und im headers mitgeschickt (X-XSRF-TOKEN) wenn das nicht übereinstimmt -> 403
- Nur Passwort Hashes gespeichert (keine Passwörter) und mit Passwordencoder gesichert
- Validation -> DTOs und Records -> Requests sind jetzt validiert und mapped 
- SQLI craziness behoben (kein RohSQL injection mehr)
- XSS -> innerHtml durch textContent ersetzt (innerHTML parset ALLES)
- SSRF -> bitte API health NIE im PROD preisgeben, Danke. 
- Tests sind KI Generiert (sorry)

Selbstreflexion 
- Zeitmanagement absolut schlecht
- ich bin viel mehr bereit gewesen für einen JSON Based REST API, das ganzem Form parsing hat mir ein bisschen mehr Zeit gebraucht das zum verstehen 
- Die rest konnte ich sehr schnell checken weil ich schon alles beim https://rebit.ch implementieren musste. Danke für die neue "Kulissen"

Freundliche Grüsse
Erik