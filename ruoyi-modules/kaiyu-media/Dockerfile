FROM jrottenberg/ffmpeg:centos
LABEL maintainer=xiaojuzi

# 安装 OpenJDK 8
RUN yum install -y java-1.8.0-openjdk

# 设置 JAVA_HOME 环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

#docker run -e PARAMS="--server.port 9090"
ENV PARAMS="--server.port=8080 --spring.profiles.active=prod --spring.cloud.nacos.discovery.server-addr=121.89.198.78:30230 --spring.cloud.nacos.config.server-addr=121.89.198.78:30230 --spring.cloud.nacos.config.namespace=prod --spring.cloud.nacos.config.file-extension=yml"
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY target/*.jar /app.jar
EXPOSE 8080

#
ENTRYPOINT ["/bin/sh","-c","java -Dfile.encoding=utf8 -Djava.security.egd=file:/dev/./urandom -jar app.jar ${PARAMS}"]