rem Ejecuta las pruebas unitarias (JUnit 5 + Mockito) de los 3 microservicios
cd ms-admin
call .\mvnw test -s ..\configuracion-maven\settings.xml

cd ../ms-reservas
call .\mvnw test -s ..\configuracion-maven\settings.xml

cd ../ms-finanzas
call .\mvnw test -s ..\configuracion-maven\settings.xml

cd ..
