# OpenJDK 17 이미지를 베이스로 사용
FROM tomcat:9.0.82-jdk17-temurin-focal

# jar 파일을 복사하여 도커 Tomcat 웹 애플리케이션 폴더로 이동

COPY build/libs/ROOT.jar /usr/local/tomcat/
EXPOSE 8080

#CMD ["java", "-jar", "ROOT.jar"]
ENTRYPOINT ["java","-jar","ROOT.jar"]