FROM openjdk:8-jdk
LABEL maintainer=xiaojuzi


# 待修改 需要加入ffmpeg 环境


# 添加 Jonathon F 的 PPA 并安装 FFmpeg
RUN echo "deb http://ppa.launchpad.net/jonathonf/ffmpeg-4/ubuntu bionic main" | tee /etc/apt/sources.list.d/ffmpeg.list \
    && apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 2A8E3034D018A4CE B9316A7BC7917B12 \
    && \
    apt-get install -y --no-install-recommends ffmpeg && \
    apt-get clean;

#docker run -e PARAMS="--server.port 9090"
ENV PARAMS="--server.port=8080 --spring.profiles.active=prod --spring.cloud.nacos.discovery.server-addr=121.89.198.78:30230 --spring.cloud.nacos.config.server-addr=121.89.198.78:30230 --spring.cloud.nacos.config.namespace=prod --spring.cloud.nacos.config.file-extension=yml"
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY target/*.jar /app.jar
EXPOSE 8080

#
ENTRYPOINT ["/bin/sh","-c","java -Dfile.encoding=utf8 -Djava.security.egd=file:/dev/./urandom -jar app.jar ${PARAMS}"]