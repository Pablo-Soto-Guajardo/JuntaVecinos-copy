cd api-gateway
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../ms-admin
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../ms-reservas
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ../ms-finanzas
call .\mvnw clean -s ..\configuracion-maven\settings.xml

cd ..
