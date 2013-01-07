git pull
mvn clean install
service tomcat6 stop
rm -rf /var/lib/tomcat6/webapps/tcommerce
cp /root/tcommerce/target/gujum-1.0-SNAPSHOT.war /var/lib/tomcat6/webapps/tcommerce.war
service tomcat6 start