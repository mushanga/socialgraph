git pull origin master
mvn clean install
service tomcat6 stop
rm -rf /var/lib/tomcat6/webapps/debatree
cp /root/debatree/target/debatree-1.0-SNAPSHOT.war /var/lib/tomcat6/webapps/socialgraph.war
service tomcat6 start