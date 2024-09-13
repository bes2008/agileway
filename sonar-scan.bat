REM 使用之前，先将 pom 文件中的 构建目标版本设置为 1.7

set JAVA_HOME=%JAVA_17_HOME%
set Path=%JAVA_HOME%/bin;%Path%
REM mvn clean package -DskipTests
mvn clean package -DskipTests -PstaticAnalyze -Dsonar.login=squ_7cfd830d0fce088249875af9c8f83222f86e8eb6 -Dsonar.projectKey=agileway -Dsonar.projectName=agileway -Dsonar.host.url=http://192.168.137.133:9000