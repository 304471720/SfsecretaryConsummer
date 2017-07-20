start()
{
nohup  /www/3g.client.soufun.com/ljj/jdk7/bin/java -jar /logs/ljj/SfsecretaryConsummer/target/SfsecretaryConsummer-1.0-SNAPSHOT.jar  >/dev/null 2>&1  &
}

success()
{
ls /logs/ljj/SfsecretaryConsummer/logs  -l --full-time | grep `date +%Y-%m-%d` | awk '{print " cat /logs/ljj/SfsecretaryConsummer/logs/"$9" |grep \"push success\" | grep `date +%Y-%m-%d` | wc -l  "}' | sh | awk '{sum+=$1}END{print sum}'
}

fail()
{
ls /logs/ljj/SfsecretaryConsummer/logs  -l --full-time | grep `date +%Y-%m-%d` | awk '{print " cat /logs/ljj/SfsecretaryConsummer/logs/"$9" |grep \"push fail\" | grep `date +%Y-%m-%d` | wc -l  "}' | sh | awk '{sum+=$1}END{print sum}'
}

receive()
{
ls /logs/ljj/SfsecretaryConsummer/logs  -l --full-time | grep `date +%Y-%m-%d` | awk '{print " cat /logs/ljj/SfsecretaryConsummer/logs/"$9" |grep \"receice token\" | grep `date +%Y-%m-%d` | wc -l  "}' | sh | awk '{sum+=$1}END{print sum}'
}
logs()
{
tail -f /logs/ljj/SfsecretaryConsummer/logs/SfsecretaryConsummer.log
}

stop()
{
ps aux | grep SfsecretaryConsummer | grep -v grep | awk '{print "sudo  kill "$2}' | sh
}

processnum()
{
ps aux | grep  SfsecretaryConsummer| grep -v grep | wc -l
}
build()
{
stop
cd /logs/ljj/SfsecretaryConsummer
export JAVA_HOME=/www/3g.client.soufun.com/ljj/jdk7
cvs up -d -C
mvn clean package
}

case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'logs')
     logs
     ;;
   'success')
     success
     ;;
   'fail')
     fail
     ;;
   'receive')
     receive
     ;;
   'build')
     build
     ;;
  *)
     processnum
     exit 1
esac
exit 0