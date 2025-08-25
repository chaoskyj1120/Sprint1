FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle Wrapper 및 관련 파일만 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon dependencies || true

# 전체 프로젝트 복사 후 빌드
COPY . .
RUN ./gradlew clean build --no-daemon --no-parallel -x test

# =============================
# 실행 단계
# =============================
FROM amazoncorretto:17
WORKDIR /app

# 환경 변수
ENV PROJECT_NAME=Discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 빌드 결과 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 포트 노출
EXPOSE 80

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
