rem Compila los 4 proyectos (sin ejecutar pruebas) para luego crear las imagenes Docker
cd api-gateway
call .\mvnw clean package -DskipTests -s ..\configuracion-maven\settings.xml

cd ../ms-admin
call .\mvnw clean package -DskipTests -s ..\configuracion-maven\settings.xml

cd ../ms-reservas
call .\mvnw clean package -DskipTests -s ..\configuracion-maven\settings.xml

cd ../ms-finanzas
call .\mvnw clean package -DskipTests -s ..\configuracion-maven\settings.xml

cd ..
