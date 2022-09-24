*start*
1. wget https://apache-mirror.rbc.ru/pub/apache/kafka/2.6.0/kafka_2.12-2.6.0.tgz
2. tar -xvf kafka_2.12-2.6.0.tgz
3. sudo mv kafka_2.12-2.6.0 /usr/local/kafka
4. sudo useradd kafka -m
5. sudo passwd kafka
6. sudo adduser kafka kafka
7. sudo chown -R kafka:kafka /usr/local/kafka
8. sudo mkdir /tmp/kafka-logs
9. sudo chown -R kafka:kafka /tmp/kafka-logs
10. sudo mkdir /tmp/zookeeper
11. sudo chown -R kafka:kafka /tmp/zookeeper
12. sudo vim /etc/systemd/system/zookeeper.service
      [Unit]
      Description=zookeeper
      After=syslog.target network.target

      [Service]
      Type=simple

      User=kafka
      Group=kafka

      ExecStart=/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties
      ExecStop=/opt/kafka/bin/zookeeper-server-stop.sh

      [Install]
      WantedBy=multi-user.target

13. sudo vim /etc/systemd/system/kafka.service
       Unit]
       Description=Apache Kafka
       Requires=zookeeper.service
       After=zookeeper.service

      [Service]
      Type=simple
      User=kafka
      ExecStart=/bin/sh -c '/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties > /tmp/kafka-logs/kafka.log 2>&1'
      ExecStop=/opt/kafka/bin/kafka-server-stop.sh
      Restart=on-abnormal

      [Install]
      WantedBy=multi-user.target

14. sudo systemctl daemon-reload
15. sudo vim /opt/kafka/config/zookeeper.properties
         dataDir=/tmp/zookeeper
         clientPort=2181
         maxClientCnxns=0
16. sudo vim /opt/kafka/config/server.properties
            broker.id=0
            num.network.threads=3
            num.io.threads=8
            socket.send.buffer.bytes=102400
            socket.receive.buffer.bytes=102400
            socket.request.max.bytes=104857600
            log.dirs=/tmp/kafka-logs
            num.partitions=1
            num.recovery.threads.per.data.dir=1
            offsets.topic.replication.factor=1
            transaction.state.log.replication.factor=1
            transaction.state.log.min.isr=1
            log.retention.hours=168
            log.segment.bytes=1073741824
            log.retention.check.interval.ms=300000
            zookeeper.connect=localhost:2181
            zookeeper.connection.timeout.ms=18000
            group.initial.rebalance.delay.ms=0
            delete.topic.enable=true
            advertised.host.name=myhost.com

            listeners=PLAINTEXT://localhost:9092
            advertised.listeners=PLAINTEXT://localhost:9092  

***
1. sudo systemctl start zookeeper.service
2. sudo systemctl start kafka.service
3. sudo systemctl status kafka zookeeper.service
4. option: create topic 
5. check topic
   sudo /usr/local/kafka-server/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic theFirstTopic
   sudo /usr/local/kafka-server/bin/kafka-topics.sh  --topic theFirstTopic --bootstrap-server localhost:9092
6. option: test producing
   sudo /usr/local/kafka-server/bin/kafka-console-producer.sh --topic theFirstTopic --bootstrap-server localhost:9092
7. option test consumer
   sudo /usr/local/kafka-server/bin/kafka-console-consumer.sh --topic theFirstTopic --from-begining --bootstrap-server localhost:9092
8.