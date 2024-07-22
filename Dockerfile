FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 安装字体库和其他必要的工具
RUN apk update && \
    apk add --no-cache ttf-dejavu ttf-freefont

# 将应用程序 JAR 文件添加到容器中
COPY target/FindingPetsSys-0.0.1-SNAPSHOT.jar /app/

# 暴露端口
EXPOSE 8080 25 465

# 设置容器入口点
ENTRYPOINT ["java", "-jar", "FindingPetsSys-0.0.1-SNAPSHOT.jar"]