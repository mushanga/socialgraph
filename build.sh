git pull
mvn clean install
service tomcat6 stop
<<<<<<< HEAD
rm -rf /var/lib/tomcat6/webapps/debatree
=======
rm -rf /var/lib/tomcat6/webapps/tcommerce
>>>>>>> 5d45ee488312868890927b23e091a7b5a389434a
cp /root/debatree/target/debatree-1.0-SNAPSHOT.war /var/lib/tomcat6/webapps/socialgraph.war
service tomcat6 start