#!/usr/bin/env bash
#modify

REPOSITORY=/home/ubuntu/app/deploy
APP_NAME=itaseki-backend
cd $REPOSITORY

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f $APP_NAME)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]
then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

JAR_NAME=$(ls $REPOSITORY | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/$JAR_NAME

echo "> 파일명: $JAR_NAME"
echo "> 파일경로: $JAR_PATH"

echo "> 새 애플리케이션 배포"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

nohup java -jar -Dspring.config.location=classpath:/,/home/ubuntu/backend-enc/application-s3.properties,/home/ubuntu/backend-enc/application-mysql.properties -Duser.timezone=Asia/Seoul $JAR_NAME >> $REPOSITORY/nohup.out 2>&1 &
